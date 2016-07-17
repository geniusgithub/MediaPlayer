package com.geniusgithub.mediaplayer.browse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.browse.model.ContentManager;
import com.geniusgithub.mediaplayer.browse.proxy.BrowseDMSProxy;
import com.geniusgithub.mediaplayer.browse.ui.BroswerView;
import com.geniusgithub.mediaplayer.dlna.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.model.DMSDeviceBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.dlna.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerActivity;
import com.geniusgithub.mediaplayer.player.picture.PicturePlayerActivity;
import com.geniusgithub.mediaplayer.player.video.VideoPlayerActivity;
import com.geniusgithub.mediaplayer.util.CommonUtil;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.AlwaysLog;

import java.util.ArrayList;
import java.util.List;

public class BrowsePresenter implements  IBaseFragmentPresent, IBrowsePresenter,
                                                IDeviceChangeListener,
                                                BrowseDMSProxy.BrowseRequestCallback{


    private static final String TAG = BrowsePresenter.class.getSimpleName();

    /////////////////////////////////////////////////
    public static interface IBrowseView{
        public void bindView(Context context, View container);
        public void bindPresent(IBrowsePresenter presenter);
        public void showProgress(boolean bShow);
        public void updateDeviceList(List<Device> devices);
        public void switchView(int viewType);
        public void updateItemList(List<MediaItem>  contentItem);
    }

    private IBrowseView mIBrowseView;
    private IBrowseView createBrosweView(){
        return new BroswerView();
    }
    /////////////////////////////////////////////////



    private Context mContext;
    private AllShareProxy mAllShareProxy;
    private DMSDeviceBrocastFactory mBrocastFactory;
    private ContentManager mContentManager;

    private List<MediaItem> mCurItems;
    private final int VIEW_DMS = 0;
    private final int VIEW_CONTENT = 1;
    private int mViewType = VIEW_DMS;

    private Handler mHandler;
    @Override
    public void onAttach(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIBrowseView = createBrosweView();
        mIBrowseView.bindPresent(this);
        mAllShareProxy = AllShareProxy.getInstance(mContext.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dms_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mIBrowseView.bindView(mContext, view);
        mIBrowseView.switchView(VIEW_DMS);

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);

        mContentManager = ContentManager.getInstance();
        mCurItems = new ArrayList<MediaItem>();
        mHandler = new Handler();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mBrocastFactory.unRegisterListener();
        mContentManager.clear();
    }


    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {

        updateDeviceList();
        if (mViewType != VIEW_DMS && isSelDeviceChange){
            mIBrowseView.switchView(VIEW_DMS);
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
                    mIBrowseView.switchView(VIEW_CONTENT);
                }
            }
        });
    }

    ///////////////////////////////////////////////// presenter callback begin
    @Override
    public void onSearch() {
        mAllShareProxy.startSearch();
    }

    @Override
    public void onReset() {
        mAllShareProxy.resetSearch();
    }

    @Override
    public void onExit() {
        mAllShareProxy.exitSearch();
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
    ///////////////////////////////////////////////// presenter callback end

    private void updateDeviceList(){
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        mIBrowseView.updateDeviceList(list);
    }


    private void updateItemList(List<MediaItem> list)
    {
        mCurItems = list;
        mIBrowseView.updateItemList(list);
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
        intent.putExtra(MusicPlayerActivity.PLAY_INDEX, index);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }

    private void goVideoPlayerActivity(int position, MediaItem item){

        MediaManager.getInstance().setVideoList(mCurItems);

        Intent intent = new Intent();
        intent.setClass(mContext, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.PLAY_INDEX, position);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }


    private void goPicturePlayerActivity(int position, MediaItem item){

        MediaManager.getInstance().setPictureList(mCurItems);

        Intent intent = new Intent();
        intent.setClass(mContext, PicturePlayerActivity.class);
        intent.putExtra(PicturePlayerActivity.PLAY_INDEX, position);
        MediaItemFactory.putItemToIntent(item, intent);
        mContext.startActivity(intent);
    }
}
