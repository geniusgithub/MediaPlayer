package com.geniusgithub.mediaplayer.dlna.model;

import android.content.Context;

import com.geniusgithub.mediaplayer.dlna.proxy.IDeviceChangeListener;

public abstract class AbstractDeviceBrocastFactory {

	
	protected Context mContext;
	protected AbstractDeviceChangeBrocastReceiver mReceiver;
	
	public AbstractDeviceBrocastFactory(Context context){
		mContext = context;
	}
	
	public abstract void registerListener(IDeviceChangeListener listener);
	public abstract void unRegisterListener();
	
}
