package com.geniusgithub.mediaplayer.dlna.control.model;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.geniusgithub.mediaplayer.dlna.control.base.IDMRDeviceChangeListener;

public class DMRDeviceBrocastFactory {

	public static final String ADD_DEVICES = "com.geniusgithub.allshare.add_dmr_device";
	public static final String REMOVE_DEVICES = "com.geniusgithub.allshare.remove_dmr_device";
	public static final String REMOVE_EXTRA_FLAG = "com.geniusgithub.allshare.remove_dmr_extra_flag";
	public static final String CLEAR_DEVICES = "com.geniusgithub.allshare.clear_dmr_device";



	protected Context mContext;
	protected DMRDeviceChangeBrocastReceiver mReceiver;

	public DMRDeviceBrocastFactory(Context context) {
		mContext = context;
	
	}

	public void registerListener(IDMRDeviceChangeListener listener) {
		
		if (mReceiver == null){
			mReceiver = new DMRDeviceChangeBrocastReceiver();
			mContext.registerReceiver(mReceiver, new IntentFilter(ADD_DEVICES));
			mContext.registerReceiver(mReceiver, new IntentFilter(REMOVE_DEVICES));
			mContext.registerReceiver(mReceiver, new IntentFilter(CLEAR_DEVICES));
			mReceiver.setListener(listener);
		}
		
	}

	public void unRegisterListener() {
	
		if (mReceiver != null){
			mContext.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	


	public static  void sendAddBrocast(Context context){
		Intent intent = new Intent(ADD_DEVICES);
		context.sendBroadcast(intent);
	}
	
	public static void sendRemoveBrocast(Context context, boolean isSelected){
		Intent intent = new Intent(REMOVE_DEVICES);
		intent.putExtra(REMOVE_EXTRA_FLAG, isSelected);
		context.sendBroadcast(intent);
	}
	
	public static void sendClearBrocast(Context context){
		Intent intent = new Intent(CLEAR_DEVICES);
		intent.putExtra(REMOVE_EXTRA_FLAG, true);
		context.sendBroadcast(intent);
	}
	

}
