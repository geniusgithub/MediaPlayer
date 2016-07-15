package com.geniusgithub.mediaplayer.upnp;

import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;


import android.content.Context;

public abstract class AbstractDeviceBrocastFactory {

	protected static final CommonLog log = LogFactory.createLog();
	
	protected Context mContext;
	protected AbstractDeviceChangeBrocastReceiver mReceiver;
	
	public AbstractDeviceBrocastFactory(Context context){
		mContext = context;
	}
	
	public abstract void registerListener(IDeviceChangeListener listener);
	public abstract void unRegisterListener();
	
}
