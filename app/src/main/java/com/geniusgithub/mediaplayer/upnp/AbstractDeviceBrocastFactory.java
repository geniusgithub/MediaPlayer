package com.geniusgithub.mediaplayer.upnp;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
