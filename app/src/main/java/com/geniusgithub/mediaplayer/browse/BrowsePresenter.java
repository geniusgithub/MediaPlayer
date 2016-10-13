package com.geniusgithub.mediaplayer.browse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.common.util.CommonUtil;
import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.browse.BrowseContract.IPresenter;
import com.geniusgithub.mediaplayer.component.CacheManager;
import com.geniusgithub.mediaplayer.component.DialogFactory;
import com.geniusgithub.mediaplayer.component.ImageLoader;
import com.geniusgithub.mediaplayer.component.MediaItemFactory;
import com.geniusgithub.mediaplayer.component.MediaManager;
import com.geniusgithub.mediaplayer.dlna.control.model.DMSDeviceBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.control.base.IDMSDeviceChangeListener;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.control.AllShareProxy;
import com.geniusgithub.mediaplayer.dlna.control.browsecontrol.BrowseControllerProxy;
import com.geniusgithub.mediaplayer.dlna.util.UpnpUtil;
import com.geniusgithub.mediaplayer.player.music.LocalMusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.view.MusicPlayerActivity;
import com.geniusgithub.mediaplayer.player.photo.PhotoBrowsePresenter;
import com.geniusgithub.mediaplayer.player.photo.view.PhotoBrowseActivity;
import com.geniusgithub.mediaplayer.player.video.VideoPlayePresenter;
import com.geniusgithub.mediaplayer.player.video.view.VideoPlayerActivity;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class BrowsePresenter implements IPresenter, IDMSDeviceChangeListener,
                                        BrowseControllerProxy.BrowseRequestCallback{


    private static final String TAG = BrowsePresenter.class.getSimpleName();

    private Context mContext;
    private BrowseContract.IView mIBrowseView;

    private AllShareProxy mAllShareProxy;
    private DMSDeviceBrocastFactory mBrocastFactory;
    private ContentManager mContentManager;
    private Device mCurDevice;
    private BrowseControllerProxy.BrowseContentAsnyTask mRequestTask;

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
        setCurDevice(device);
        mRequestTask =  BrowseControllerProxy.asyncBrowseDirectory(mContext, device, this);
    }


    @Override
    public void browseItem(int index, MediaItem item) {
        if (UpnpUtil.isAudioItem(item)) {
            goMusicPlayerActivity(index, item);
        }else if (UpnpUtil.isVideoItem(item)){
            goVideoPlayerActivity(index, item);
        }else if (UpnpUtil.isPictureItem(item)){
            goPhotoPlayerActivity(index, item);
        }else{
            mRequestTask = BrowseControllerProxy.asyncBrowseItems(mContext, mCurDevice, item.getStringid(), this);
        }
    }

    @Override
    public void cancelTask() {
        AlwaysLog.i(TAG, "cancelTask");
        if (mRequestTask != null){
            mRequestTask.cancel(true);
            mRequestTask = null;
        }
    }

    ///////////////////////////////////////     presenter callback end




    @Override
    public void onDMSDeviceChange(boolean isSelDeviceChange) {
        updateDeviceList();
        if (mViewType != VIEW_DMS && isSelDeviceChange){
            mContentManager.clear();
            ImageLoader.clearTask(mContext);
           setCurDevice(null);
            switchView(VIEW_DMS);
        }
    }

    @Override
    public void onRequestBegin() {
        mIBrowseView.showProgress(true);
    }


    @Override
    public void onRequestCancel() {
        AlwaysLog.i(TAG, "onRequestCancel");
    }

    @Override
    public void onRequestSuccess(final List<MediaItem> list) {
        mIBrowseView.showProgress(false);
        mRequestTask = null;

        ImageLoader.clearTask(mContext);
        mContentManager.pushListItem(list);
        updateItemList(list);

        if (mViewType == VIEW_DMS){
            switchView(VIEW_CONTENT);
        }
    }

    @Override
    public void onRequestFail() {
        mIBrowseView.showProgress(false);
        mRequestTask = null;
        CommonUtil.showToask(mContext, "can't get folder...");
    }


    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context) {
        mContext = context;
        mAllShareProxy = AllShareProxy.getInstance(mContext);

        mContentManager = ContentManager.getInstance();
        mCurItems = new ArrayList<MediaItem>();
        mCurDevice = mAllShareProxy.getmDeviceOperator().getDMSSelectedDevice();
        mHandler = new Handler();

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);

        updateDeviceList();
        switchView(VIEW_DMS);
    }




    public void onUiDestroy() {
        mBrocastFactory.unRegisterListener();
        mContentManager.clear();
        CacheManager.getInstance().clearMemoryCache();
        DialogFactory.releaseDialogResource();
    }

    public boolean onBackPressed(){
        switch (mViewType){
            case VIEW_DMS:
                break;
            case VIEW_CONTENT:{
                mContentManager.popListItem();
                List<MediaItem> list = mContentManager.peekListItem();
                ImageLoader.clearTask(mContext);
                if (list == null){
                    setCurDevice(null);
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

    private void setCurDevice(Device device){
        mCurDevice = device;
        AllShareProxy.getInstance(mContext).getmDeviceOperator().setDMSSelectedDevice(mCurDevice);
    }



    private void updateDeviceList(){
        List<Device> list = mAllShareProxy.getmDeviceOperator().getDMSDeviceList();
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
            Device device = mAllShareProxy.getmDeviceOperator().getDMSSelectedDevice();
            if (device != null){
                title = device.getFriendlyName();
            }
        }

        mIBrowseView.updateToolTitle(title);
    }


    private void goMusicPlayerActivity(int index, MediaItem item){

        index = MediaManager.getInstance().filterMusicList(mCurItems, index);

        Intent intent = new Intent();
        intent.setClass(mContext, MusicPlayerActivity.class);
        intent.putExtra(LocalMusicPlayerPresenter.PLAY_INDEX, index);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }

    private void goVideoPlayerActivity(int position, MediaItem item){

        position = MediaManager.getInstance().filterVideoList(mCurItems, position);



        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/*";
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


    private void goPhotoPlayerActivity(int position, MediaItem item){

        position = MediaManager.getInstance().filterPictureList(mCurItems, position);

        Intent intent = new Intent();
        intent.setClass(mContext, PhotoBrowseActivity.class);
        intent.putExtra(PhotoBrowsePresenter.PLAY_INDEX, position);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }


}
