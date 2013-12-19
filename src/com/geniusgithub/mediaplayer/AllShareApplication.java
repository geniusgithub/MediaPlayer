package com.geniusgithub.mediaplayer;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.geniusgithub.mediaplayer.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.util.CommonUtil;


import android.app.Application;


/**
 * @author lance
 * @csdn  http://blog.csdn.net/geniuseoe2012
 * @github https://github.com/geniusgithub
 */
public class AllShareApplication extends Application{

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
		
		boolean ret = CommonUtil.openWifiBrocast(this);
		log.e("openWifiBrocast = " + ret);
	}
	
	public void setControlPoint(ControlPoint controlPoint){
		mControlPoint = controlPoint;
	}
	
	public ControlPoint getControlPoint(){
		return mControlPoint;
	}
	
}
