package com.geniusgithub.mediaplayer;

import java.util.HashMap;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.geniusgithub.mediaplayer.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.tendcloud.tenddata.TCAgent;


import android.app.Activity;
import android.app.Application;
import android.content.Context;


/**
 * @author lance
 * @csdn  http://blog.csdn.net/geniuseoe2012
 * @github https://github.com/geniusgithub
 */
public class AllShareApplication extends Application implements ItatisticsEvent{

	private static final CommonLog log = LogFactory.createLog();
	
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

	@Override
	public void onEvent(String eventID) {
		log.e("eventID = " + eventID);	
		TCAgent.onEvent(this, eventID);
	}

	@Override
	public void onEvent(String eventID, HashMap<String, String> map) {
		log.e("eventID = " + eventID);	
		TCAgent.onEvent(this, eventID, "", map);
	}
	
	public static void onPause(Activity context){
		TCAgent.onPause(context);
	}
	
	public static void onResume(Activity context){
		TCAgent.onResume(context);
	}
	
	public static void onCatchError(Context context){
		TCAgent.setReportUncaughtExceptions(true);
	}
	
}
