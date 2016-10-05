/*
 * Copyright  2016 geniusgithub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniusgithub.mediaplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.component.CacheManager;
import com.geniusgithub.mediaplayer.dlna.control.AllShareProxy;
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
public class AllShareApplication extends Application implements AllShareProxy.IEngineStatusCallback, ItatisticsEvent {

	private final static String TAG = AllShareApplication.class.getSimpleName();

	private static AllShareApplication mAllShareApplication;
	private AllShareProxy mAllShareProxy;
	private CacheManager mCacheManager;

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
		mAllShareProxy.setEngineStatusCallback(this);
		mAllShareApplication = this;

		mCacheManager = CacheManager.newInstance(this);


		mHandle = new Handler() {
		};

		MobclickAgent.setDebugMode(true);

	}

	public void setEnterFlag(boolean flag){
		mEnterMain = flag;
	}

	public boolean getEnterFlag(){
		return mEnterMain;
	}


	public void delayToExit(){
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				AlwaysLog.i(TAG, "Exit Application...");
				System.exit(0);
			}
		}, 500);
	}
	@Override
	public void onEvent(String eventID) {
		AlwaysLog.d(TAG, "eventID = " + eventID);

	}

	@Override
	public void onEvent(String eventID, HashMap<String, String> map) {
		AlwaysLog.i(TAG, "eventID = " + eventID);

	}
	
	public static void onPause(Activity context){
		MobclickAgent.onPause(context);

	}
	
	public static void onResume(Activity context){
		MobclickAgent.onResume(context);

	}
	
	public static void onCatchError(Context context){

	}

	@Override
	public void onEngineCreate() {
		CacheManager.getInstance().clearCache();
	}

	@Override
	public void onEngineDestory() {
		CacheManager.getInstance().clearCache();
	}

	@Override
	public void onEngineRestart() {
		CacheManager.getInstance().clearCache();
	}
}
