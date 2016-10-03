package com.geniusgithub.mediaplayer.dlna.proxy;


import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.center.ControlPointImpl;
import com.geniusgithub.mediaplayer.dlna.base.IControlPointState;
import com.geniusgithub.mediaplayer.dlna.util.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.center.DlnaService;
import com.geniusgithub.mediaplayer.dlna.base.IEngineStatusCallback;
import com.geniusgithub.mediaplayer.dlna.model.AbstractMediaMng;
import com.geniusgithub.mediaplayer.dlna.model.ControlStatusChangeBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaServerMng;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.AlwaysLog;
import org.cybergarage.util.Mutex;

import java.util.List;


public class AllShareProxy implements IDeviceOperator,
										IDeviceOperator.IDMSDeviceOperator,
		IControlOperator,
		IEngineStatusCallback{

	private final static String TAG = AllShareProxy.class.getSimpleName();
	
	private static  AllShareProxy instance;
	private Context mContext;


	private ControlPointImpl mControlPoint;
	private int mContropPointStatus = IControlPointState.STATUS_SOTP;
	private Mutex controlLock;

	private IEngineStatusCallback mEngineStatusCallback;
	private AbstractMediaMng dmsMediaMng;
	
	private AllShareProxy(Context context) {
		mContext = context;
		dmsMediaMng = new MediaServerMng(context);
		controlLock = new Mutex();
	}

	public static synchronized AllShareProxy getInstance(Context context) {
		if (instance == null){
			instance  = new AllShareProxy(context);
		}
		return instance;
	}

	public void setEngineStatusCallback(IEngineStatusCallback callback){
		mEngineStatusCallback = callback;
	}

	public void startSearch(){
		Intent intent = new Intent(DlnaService.SEARCH_DEVICES);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
	}
	
	public void resetSearch(){
		Intent intent = new Intent(DlnaService.RESET_SEARCH_DEVICES);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
		clearDevice();
	}
	
	public void exitSearch(){
		mContext.stopService(new Intent(mContext, DlnaService.class));
		clearDevice();
	}
	
	
	@Override
	public void addDevice(Device d) {
	    if (UpnpUtil.isMediaServerDevice(d)){
			AlwaysLog.i(TAG, "addDevice dev = " + d.getUDN());
			dmsMediaMng.addDevice(d);
		}
	}

	@Override
	public void removeDevice(Device d) {
		if (UpnpUtil.isMediaServerDevice(d)){
			AlwaysLog.i(TAG, "removeDevice dev = " + d.getUDN());
			dmsMediaMng.removeDevice(d);
		}
	}

	@Override
	public void clearDevice() {
		AlwaysLog.i(TAG, "clearDevice dev ");
		dmsMediaMng.clear();
	}

	@Override
	public List<Device> getDMSDeviceList() {
		return dmsMediaMng.getDeviceList();
	}


	@Override
	public void setDMSSelectedDevice(Device selectedDevice) {
		dmsMediaMng.setSelectedDevice(selectedDevice);
	}

	@Override
	public Device getDMSSelectedDevice() {
		return dmsMediaMng.getSelectedDevice();
	}

	@Override
	public int getControlStatus() {
		synchronized (controlLock){
			return mContropPointStatus;
		}
	}

	@Override
	public void setControlStauts(int stauts) {
		synchronized (controlLock){
			if (mContropPointStatus != stauts){
				mContropPointStatus = stauts;
				ControlStatusChangeBrocastFactory.sendControlStatusChangeBrocast(mContext, mContropPointStatus);
			}
		}
	}

	@Override
	public ControlPointImpl getControlPoint() {
		return mControlPoint;
	}

	@Override
	public void setControlPoint(ControlPointImpl controlPoint) {
		mControlPoint = controlPoint;
	}



	@Override
	public void onEngineCreate() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineCreate();
		}
	}

	@Override
	public void onEngineDestory() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineDestory();
		}
	}

	@Override
	public void onEngineRestart() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineRestart();
		}
	}


	public String getLocalAddress(){
		if (mControlPoint != null){
			return mControlPoint.getLocalAddress();
		}
		return "";
	}
}
