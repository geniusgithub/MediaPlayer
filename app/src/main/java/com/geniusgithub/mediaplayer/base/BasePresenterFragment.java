package com.geniusgithub.mediaplayer.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public abstract  class BasePresenterFragment<T extends IBaseFragmentPresent> extends BaseFragment {

    protected Context mContext;
    protected T mPresenter;

    public abstract T createPresenter();

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
        mPresenter = createPresenter();
        mPresenter.bindFragment(this);
        mPresenter.onAttach(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mPresenter.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return mPresenter.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mPresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPresenter.onDestroy();
    }

    public boolean onBackPressed(){
        return mPresenter.onBackPressed();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mPresenter.dispatchTouchEvent(ev);
    }

    public void onNewIntent(Intent intent){
        mPresenter.onNewIntent(intent);
    }



}
