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

package com.geniusgithub.mediaplayer.dlna.base;

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

public abstract class DlnaService extends Service {

	private static final String TAG = DlnaService.class.getSimpleName();

	private DlnaEngineObserver mObserver;

	private static final int NETWORK_CHANGE = 0x0001;
	private Handler mHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public abstract DlnaEngineObserver createObserver();
	public abstract  String getStartAction();
	public abstract  String getRestartAction();


	@Override
	public void onCreate() {
		super.onCreate();

		mHandler = new Handler(){

			public void handleMessage(Message msg) {
				switch(msg.what){
					case NETWORK_CHANGE:
						mObserver.restartEngine();
						break;
				}
			}

		};
		openWifiBrocast(this);
		registerNetworkStatusBR();

		mObserver = createObserver();
		mObserver.initEngine();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null && intent.getAction() != null){
			String action = intent.getAction();
			if (getStartAction().equals(action)) {
				mObserver.startEngine();
			}else if (getRestartAction().equals(action)){
				mObserver.restartEngine();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unRegisterNetworkStatusBR();
		mObserver.stopEngine();
		super.onDestroy();
	}


	protected boolean firstReceiveNetworkChangeBR = true;
	protected  NetworkStatusChangeBR mNetworkStatusChangeBR;

	protected class NetworkStatusChangeBR extends BroadcastReceiver {

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

	protected void registerNetworkStatusBR(){
		if (mNetworkStatusChangeBR == null){
			mNetworkStatusChangeBR = new NetworkStatusChangeBR();
			registerReceiver(mNetworkStatusChangeBR, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	protected void unRegisterNetworkStatusBR(){
		if (mNetworkStatusChangeBR != null){
			unregisterReceiver(mNetworkStatusChangeBR);
		}
	}

	protected void sendNetworkChangeMessage(){
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
