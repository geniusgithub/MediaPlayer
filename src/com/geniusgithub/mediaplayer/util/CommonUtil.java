package com.geniusgithub.mediaplayer.util;


import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class CommonUtil {

	private static final CommonLog log = LogFactory.createLog();
	
	public static boolean checkNetState(Context context)
    {
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
	
	public static String getLocalIpAddress() throws SocketException {  
		String defaultIP = "0.0.0.0";
    
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
            NetworkInterface intf = en.nextElement();  
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                InetAddress inetAddress = enumIpAddr.nextElement();  
                if (!inetAddress.isLoopbackAddress()) {  
                	String ip = inetAddress.getHostAddress().toString();  
                	 if(ip == null)
                 	  {
                 		  continue;
                 	  }
                	 return ip;
                }  
            }  
        }  
  
        return defaultIP;  
    } 
	
	public static String getLocalMacAddress(Context mc){
		String defmac = "00:00:00:00:00:00";
		InputStream   input   =   null;
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
    } 
	
	public static void showToask(Context context, String tip){
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}
	
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
		//	log.e("No sdcard");
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
	    
	    log.e("videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ",fit1 = " + fit1);
	    log.e("width2 = " + width2 + ", height2 = " + height2 + ",fit2 = " + fit2);
	    
	    double fit = 1;
	    if (fit1 > fit2)
	    {
	    	fit = width2 * 1.0 / videoWidth;
	    }else{
	    	fit = height2 * 1.0 / videoHeight;
	    }
	    
	    log.e("fit = " + fit);
	    
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
    
}
