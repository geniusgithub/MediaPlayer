package com.geniusgithub.mediaplayer.upnp;

import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AbstractDeviceChangeBrocastReceiver extends BroadcastReceiver{

	public static final CommonLog log = LogFactory.createLog();
	protected IDeviceChangeListener mListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
	}

	
	public void setListener(IDeviceChangeListener listener){
		mListener  = listener;
	}
	
}
