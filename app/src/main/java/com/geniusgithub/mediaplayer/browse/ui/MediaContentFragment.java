package com.geniusgithub.mediaplayer.browse.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.browse.adapter.ContentAdapter;
import com.geniusgithub.mediaplayer.browse.model.ContentManager;
import com.geniusgithub.mediaplayer.browse.proxy.BrowseDMSProxy;
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
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class MediaContentFragment extends Fragment implements AdapterView.OnItemClickListener,
                                                                IDeviceChangeListener,
                                                                 BrowseDMSProxy.BrowseRequestCallback{
    private static final CommonLog log = LogFactory.createLog();

    private Context mContext;
    private ListView mContentListView;

    private ContentAdapter mContentAdapter;
    private AllShareProxy mAllShareProxy;
    private ContentManager mContentManager;

    private List<MediaItem> mCurItems;
    private DMSDeviceBrocastFactory mBrocastFactory;

    private Handler mHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_layout, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();


    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        mContentManager.clear();
        mBrocastFactory.unRegisterListener();

        super.onDestroy();
    }


    private void initView(View view)
    {

        mContentListView = (ListView) view.findViewById(R.id.content_list);
        mContentListView.setOnItemClickListener(this);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading...");
    }

    private void initData()
    {
        mAllShareProxy = AllShareProxy.getInstance(mContext.getApplicationContext());
        mContentManager = ContentManager.getInstance();


        mCurItems = new ArrayList<MediaItem>();
        mContentAdapter = new ContentAdapter(mContext, mCurItems);
        mContentListView.setAdapter(mContentAdapter);

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mHandler = new Handler();
        mHandler.postDelayed(new RequestDirectoryRunnable(), 100);

        mBrocastFactory.registerListener(this);
    }


    private void requestDirectory()
    {
        Device selDevice = mAllShareProxy.getDMSSelectedDevice();
        if (selDevice == null){
            CommonUtil.showToask(mContext, "can't select any devices...");
            getActivity().finish();
            return ;
        }

        BrowseDMSProxy.syncGetDirectory(mContext, this);
        showProgress(true);
    }

    class RequestDirectoryRunnable implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            requestDirectory();
        }

    }



    private void setContentlist(List<MediaItem> list)
    {
        mCurItems = list;
        if (list == null){
            mContentAdapter.clear();
        }else{
            mContentAdapter.refreshData(list);
        }
    }


    private ProgressDialog mProgressDialog;
    private void showProgress(boolean bShow)
    {
        mProgressDialog.dismiss();
        if (bShow){
            mProgressDialog.show();
        }

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

    public boolean back(){
        mContentManager.popListItem();
        List<MediaItem> list = mContentManager.peekListItem();
        if (list == null){
            return false;
        }else{
            setContentlist(list);
            return true;
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        MediaItem item = (MediaItem) parent.getItemAtPosition(position);
        log.e("item = \n" + item.getShowString());

        if (UpnpUtil.isAudioItem(item)) {
            goMusicPlayerActivity(position, item);
        }else if (UpnpUtil.isVideoItem(item)){
            goVideoPlayerActivity(position, item);
        }else if (UpnpUtil.isPictureItem(item)){
            goPicturePlayerActivity(position, item);
        }else{
            BrowseDMSProxy.syncGetItems(mContext, item.getStringid(), this);
            showProgress(true);
        }

    }

    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        // TODO Auto-generated method stub
        if (isSelDeviceChange){
            CommonUtil.showToask(mContext, "current device has been drop...");
            getActivity().finish();
        }
    }

    @Override
    public void onGetItems(final List<MediaItem> list) {
        // TODO Auto-generated method stub
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                showProgress(false);
                if (list == null){
                    CommonUtil.showToask(mContext, "can't get folder...");
                    return ;
                }

                mContentManager.pushListItem(list);
                setContentlist(list);

            }
        });
    }

}
