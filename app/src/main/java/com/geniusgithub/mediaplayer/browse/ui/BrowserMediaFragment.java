package com.geniusgithub.mediaplayer.browse.ui;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.activity.BaseFragment;
import com.geniusgithub.mediaplayer.activity.IToolBar;
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.browse.IBaseFragmentPresent;



public class BrowserMediaFragment extends BaseFragment {

    private static final String TAG = BrowserMediaFragment.class.getSimpleName();

    private Context mContext;
    private IToolBar mExternToolbar;

    private IBaseFragmentPresent mBrwsePresenter;

    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }


    public IBaseFragmentPresent createPresenter(){
        return new BrowsePresenter();
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
        mBrwsePresenter = createPresenter();
        mBrwsePresenter.bindFragment(this);
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



    public void onViewSwitch(String title){
        if (mExternToolbar != null){
            mExternToolbar.updateToolTitle(title);
        }
    }

}
