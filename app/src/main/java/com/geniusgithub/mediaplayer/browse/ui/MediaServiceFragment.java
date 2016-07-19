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
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.browse.IBaseFragmentPresent;
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


public class MediaServiceFragment extends Fragment {

    private static final String TAG = MediaServiceFragment.class.getSimpleName();

    private Context mContext;
    private IToolBar mExternToolbar;

    private IBaseFragmentPresent mBrwsePresenter;

    public MediaServiceFragment(IToolBar toolbar) {
        mExternToolbar = toolbar;
        mBrwsePresenter = createPresenter();
    }



    public IBaseFragmentPresent createPresenter(){
        return new BrowsePresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mBrwsePresenter.onAttach(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrwsePresenter.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return mBrwsePresenter.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBrwsePresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBrwsePresenter.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        mBrwsePresenter.onPause();
    }

    @Override
    public void onDestroy() {

        mBrwsePresenter.onDestroy();

        super.onDestroy();
    }


    public boolean back(){
        return mBrwsePresenter.onBackPressed();
    }

}
