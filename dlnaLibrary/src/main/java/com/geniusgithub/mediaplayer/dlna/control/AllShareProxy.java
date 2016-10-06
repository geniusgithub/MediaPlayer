package com.geniusgithub.mediaplayer.dlna.control;


import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.base.DlnaEngineObserver;
import com.geniusgithub.mediaplayer.dlna.control.model.AbstractMediaMng;
import com.geniusgithub.mediaplayer.dlna.control.model.ControlStatusChangeBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaRenderMng;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaServerMng;
import com.geniusgithub.mediaplayer.dlna.control.base.IControlOperator;
import com.geniusgithub.mediaplayer.dlna.control.model.IControlPointState;
import com.geniusgithub.mediaplayer.dlna.control.base.IDeviceOperator;
import com.geniusgithub.mediaplayer.dlna.util.UpnpUtil;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.AlwaysLog;
import org.cybergarage.util.Mutex;

import java.util.List;


public class AllShareProxy implements IControlOperator,
											DlnaEngineObserver{

	private final static String TAG = AllShareProxy.class.getSimpleName();
	
	private static AllShareProxy instance;
	private Context mContext;


	private ControlPointImpl mControlPoint;
	private int mContropPointStatus = IControlPointState.STATUS_SOTP;
	private Mutex controlLock;


	
	private AllShareProxy(Context context) {
		mContext = context;
		dmsMediaMng = new MediaServerMng(context);
		dmrMediaMng = new MediaRenderMng(context);

		controlLock = new Mutex();
		mDeviceOperator = new DeviceOperator();
	}

	public static synchronized AllShareProxy getInstance(Context context) {
		if (instance == null){
			instance  = new AllShareProxy(context);
		}
		return instance;
	}


	public void startSearch(){
		Intent intent = new Intent(ControlService.SEARCH_DEVICES);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
	}
	
	public void resetSearch(){
		Intent intent = new Intent(ControlService.RESET_SEARCH_DEVICES);
		intent.setPackage(mContext.getPackageName());
		mContext.startService(intent);
		mDeviceOperator.clearDevice();
	}
	
	public void exitSearch(){
		mContext.stopService(new Intent(mContext, ControlService.class));
		mDeviceOperator.clearDevice();
	}

	public String getLocalAddress(){
		if (mControlPoint != null){
			return mControlPoint.getLocalAddress();
		}
		return "";
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
	public void setControlPoint(ControlPointImpl controlPoint) {
		mControlPoint = controlPoint;
	}

	public interface IEngineStatusCallback {
		public void onEngineCreate();
		public void onEngineDestory();
		public void onEngineRestart();
	}

	private IEngineStatusCallback mEngineStatusCallback;
	public void setEngineStatusCallback(IEngineStatusCallback callback){
		mEngineStatusCallback = callback;
	}

	@Override
	public void initEngine() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineCreate();
		}
	}

	@Override
	public boolean startEngine() {
		return false;
	}

	@Override
	public boolean stopEngine() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineDestory();
		}
		return true;
	}

	@Override
	public boolean restartEngine() {
		if (mEngineStatusCallback != null){
			mEngineStatusCallback.onEngineRestart();
		}
		return true;
	}



	private DeviceOperator mDeviceOperator;
	private AbstractMediaMng dmsMediaMng;
	private AbstractMediaMng dmrMediaMng;
	public DeviceOperator getmDeviceOperator(){
		return mDeviceOperator;
	}

	public class DeviceOperator implements IDeviceOperator, IDeviceOperator.IDMSDeviceOperator, IDeviceOperator.IDMRDeviceOperator{

		@Override
		public void addDevice(Device d) {
			if (UpnpUtil.isMediaServerDevice(d)){
				AlwaysLog.i(TAG, "mediaserver addDevice dev = " + d.getUDN());
				dmsMediaMng.addDevice(d);
			}else if (UpnpUtil.isMediaRenderDevice(d)){
				AlwaysLog.i(TAG, "mediarender addDevice dev = " + d.getUDN());
				dmrMediaMng.addDevice(d);
			}
		}

		@Override
		public void removeDevice(Device d) {
			if (UpnpUtil.isMediaServerDevice(d)){
				AlwaysLog.i(TAG, "mediaserver removeDevice dev = " + d.getUDN());
				dmsMediaMng.removeDevice(d);
			}else if (UpnpUtil.isMediaRenderDevice(d)){
				AlwaysLog.i(TAG, "mediarender removeDevice dev = " + d.getUDN());
				dmrMediaMng.removeDevice(d);
			}
		}

		@Override
		public void clearDevice() {
			AlwaysLog.i(TAG, "clearDevice dev ");
			dmsMediaMng.clear();
			dmrMediaMng.clear();
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
		public List<Device> getDMRDeviceList() {
			return dmrMediaMng.getDeviceList();
		}

		@Override
		public Device getDMRSelectedDevice1() {
			return dmrMediaMng.getSelectedDevice();
		}

		@Override
		public void setDMRSelectedDevice(Device selectedDevice) {
			dmrMediaMng.setSelectedDevice(selectedDevice);
		}
	}



}
