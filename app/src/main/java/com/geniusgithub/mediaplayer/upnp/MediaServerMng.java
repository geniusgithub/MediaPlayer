package com.geniusgithub.mediaplayer.upnp;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;


import android.content.Context;

public class MediaServerMng extends AbstractMediaMng{

	public MediaServerMng(Context context) {
		super(context);
	
	}

	@Override
	public void sendAddBrocast(Context context) {
		DMSDeviceBrocastFactory.sendAddBrocast(context);	
	}

	@Override
	public void sendRemoveBrocast(Context context, boolean isSelected) {
		DMSDeviceBrocastFactory.sendRemoveBrocast(context, isSelected);
	}

	@Override
	public void sendClearBrocast(Context context) {
		DMSDeviceBrocastFactory.sendClearBrocast(context);
	}

}
