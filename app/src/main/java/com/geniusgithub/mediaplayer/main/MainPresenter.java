package com.geniusgithub.mediaplayer.main;

import android.content.Context;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.dlna.base.IControlPointState;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.model.ControlStatusChangeBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.model.IStatusChangeListener;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;

public class MainPresenter implements  MainContract.IPresenter, IStatusChangeListener{


    private MainContract.IView mView;
    private AllShareProxy mAllShareProxy;
    private ControlStatusChangeBrocastFactory mBrocastFactory;
    private Context mContext;

    public MainPresenter(){
        mContext = AllShareApplication.getInstance();

    }


    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(MainContract.IView view) {
        mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }

    @Override
    public void onStart() {
        mAllShareProxy.startSearch();
    }

    @Override
    public void onRestart() {
        mAllShareProxy.resetSearch();
    }

    @Override
    public void onStop() {
        mAllShareProxy.exitSearch();
    }

    @Override
    public void onExit() {
        onStop();
        AllShareApplication.getInstance().delayToExit();
    }
    ///////////////////////////////////////     presenter callback end



    @Override
    public void onStatusChange(int status) {
        updateLocalAddress(status);
    }



    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onCreate(Context context){
        mContext = context;
        mAllShareProxy = AllShareProxy.getInstance(mContext.getApplicationContext());
        mBrocastFactory = new ControlStatusChangeBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);
        AllShareApplication.getInstance().setEnterFlag(true);
        updateLocalAddress();
    }

    public void onDestroy(){
        mBrocastFactory.unRegisterListener();
        mBrocastFactory = null;
    }
    ///////////////////////////////////////     lifecycle or ui operator end



    public void updateLocalAddress() {
        updateLocalAddress(mAllShareProxy.getControlStatus());
    }

    public void updateLocalAddress(int status) {
        String value = mContext.getResources().getString(R.string.status_stop);
        switch(status){
            case IControlPointState.STATUS_SOTP:
                value = mContext.getResources().getString(R.string.status_stop);
                break;
            case IControlPointState.STATUS_STARTED:
                value = mContext.getResources().getString(R.string.status_started);
                value += "(" + mAllShareProxy.getLocalAddress() + ")";
                break;
            case IControlPointState.STATUS_STARTING:
                value = mContext.getResources().getString(R.string.status_starting);
                break;
        }

        mView.updateLocalAddress(value);
    }
}
