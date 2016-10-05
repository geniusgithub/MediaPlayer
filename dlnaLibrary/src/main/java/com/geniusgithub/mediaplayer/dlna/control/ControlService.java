package com.geniusgithub.mediaplayer.dlna.control;

import android.util.Log;

import com.geniusgithub.mediaplayer.dlna.base.DlnaEngineObserver;
import com.geniusgithub.mediaplayer.dlna.base.DlnaService;
import com.geniusgithub.mediaplayer.dlna.control.model.IControlPointState;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;

public class ControlService extends DlnaService implements DeviceChangeListener, ControlCenterWorkThread.ISearchDeviceListener {

    private static final String TAG = ControlService.class.getSimpleName();

    public static final String SEARCH_DEVICES = "com.geniusgithub.allshare.search_device";
    public static final String RESET_SEARCH_DEVICES = "com.geniusgithub.allshare.reset_search_device";


    private ControlPointImpl mControlPoint;
    private  ControlCenterWorkThread mCenterWorkThread;
    private AllShareProxy mAllShareProxy;


    @Override
    public DlnaEngineObserver createObserver() {
        return new ControlEngineObserver();
    }

    @Override
    public String getStartAction() {
        return SEARCH_DEVICES;
    }

    @Override
    public String getRestartAction() {
        return RESET_SEARCH_DEVICES;
    }

    @Override
    public void deviceAdded(Device dev) {
        mAllShareProxy.getmDeviceOperator().addDevice(dev);
    }

    @Override
    public void deviceRemoved(Device dev) {
        mAllShareProxy.getmDeviceOperator().removeDevice(dev);
    }

    @Override
    public void onSearchComplete(boolean searchSuccess) {

    }

    @Override
    public void onStartComplete(boolean startSuccess) {
        mControlPoint.flushLocalAddress();
        //	sendStartDeviceEventBrocast(this, startSuccess);
        Log.i(TAG, "onStartComplete startSuccess = " + startSuccess);
        if (startSuccess){
            mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTED);
        }else{

        }
    }

    @Override
    public void onStopComplete() {
        Log.i(TAG, "onStopComplete");
        mAllShareProxy.setControlStauts(IControlPointState.STATUS_SOTP);
    }

    private void init(){
        mAllShareProxy = AllShareProxy.getInstance(this);

        mControlPoint = new ControlPointImpl();
        mAllShareProxy.setControlPoint(mControlPoint);
        mControlPoint.addDeviceChangeListener(this);

        mCenterWorkThread = new ControlCenterWorkThread(this, mControlPoint);
        mCenterWorkThread.setSearchListener(this);

    }


    private void unInit(){
        mAllShareProxy.setControlPoint(null);
    }


    private void awakeWorkThread(){

        if (mCenterWorkThread.isAlive()){
            mCenterWorkThread.awakeThread();
        }else{
            mCenterWorkThread.start();
        }
    }


    private void exitWorkThread(){
        if (mCenterWorkThread != null && mCenterWorkThread.isAlive()){
            mCenterWorkThread.exit();
            long time1 = System.currentTimeMillis();
            while(mCenterWorkThread.isAlive()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long time2 = System.currentTimeMillis();
            Log.d(TAG, "exitCenterWorkThread cost time:" + (time2 - time1));
            mCenterWorkThread = null;
        }
    }


    private class ControlEngineObserver implements DlnaEngineObserver {


        @Override
        public void initEngine() {
            Log.i(TAG, "initEngine");
            init();
            mAllShareProxy.initEngine();
        }

        @Override
        public boolean startEngine() {
            Log.i(TAG, "startEngine");
            if (mAllShareProxy.getControlStatus() != IControlPointState.STATUS_STARTED){
                mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTING);
            }
            awakeWorkThread();
            mAllShareProxy.startEngine();
            return true;
        }

        @Override
        public boolean stopEngine() {
            Log.i(TAG, "stopEngine");
            exitWorkThread();
            mAllShareProxy.stopEngine();
            return true;
        }

        @Override
        public boolean restartEngine() {
            Log.i(TAG, "restartEngine");
            mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTING);
            mCenterWorkThread.setCompleteFlag(false);
            awakeWorkThread();

            mAllShareProxy.restartEngine();
            return true;
        }

    }

}
