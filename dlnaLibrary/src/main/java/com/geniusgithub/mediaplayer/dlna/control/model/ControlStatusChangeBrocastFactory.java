package com.geniusgithub.mediaplayer.dlna.control.model;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.geniusgithub.mediaplayer.dlna.control.base.IStatusChangeListener;

public class ControlStatusChangeBrocastFactory{


	public static final String CONTROL_STATUS_CHANGE_EVENT = "com.geniusgithub.allshare.control_status_change_event";
	public static final String CONTROL_STATUS_CHANGE_EXTRA = "com.geniusgithub.allshare.control_status_change_extra";



	private Context mContext;
	private ControlStatusChangeBrocastReceiver mReceiver;

	public ControlStatusChangeBrocastFactory(Context context) {
		mContext = context;
	}


	public void registerListener(IStatusChangeListener listener) {
		
		if (mReceiver == null){
			mReceiver = new ControlStatusChangeBrocastReceiver();
			mContext.registerReceiver(mReceiver, new IntentFilter(CONTROL_STATUS_CHANGE_EVENT));
			mReceiver.setListener(listener);
		}
		
	}

	public void unRegisterListener() {
	
		if (mReceiver != null){
			mContext.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	public static void sendControlStatusChangeBrocast(Context context, int status){
		Intent intent = new Intent(CONTROL_STATUS_CHANGE_EVENT);
		intent.putExtra(CONTROL_STATUS_CHANGE_EXTRA, status);
		context.sendBroadcast(intent);
	}

	

}
