package com.geniusgithub.mediaplayer.dlna.control.model;


import android.content.Context;

public class MediaRenderMng extends AbstractMediaMng {

	public MediaRenderMng(Context context) {
		super(context);
	
	}

	@Override
	public void sendAddBrocast(Context context) {
		DMRDeviceBrocastFactory.sendAddBrocast(context);
	}

	@Override
	public void sendRemoveBrocast(Context context, boolean isSelected) {
		DMRDeviceBrocastFactory.sendRemoveBrocast(context, isSelected);
	}

	@Override
	public void sendClearBrocast(Context context) {
		DMRDeviceBrocastFactory.sendClearBrocast(context);
	}

}
