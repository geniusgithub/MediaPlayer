package com.geniusgithub.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class CommonUtil {

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath: /data/data/
		}
	}

/*	public static boolean checkNetworkState(Context context){
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
	}*/

	/*public static String getLocalMacAddress(Context mc){
		String defmac = "00:00:00:00:00:00";
		InputStream input   =   null;
		String wifimac = getWifiMacAddress(mc);
		if(null != wifimac){
			if(!wifimac.equals(defmac))
				return wifimac;
		}
		try{

			ProcessBuilder builder = new ProcessBuilder( "busybox","ifconfig");
			Process process = builder.start();
			input = process.getInputStream();



			byte[] b = new byte[1024];
			StringBuffer buffer = new StringBuffer();
			while(input.read(b)>0){
				buffer.append(new String(b));
			}
			String value = buffer.substring(0);
			String systemFlag ="HWaddr ";
			int index = value.indexOf(systemFlag);
			//List <String> address   = new ArrayList <String> ();
			if(0<index){
				value = buffer.substring(index+systemFlag.length());
				//address.add(value.substring(0,18));
				defmac=value.substring(0,17);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return defmac;
	}

	public static String getWifiMacAddress(Context mc) {
		WifiManager wifi = (WifiManager) mc.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}*/

/*	public static boolean openWifiBrocast(Context context){
		WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock=wifiManager.createMulticastLock("MediaPlayer");
		if (multicastLock != null){
			multicastLock.acquire();
			return true;
		}
		return false;
	}*/


	public static void setCurrentVolume(int percent,Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		int maxvolume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (maxvolume*percent)/100,
				AudioManager.FLAG_PLAY_SOUND|AudioManager.FLAG_SHOW_UI);
		am.setMode(AudioManager.MODE_INVALID);
	}

	public static void setVolumeMute(Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}
	public static void setVolumeUnmute(Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public static void showToask(Context context, String tip){
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static ViewSize getFitSize(Context context, MediaPlayer mediaPlayer)
	{
		int videoWidth = mediaPlayer.getVideoWidth();
		int videoHeight = mediaPlayer.getVideoHeight();
		double fit1 = videoWidth * 1.0 / videoHeight;

		int width2 = getScreenWidth(context);
		int height2 = getScreenHeight(context);
		double fit2 = width2 * 1.0 / height2;

		double fit = 1;
		if (fit1 > fit2)
		{
			fit = width2 * 1.0 / videoWidth;
		}else{
			fit = height2 * 1.0 / videoHeight;
		}

		ViewSize viewSize = new ViewSize();
		viewSize.width = (int) (fit * videoWidth);
		viewSize.height = (int) (fit * videoHeight);

		return viewSize;
	}

	public static class ViewSize
	{
		public int width = 0;
		public int height = 0;
	}

	public static boolean getWifiState(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifistate = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifistate != NetworkInfo.State.CONNECTED){
			return false;
		}

		NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		boolean ret = NetworkInfo.State.CONNECTED != mobileState;
		return ret;
	}


	public static boolean getMobileState(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifistate = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifistate != NetworkInfo.State.CONNECTED){
			return false;
		}

		NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		boolean ret = NetworkInfo.State.CONNECTED == mobileState;
		return ret;
	}



	private static long m_lSysNetworkSpeedLastTs = 0;
	private static long m_lSystNetworkLastBytes = 0;
	private static float m_fSysNetowrkLastSpeed = 0.0f;
	public static float getSysNetworkDownloadSpeed() {
		long nowMS = System.currentTimeMillis();
		long nowBytes = TrafficStats.getTotalRxBytes();

		long timeinterval = nowMS - m_lSysNetworkSpeedLastTs;
		long bytes = nowBytes - m_lSystNetworkLastBytes;

		if(timeinterval > 0) m_fSysNetowrkLastSpeed = (float)bytes * 1.0f / (float)timeinterval;

		m_lSysNetworkSpeedLastTs = nowMS;
		m_lSystNetworkLastBytes = nowBytes;

		return m_fSysNetowrkLastSpeed;
	}


	public static String getSoftVersion(Context context){

		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = "00.00.01";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version  = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;

	}
}
