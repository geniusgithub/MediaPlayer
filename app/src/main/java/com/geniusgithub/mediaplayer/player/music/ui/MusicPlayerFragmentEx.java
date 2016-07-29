package com.geniusgithub.mediaplayer.player.music.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.browse.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.player.music.presenter.MusicPlayerPresenter;

public class MusicPlayerFragmentEx extends Fragment {


    private Context mContext;
    private IBaseFragmentPresent mMusicPlayerPresenter;

    public MusicPlayerFragmentEx() {
        mMusicPlayerPresenter = createPresenter();
        mMusicPlayerPresenter.bindFragment(this);
    }

    public IBaseFragmentPresent createPresenter(){
        return new MusicPlayerPresenter();
    }


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        mContext = context;

        mMusicPlayerPresenter.onAttach(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicPlayerPresenter.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return mMusicPlayerPresenter.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMusicPlayerPresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMusicPlayerPresenter.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        mMusicPlayerPresenter.onPause();
    }

    @Override
    public void onDestroy() {

        mMusicPlayerPresenter.onDestroy();

        super.onDestroy();
    }

}
