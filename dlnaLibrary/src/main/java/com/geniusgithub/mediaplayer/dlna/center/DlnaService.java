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

package com.geniusgithub.mediaplayer.dlna.center;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.geniusgithub.mediaplayer.dlna.base.IControlPointState;
import com.geniusgithub.mediaplayer.dlna.base.IControlEngine;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

public class DlnaService extends Service implements IControlEngine,
													DeviceChangeListener,
													ControlCenterWorkThread.ISearchDeviceListener{

	private static final String TAG = DlnaService.class.getSimpleName();

	public static final String SEARCH_DEVICES = "com.geniusgithub.allshare.search_device";
	public static final String RESET_SEARCH_DEVICES = "com.geniusgithub.allshare.reset_search_device";
	
	private static final int NETWORK_CHANGE = 0x0001;
	private boolean firstReceiveNetworkChangeBR = true;
	private  NetworkStatusChangeBR mNetworkStatusChangeBR;
	
	
	private ControlPointImpl mControlPoint;
	private  ControlCenterWorkThread mCenterWorkThread;
	private  AllShareProxy mAllShareProxy;
	private  Handler mHandler;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "DlnaService onCreate");
		init();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null && intent.getAction() != null){
			String action = intent.getAction();
			if (DlnaService.SEARCH_DEVICES.equals(action)) {
				startEngine();
			}else if (DlnaService.RESET_SEARCH_DEVICES.equals(action)){
				restartEngine();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "DlnaService onDestroy");
		unInit();
		super.onDestroy();
	}
	
	
	private void init(){
		mAllShareProxy = AllShareProxy.getInstance(this);
		
		mControlPoint = new ControlPointImpl();
		mAllShareProxy.setControlPoint(mControlPoint);
		mControlPoint.addDeviceChangeListener(this);
		mControlPoint.addSearchResponseListener(new SearchResponseListener() {		
			public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
			}
		});
	

		mCenterWorkThread = new ControlCenterWorkThread(this, mControlPoint);
		mCenterWorkThread.setSearchListener(this);
		
		mHandler = new Handler(){

			public void handleMessage(Message msg) {
				switch(msg.what){
					case NETWORK_CHANGE:
						mAllShareProxy.resetSearch();
						break;
				}
			}
			
		};
		
		registerNetworkStatusBR();

		boolean ret = openWifiBrocast(this);
		Log.i(TAG, "openWifiBrocast = " + ret);

		mAllShareProxy.onEngineCreate();

	}
	
	private void unInit(){
		unRegisterNetworkStatusBR();
		stopEngine();
		mAllShareProxy.setControlPoint(null);
		mAllShareProxy.onEngineDestory();
	}

	
	@Override
	public boolean startEngine() {
		Log.i(TAG, "startEngine");
		if (mAllShareProxy.getControlStatus() != IControlPointState.STATUS_STARTED){
			mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTING);
		}
		awakeWorkThread();
		return true;
	}


	@Override
	public boolean stopEngine() {
		Log.i(TAG, "stopEngine");
		exitWorkThread();
		return true;
	}


	@Override
	public boolean restartEngine() {
		Log.i(TAG, "restartEngine");
		mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTING);
		mCenterWorkThread.setCompleteFlag(false);
		awakeWorkThread();

		mAllShareProxy.onEngineRestart();

		return true;
	}




	@Override
	public void deviceAdded(Device dev) {
		mAllShareProxy.addDevice(dev);
	}


	@Override
	public void deviceRemoved(Device dev) {
		mAllShareProxy.removeDevice(dev);
	}
	
	
	private void awakeWorkThread(){

		if (mCenterWorkThread.isAlive()){
			mCenterWorkThread.awakeThread();
		}else{
			mCenterWorkThread.start();
		}
	}
	
	private void exitWorkThread(){
		if (mCenterWorkThread != null && mCenterWorkThread.isAlive()){
			mCenterWorkThread.exit();
			long time1 = System.currentTimeMillis();
			while(mCenterWorkThread.isAlive()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long time2 = System.currentTimeMillis();
			Log.d(TAG, "exitCenterWorkThread cost time:" + (time2 - time1));
			mCenterWorkThread = null;
		}
	}
	
	
	@Override
	public void onSearchComplete(boolean searchSuccess) {

/*		if (!searchSuccess){
			sendSearchDeviceFailBrocast(this);
		}*/
	}

	@Override
	public void onStartComplete(boolean startSuccess) {

		mControlPoint.flushLocalAddress();
	//	sendStartDeviceEventBrocast(this, startSuccess);
		Log.i(TAG, "onStartComplete startSuccess = " + startSuccess);
		if (startSuccess){
			mAllShareProxy.setControlStauts(IControlPointState.STATUS_STARTED);
		}else{

		}

	}

	@Override
	public void onStopComplete() {
		Log.i(TAG, "onStopComplete");
		mAllShareProxy.setControlStauts(IControlPointState.STATUS_SOTP);
	}

	
	private class NetworkStatusChangeBR extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent != null){
				String action = intent.getAction();
				if (action != null){
					if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
						sendNetworkChangeMessage();
					}
				}
			}
			
		}
		
	}
	
	private void registerNetworkStatusBR(){
		if (mNetworkStatusChangeBR == null){
			mNetworkStatusChangeBR = new NetworkStatusChangeBR();
			registerReceiver(mNetworkStatusChangeBR, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}
	
	private void unRegisterNetworkStatusBR(){
		if (mNetworkStatusChangeBR != null){
			unregisterReceiver(mNetworkStatusChangeBR);
		}
	}
	
	private void sendNetworkChangeMessage(){
		if (firstReceiveNetworkChangeBR){
		    Log.i(TAG, "first receive the NetworkChangeMessage, so drop it...");
			firstReceiveNetworkChangeBR = false;
			return ;
		}
		
		mHandler.removeMessages(NETWORK_CHANGE);
		mHandler.sendEmptyMessageDelayed(NETWORK_CHANGE, 500);
	}


	public static boolean openWifiBrocast(Context context){
		WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock=wifiManager.createMulticastLock("MediaPlayer");
		if (multicastLock != null){
			multicastLock.acquire();
			return true;
		}
		return false;
	}
}
