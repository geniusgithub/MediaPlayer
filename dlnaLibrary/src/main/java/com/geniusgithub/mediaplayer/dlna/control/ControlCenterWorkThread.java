package com.geniusgithub.mediaplayer.dlna.control;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.cybergarage.util.AlwaysLog;

public class ControlCenterWorkThread extends Thread{

	private final static String TAG = ControlCenterWorkThread.class.getSimpleName();

	private static final int REFRESH_DEVICES_INTERVAL = 30 * 1000; 
	
	public static interface ISearchDeviceListener{
		public void onSearchComplete(boolean searchSuccess);
		public void onStartComplete(boolean startSuccess);
		public void onStopComplete();
	}
	
	private ControlPointImpl mCP = null;
	private Context mContext = null;
	private boolean mStartComplete = false;
	private boolean mIsExit = false;
	private ISearchDeviceListener mSearchDeviceListener;
	
	public ControlCenterWorkThread(Context context, ControlPointImpl controlPoint){
		mContext = context;
		mCP = controlPoint; 
	}
	
	public void  setCompleteFlag(boolean flag){
		mStartComplete = flag;
	}
	
	public void setSearchListener(ISearchDeviceListener listener){
		mSearchDeviceListener = listener;
	}
	
	public void awakeThread(){
		synchronized (this) {
			notifyAll();
		}
	}
	
	
	public void reset(){
		setCompleteFlag(false);
		awakeThread();
	}
	
	public void exit(){
		mIsExit = true;
		awakeThread();
	}
	
	
	@Override
	public void run() {
		AlwaysLog.i(TAG, "ControlCenterWorkThread run...");
		
		while(true)
		{
			if (mIsExit){
				mCP.stop();
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onStopComplete();
				}
				break;
			}
			
			refreshDevices();
			
			synchronized(this)
			{		
				try
				{
					wait(REFRESH_DEVICES_INTERVAL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}				
			}
		}

		AlwaysLog.i(TAG, "ControlCenterWorkThread over...");
	}
	
	private void refreshDevices(){
		AlwaysLog.d(TAG, "refreshDevices...");
		if (!checkNetworkState(mContext)){
			return ;
		}

		try {
			if (mStartComplete){
				boolean searchRet = mCP.search();
				AlwaysLog.i(TAG, "mCP.search() ret = "  + searchRet);
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onSearchComplete(searchRet);
				}
			}else{
				boolean startRet = mCP.start();
				AlwaysLog.i(TAG, "mCP.start() ret = "  + startRet);
				if (startRet){
					mStartComplete = true;
				}
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onStartComplete(startRet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public static boolean checkNetworkState(Context context){
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

}
