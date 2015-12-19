package com.geniusgithub.mediaplayer.upnp;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;

public abstract class AbstractMediaMng {

	public static final CommonLog log = LogFactory.createLog();
	protected Context mContext;
	
	protected List<Device> mDeviceList = new ArrayList<Device>();
	protected Device mSelectedDevice;
	
	
	public abstract void sendAddBrocast(Context context);
	public abstract void sendRemoveBrocast(Context context, boolean isSelected);
	public abstract void sendClearBrocast(Context context);
	
	public AbstractMediaMng(Context context){
		mContext = context;
	}
	
	public synchronized List<Device> getDeviceList() {
		return mDeviceList;
	}
	
	/**
	 * @return the selectedDevice
	 */
	public Device getSelectedDevice() {
		return mSelectedDevice;
	}

	/**
	 * @param selectedDevice
	 *            the selectedDevice to set
	 */
	public void setSelectedDevice(Device selectedDevice) {
		mSelectedDevice = selectedDevice;
	}
	
	public synchronized void  addDevice(Device d) {
		mDeviceList.add(d);
		sendAddBrocast(mContext);
	}
	
	public synchronized void removeDevice(Device d)
	{	
		int size = mDeviceList.size();
		for(int i = 0; i < size; i++)
		{
			String udnString = mDeviceList.get(i).getUDN();
			if (d.getUDN().equalsIgnoreCase(udnString)){
				Device device = mDeviceList.remove(i);
				
				boolean ret = false;
				if (mSelectedDevice != null){
					ret = mSelectedDevice.getUDN().equalsIgnoreCase(device.getUDN());
				}
				if (ret){
					setSelectedDevice(null);
				}
				sendRemoveBrocast(mContext, ret);
				break;
			}
		}
	}
	
	public synchronized void  clear()
	{
		mDeviceList = new ArrayList<Device>();
		mSelectedDevice = null;
		sendClearBrocast(mContext);
	}
	
	
}
