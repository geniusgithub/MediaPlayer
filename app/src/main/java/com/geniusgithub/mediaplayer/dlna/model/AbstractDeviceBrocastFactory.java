package com.geniusgithub.mediaplayer.dlna.model;

import android.content.Context;

import com.geniusgithub.mediaplayer.dlna.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

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
