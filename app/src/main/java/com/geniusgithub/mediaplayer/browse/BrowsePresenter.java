package com.geniusgithub.mediaplayer.browse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.DialogFactory;
import com.geniusgithub.mediaplayer.dlna.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.model.DMSDeviceBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.dlna.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.view.MusicPlayerActivity;
import com.geniusgithub.mediaplayer.player.picture.PicturePlayerPresenter;
import com.geniusgithub.mediaplayer.player.picture.View.PicturePlayerActivity;
import com.geniusgithub.mediaplayer.player.video.VideoPlayePresenter;
import com.geniusgithub.mediaplayer.player.video.view.VideoPlayerActivity;
import com.geniusgithub.mediaplayer.util.CommonUtil;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.AlwaysLog;

import java.util.ArrayList;
import java.util.List;

public class BrowsePresenter implements BrowseContract.IPresenter, IDeviceChangeListener,
                                        BrowseDMSProxy.BrowseRequestCallback{


    private static final String TAG = BrowsePresenter.class.getSimpleName();

    private Context mContext;
    private BrowseContract.IView mIBrowseView;

    private AllShareProxy mAllShareProxy;
    private DMSDeviceBrocastFactory mBrocastFactory;
    private ContentManager mContentManager;

    private List<MediaItem> mCurItems;
    private final int VIEW_DMS = 0;
    private final int VIEW_CONTENT = 1;
    private int mViewType = VIEW_DMS;

    private Handler mHandler;

    public BrowsePresenter(){
        mContext = AllShareApplication.getInstance();
    }



    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(BrowseContract.IView view) {
        mIBrowseView = view;
        mIBrowseView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }



    @Override
    public void enterDevice(Device device) {
        mAllShareProxy.setDMSSelectedDevice(device);
        requestDirectory();
    }


    @Override
    public void browseItem(int index, MediaItem item) {
        if (UpnpUtil.isAudioItem(item)) {
            goMusicPlayerActivity(index, item);
        }else if (UpnpUtil.isVideoItem(item)){
            goVideoPlayerActivity(index, item);
        }else if (UpnpUtil.isPictureItem(item)){
            goPicturePlayerActivity(index, item);
        }else{
            BrowseDMSProxy.syncGetItems(mContext, item.getStringid(), this);
            mIBrowseView.showProgress(true);
        }
    }
    ///////////////////////////////////////     presenter callback end




    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        updateDeviceList();
        if (mViewType != VIEW_DMS && isSelDeviceChange){
            mContentManager.clear();
            switchView(VIEW_DMS);
        }
    }


    @Override
    public void onGetItems(final List<MediaItem> list) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mIBrowseView.showProgress(false);
                if (list == null){
                    CommonUtil.showToask(mContext, "can't get folder...");
                    return ;
                }

                AlwaysLog.i(TAG, "onGetItems list.size = " + list.size());
                mContentManager.pushListItem(list);
                updateItemList(list);

                if (mViewType == VIEW_DMS){
                    switchView(VIEW_CONTENT);
                }
            }
        });
    }


    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context) {
        mContext = context;
        mAllShareProxy = AllShareProxy.getInstance(mContext);

        mContentManager = ContentManager.getInstance();
        mCurItems = new ArrayList<MediaItem>();
        mHandler = new Handler();

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);

        updateDeviceList();
        switchView(VIEW_DMS);
    }




    public void onUiDestroy() {
        mBrocastFactory.unRegisterListener();
        mContentManager.clear();
        DialogFactory.releaseDialogResource();
    }

    public boolean onBackPressed(){
        switch (mViewType){
            case VIEW_DMS:
                break;
            case VIEW_CONTENT:{
                mContentManager.popListItem();
                List<MediaItem> list = mContentManager.peekListItem();
                if (list == null){
                    switchView(VIEW_DMS);
                }else{
                    updateItemList(list);
                }
            }
            return true;
        }

        return false;
    }
    ///////////////////////////////////////     lifecycle or ui operator end





    private void updateDeviceList(){
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        mIBrowseView.updateDeviceList(list);
    }


    private void updateItemList(List<MediaItem> list)
    {
        mCurItems = list;
        mIBrowseView.updateItemList(list);
    }

    private void switchView(int viewType){

        switch (viewType){
            case VIEW_DMS:
                mIBrowseView.showDeviceList(true);
                mIBrowseView.showItemList(false);
                break;
            case VIEW_CONTENT:
                mIBrowseView.showDeviceList(false);
                mIBrowseView.showItemList(true);
                break;
        }
        mViewType = viewType;
        updateToolTitle(mViewType);
    }


    private void updateToolTitle(int viewType){
        String title = "DLNA";
        if (viewType == VIEW_CONTENT){
            Device device = mAllShareProxy.getDMSSelectedDevice();
            if (device != null){
                title = device.getFriendlyName();
            }
        }

        mIBrowseView.updateToolTitle(title);
    }

    private void requestDirectory()
    {
        Device selDevice = mAllShareProxy.getDMSSelectedDevice();
        if (selDevice == null){
            CommonUtil.showToask(mContext, "can't select any devices...");
            return ;
        }

        BrowseDMSProxy.syncGetDirectory(mContext, this);
        mIBrowseView.showProgress(true);
    }

    private void goMusicPlayerActivity(int index, MediaItem item){

        MediaManager.getInstance().setMusicList(mCurItems);

        Intent intent = new Intent();
        intent.setClass(mContext, MusicPlayerActivity.class);
        intent.putExtra(MusicPlayerPresenter.PLAY_INDEX, index);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }

    private void goVideoPlayerActivity(int position, MediaItem item){

        MediaManager.getInstance().setVideoList(mCurItems);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/* ";
        Uri uri = Uri.parse(item.getRes());
        intent.setDataAndType(uri, type);


        try {
            mContext.startActivity(intent);
        }catch (Exception exception){
            Intent intent2 = new Intent();
            intent2.setClass(mContext, VideoPlayerActivity.class);
            intent2.putExtra(VideoPlayePresenter.PLAY_INDEX, position);
            MediaItemFactory.putItemToIntent(item, intent2);
            mContext.startActivity(intent2);
        }

    }


    private void goPicturePlayerActivity(int position, MediaItem item){

        MediaManager.getInstance().setPictureList(mCurItems);

        Intent intent = new Intent();
        intent.setClass(mContext, PicturePlayerActivity.class);
        intent.putExtra(PicturePlayerPresenter.PLAY_INDEX, position);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }


}
