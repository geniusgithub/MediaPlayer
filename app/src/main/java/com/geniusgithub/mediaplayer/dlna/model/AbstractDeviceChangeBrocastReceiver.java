package com.geniusgithub.mediaplayer.dlna.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

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
