package com.geniusgithub.mediaplayer.proxy;

import java.util.List;

import org.cybergarage.upnp.Device;

public interface IDeviceOperator {

	public void  addDevice(Device d);
	public void removeDevice(Device d);
	public void clearDevice();
	
	public static interface IDMSDeviceOperator{
		public  List<Device> getDMSDeviceList();
		public Device getDMSSelectedDevice();
		public void setDMSSelectedDevice(Device selectedDevice);

	}
//
//	public static interface IDMRDeviceOperator{
//		public  List<Device> getDMRDeviceList();
//		public Device getDMRSelectedDevice1();
//		public void setDMRSelectedDevice(Device selectedDevice);
//	}
	
}
