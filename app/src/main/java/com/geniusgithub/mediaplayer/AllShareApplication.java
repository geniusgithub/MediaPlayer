package com.geniusgithub.mediaplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.geniusgithub.mediaplayer.dlna.ControlPointImpl;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


/**
 * @author lance
 * @csdn  http://blog.csdn.net/geniuseoe2012
 * @github https://github.com/geniusgithub
 */
public class AllShareApplication extends Application implements ItatisticsEvent{

	private static final CommonLog log = LogFactory.createLog();
	
	private AllShareProxy mAllShareProxy;

	private ControlPointImpl mControlPoint;
	
	private static AllShareApplication mAllShareApplication;

	private boolean mEnterMain = false;
	
	public static AllShareApplication getInstance(){
		return mAllShareApplication;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
		mAllShareApplication = this;


		MobclickAgent.setDebugMode(true);


		  TCAgent.init(this);
		  TCAgent.setReportUncaughtExceptions(true);



		boolean ret = CommonUtil.openWifiBrocast(this);

	}

	public void setStatus(boolean flag){
		mEnterMain = flag;
	}

	public boolean getEnterFlag(){
		return mEnterMain;
	}
	
	public void setControlPoint(ControlPointImpl controlPoint){
		mControlPoint = controlPoint;
	}

	public String getLocalAddress(){
		if (mControlPoint != null){
			return mControlPoint.getLocalAddress();
		}

		return "";
	}
	public ControlPointImpl getControlPoint(){
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
		MobclickAgent.onPause(context);
		TCAgent.onPause(context);
	}
	
	public static void onResume(Activity context){
		MobclickAgent.onResume(context);
		TCAgent.onResume(context);
	}
	
	public static void onCatchError(Context context){

		TCAgent.setReportUncaughtExceptions(true);
	}
	
}
