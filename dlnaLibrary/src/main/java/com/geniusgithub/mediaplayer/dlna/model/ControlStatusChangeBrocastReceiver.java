package com.geniusgithub.mediaplayer.dlna.model;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.base.IControlPointState;

public class ControlStatusChangeBrocastReceiver extends BroadcastReceiver {

	protected IStatusChangeListener mListener;


	public void setListener(IStatusChangeListener listener){
		mListener  = listener;
	}

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
	
		
		if (ControlStatusChangeBrocastFactory.CONTROL_STATUS_CHANGE_EVENT.equalsIgnoreCase(action)){
			int status = intent.getIntExtra(ControlStatusChangeBrocastFactory.CONTROL_STATUS_CHANGE_EXTRA, IControlPointState.STATUS_SOTP);
			if (mListener != null){
				mListener.onStatusChange(status);
			}
		}

	}
}
