package com.geniusgithub.mediaplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.geniusgithub.mediaplayer.dlna.IControlPointStatu;
import com.geniusgithub.mediaplayer.base.ItatisticsEvent;
import com.geniusgithub.mediaplayer.dlna.ControlPointImpl;
import com.geniusgithub.mediaplayer.dlna.model.ControlStatusChangeBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
/**
 * @author lance
 * @cnblog http://www.cnblogs.com/lance2016/
 * @github https://github.com/geniusgithub
 */
public class AllShareApplication extends Application implements ItatisticsEvent {

	private static final CommonLog log = LogFactory.createLog();
	
	private AllShareProxy mAllShareProxy;

	private ControlPointImpl mControlPoint;
	
	private static AllShareApplication mAllShareApplication;

	private int mContropPointStatus = IControlPointStatu.STATUS_SOTP;

	private boolean mEnterMain = false;

	private Handler mHandle;
	
	public static AllShareApplication getInstance(){
		return mAllShareApplication;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mAllShareProxy = AllShareProxy.getInstance(this);
		mAllShareApplication = this;

		mHandle = new Handler() {
		};

		MobclickAgent.setDebugMode(true);


		  TCAgent.init(this);
		  TCAgent.setReportUncaughtExceptions(true);



		boolean ret = CommonUtil.openWifiBrocast(this);

	}

	public synchronized void updateControlStauts(int stauts){
		if (mContropPointStatus != stauts){
			mContropPointStatus = stauts;
			ControlStatusChangeBrocastFactory.sendControlStatusChangeBrocast(this, mContropPointStatus);
		}

	}

	public synchronized int getControlStatus(){
		return mContropPointStatus;
	}


	public void setEnterFlag(boolean flag){
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

	public void delayToExit(){
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				log.e("Exit Application...");
				System.exit(0);
			}
		}, 500);
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
