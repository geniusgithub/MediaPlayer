package com.geniusgithub.mediaplayer.dlna.control.model;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.control.base.IDMSDeviceChangeListener;

public class DMSDeviceChangeBrocastReceiver extends BroadcastReceiver {

	protected IDMSDeviceChangeListener mListener;

	public void setListener(IDMSDeviceChangeListener listener){
		mListener  = listener;
	}

	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
	
		
		if (DMSDeviceBrocastFactory.ADD_DEVICES.equalsIgnoreCase(action) ||
				DMSDeviceBrocastFactory.REMOVE_DEVICES.equalsIgnoreCase(action) || 
				DMSDeviceBrocastFactory.CLEAR_DEVICES.equalsIgnoreCase(action)){			
			boolean isSelDeviceChange = intent.getBooleanExtra(DMSDeviceBrocastFactory.REMOVE_EXTRA_FLAG, false);
			if (mListener != null){
				mListener.onDMSDeviceChange(isSelDeviceChange);
			}
		}

	}
}
