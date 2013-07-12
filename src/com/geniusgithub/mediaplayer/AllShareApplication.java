package com.geniusgithub.mediaplayer;

import org.cybergarage.upnp.ControlPoint;

import com.geniusgithub.mediaplayer.proxy.AllShareProxy;


import android.app.Application;

public class AllShareApplication extends Application{

	private AllShareProxy mAllShareProxy;

	private ControlPoint mControlPoint;
	
	private static AllShareApplication mAllShareApplication;
	
	public static AllShareApplication getInstance(){
		return mAllShareApplication;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
		mAllShareApplication = this;
	}
	
	public void setControlPoint(ControlPoint controlPoint){
		mControlPoint = controlPoint;
	}
	
	public ControlPoint getControlPoint(){
		return mControlPoint;
	}
	
}
