package com.geniusgithub.mediaplayer.upnp;



import android.content.Context;
import android.content.Intent;

public class DMSDeviceChangeBrocastReceiver extends AbstractDeviceChangeBrocastReceiver{

	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
	
		
		if (DMSDeviceBrocastFactory.ADD_DEVICES.equalsIgnoreCase(action) || 
				DMSDeviceBrocastFactory.REMOVE_DEVICES.equalsIgnoreCase(action) || 
				DMSDeviceBrocastFactory.CLEAR_DEVICES.equalsIgnoreCase(action)){			
			boolean isSelDeviceChange = intent.getBooleanExtra(DMSDeviceBrocastFactory.REMOVE_EXTRA_FLAG, false);
			if (mListener != null){
				mListener.onDeviceChange(isSelDeviceChange);
			}
		}

	}
}
