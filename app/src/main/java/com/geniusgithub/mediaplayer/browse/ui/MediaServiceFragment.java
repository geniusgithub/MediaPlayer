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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.activity.IToolBar;
import com.geniusgithub.mediaplayer.activity.MainFrameActivity;
import com.geniusgithub.mediaplayer.browse.adapter.ContentAdapter;
import com.geniusgithub.mediaplayer.browse.adapter.DeviceAdapter;
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
import org.cybergarage.util.AlwaysLog;

import java.util.ArrayList;
import java.util.List;


public class MediaServiceFragment extends Fragment implements View.OnClickListener,
                                                                IDeviceChangeListener,
                                                                AdapterView.OnItemClickListener ,
                                                                BrowseDMSProxy.BrowseRequestCallback{

    private static final String TAG = MediaServiceFragment.class.getSimpleName();
    private static final CommonLog log = LogFactory.createLog();
    private Context mContext;
    private Button mBtnSearch;
    private Button mBtnReset;
    private Button mBtnExit;

    private ListView mDevListView;
    private ListView mContentListView;
    private DeviceAdapter mDevAdapter;
    private ContentAdapter mContentAdapter;

    private List<MediaItem> mCurItems;
    private AllShareProxy mAllShareProxy;

    private OnContentItemClick mOnContentItemClick;

    private ContentManager mContentManager;
    private DMSDeviceBrocastFactory mBrocastFactory;


    private Handler mHandler;

    private final int VIEW_DMS = 0;
    private final int VIEW_CONTENT = 1;
    private int mViewType = VIEW_DMS;

    private IToolBar mExternToolbar;


    public MediaServiceFragment(IToolBar toolbar) {
        mExternToolbar = toolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dms_layout, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
    }

    private void initView(View view){
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnReset = (Button) view.findViewById(R.id.btn_reset);
        mBtnExit = (Button) view.findViewById(R.id.btn_exit);
        mBtnSearch.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);

        mDevListView = (ListView) view.findViewById(R.id.device_list);
        mDevListView.setOnItemClickListener(this);

        mContentListView = (ListView) view.findViewById(R.id.content_list);
        mOnContentItemClick = new OnContentItemClick();
        mContentListView.setOnItemClickListener(mOnContentItemClick);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading...");

        switchView(VIEW_DMS);
    }

    private void initData(){
        mAllShareProxy = AllShareProxy.getInstance(mContext.getApplicationContext());
        mContentManager = ContentManager.getInstance();

        mDevAdapter = new DeviceAdapter(mContext, new ArrayList<Device>());
        mDevListView.setAdapter(mDevAdapter);

        mCurItems = new ArrayList<MediaItem>();
        mContentAdapter = new ContentAdapter(mContext, mCurItems);
        mContentListView.setAdapter(mContentAdapter);

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);

        AllShareApplication.getInstance().setStatus(true);

        mHandler = new Handler();
    }



    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {

        mBrocastFactory.unRegisterListener();
        mContentManager.clear();

        super.onDestroy();
    }

    private void updateDeviceList(){
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        mDevAdapter.refreshData(list);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_search:
                mAllShareProxy.startSearch();
                break;
            case R.id.btn_reset:
                mAllShareProxy.resetSearch();
                break;
            case R.id.btn_exit:
                mAllShareProxy.exitSearch();
                getActivity().finish();
                break;
        }
    }


    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {

        updateDeviceList();
        if (isSelDeviceChange){
            switchView(VIEW_DMS);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Device device = (Device) parent.getItemAtPosition(position);
        mAllShareProxy.setDMSSelectedDevice(device);
        requestDirectory();
    }


    private class OnContentItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem item = (MediaItem) parent.getItemAtPosition(position);
            log.e("item = \n" + item.getShowString());

            if (UpnpUtil.isAudioItem(item)) {
                goMusicPlayerActivity(position, item);
            }else if (UpnpUtil.isVideoItem(item)){
                goVideoPlayerActivity(position, item);
            }else if (UpnpUtil.isPictureItem(item)){
                goPicturePlayerActivity(position, item);
            }else{
                BrowseDMSProxy.syncGetItems(mContext, item.getStringid(), MediaServiceFragment.this);
                showProgress(true);
            }

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

                AlwaysLog.i(TAG, "onGetItems list.size = " + list.size());
                mContentManager.pushListItem(list);
                setContentlist(list);

                if (mViewType == VIEW_DMS){
                    switchView(VIEW_CONTENT);
                }

            }
        });
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

    private ProgressDialog mProgressDialog;
    private void showProgress(boolean bShow)
    {
        mProgressDialog.dismiss();
        if (bShow){
            mProgressDialog.show();
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
        switch (mViewType){
            case VIEW_DMS:
                break;
            case VIEW_CONTENT:{
                    mContentManager.popListItem();
                    List<MediaItem> list = mContentManager.peekListItem();
                    if (list == null){
                        switchView(VIEW_DMS);
                    }else{
                        setContentlist(list);
                    }
                }
                return true;
        }

        return false;
    }

    private void switchView(int viewType){
        switch (viewType){
            case VIEW_DMS:
                mDevListView.setVisibility(View.VISIBLE);
                mContentListView.setVisibility(View.GONE);
                updateToolTitle("DLNA");
                break;
            case VIEW_CONTENT:
                mDevListView.setVisibility(View.GONE);
                mContentListView.setVisibility(View.VISIBLE);
                Device device  = mAllShareProxy.getDMSSelectedDevice();
                if (device == null)
                {
                    updateToolTitle("no select device");
                }else{
                    updateToolTitle(device.getFriendlyName());
                }
                break;
        }
        mViewType = viewType;
    }


    private void updateToolTitle(String title) {
        if (mExternToolbar != null) {
            mExternToolbar.updateToolTitle(title);
        }
    }

}
