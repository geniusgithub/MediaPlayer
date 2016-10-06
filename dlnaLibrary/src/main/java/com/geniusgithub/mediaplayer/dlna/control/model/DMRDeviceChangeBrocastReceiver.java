package com.geniusgithub.mediaplayer.dlna.control.model;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.control.base.IDMRDeviceChangeListener;

public class DMRDeviceChangeBrocastReceiver extends BroadcastReceiver {


	protected IDMRDeviceChangeListener mListener;

	public void setListener(IDMRDeviceChangeListener listener){
		mListener  = listener;
	}

	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
	
		
		if (DMRDeviceBrocastFactory.ADD_DEVICES.equalsIgnoreCase(action) ||
				DMRDeviceBrocastFactory.REMOVE_DEVICES.equalsIgnoreCase(action) ||
				DMRDeviceBrocastFactory.CLEAR_DEVICES.equalsIgnoreCase(action)){
			boolean isSelDeviceChange = intent.getBooleanExtra(DMRDeviceBrocastFactory.REMOVE_EXTRA_FLAG, false);
			if (mListener != null){
				mListener.onDMRDeviceChange(isSelDeviceChange);
			}
		}

	}
}
