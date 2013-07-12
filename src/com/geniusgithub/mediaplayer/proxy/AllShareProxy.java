package com.geniusgithub.mediaplayer.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;
import android.content.Intent;


public class AllShareProxy{
//public class AllShareProxy implements IDeviceOperator, 
//								IDeviceOperator.IDMRDeviceOperator,
//								IDeviceOperator.IDMSDeviceOperator{

	private static final CommonLog log = LogFactory.createLog();

	
	private static  AllShareProxy instance;
	private Context mContext;
	
//	private AbstractMediaMng dmsMediaMng;
//	private AbstractMediaMng dmrMediaMng;
	
	private AllShareProxy(Context context) {
		mContext = context;
//		dmsMediaMng = new MediaServerMng(context);
//		dmrMediaMng = new MediaRenderMng(context);
	}

	public static synchronized AllShareProxy getInstance(Context context) {
		if (instance == null){
			instance  = new AllShareProxy(context);
		}
		return instance;
	}

	
//	public void startSearch(){
//		mContext.startService(new Intent(DlnaService.SEARCH_DEVICES));
//	}
//	
//	public void resetSearch(){
//
//		mContext.startService(new Intent(DlnaService.RESET_SEARCH_DEVICES));
//		clearDevice();
//	}
//	
//	public void exitSearch(){
//
//		mContext.stopService(new Intent(mContext, DlnaService.class));
//		clearDevice();
//	}
//
//	@Override
//	public List<Device> getDMSDeviceList() {
//		return dmsMediaMng.getDeviceList();
//	}
//
//	@Override
//	public Device getDMSSelectedDevice1() {
//		return dmsMediaMng.getSelectedDevice();
//	}
//
//	@Override
//	public void setDMSSelectedDevice(Device selectedDevice) {
//		dmsMediaMng.setSelectedDevice(selectedDevice);
//	}
//
//	@Override
//	public List<Device> getDMRDeviceList() {
//		return dmrMediaMng.getDeviceList();
//	}
//
//	@Override
//	public Device getDMRSelectedDevice1() {
//		return dmrMediaMng.getSelectedDevice();
//	}
//
//	@Override
//	public void setDMRSelectedDevice(Device selectedDevice) {
//		dmrMediaMng.setSelectedDevice(selectedDevice);
//	}
//
//	@Override
//	public void addDevice(Device d) {
//		if (UpnpUtil.isValidRenderDevice(d)){
//			dmrMediaMng.addDevice(d);
//		}else if (UpnpUtil.isMediaServerDevice(d)){
//			dmsMediaMng.addDevice(d);
//		}
//	}
//
//	@Override
//	public void removeDevice(Device d) {
//		if (UpnpUtil.isMediaRenderDevice(d)){
//			dmrMediaMng.removeDevice(d);
//		}else if (UpnpUtil.isMediaServerDevice(d)){
//			dmsMediaMng.removeDevice(d);
//		}
//	}
//
//	@Override
//	public void clearDevice() {
//		dmrMediaMng.clear();
//		dmsMediaMng.clear();
//	}
	

}
