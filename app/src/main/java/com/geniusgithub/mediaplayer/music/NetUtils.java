package com.geniusgithub.mediaplayer.music;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.cybergarage.util.CommonLog;

import android.graphics.drawable.Drawable;

import com.geniusgithub.mediaplayer.util.LogFactory;



public class NetUtils {

private static final CommonLog log = LogFactory.createLog();
	
	public static Drawable requestDrawableByUri(String uri){
		if (uri == null || uri.length() == 0){
			return null;
		}
		
		Drawable drawable = null; 
		int index = 0;
		while(true){
			if (index >= 3){
				break;
			}
			drawable = getDrawableFromUri(uri);
			if (drawable != null){
				break;
			}
			index++;
		}
			
		return drawable;
	}
	
	public static Drawable getDrawableFromUri(String uri){
		if (uri == null || uri.length() < 1){
			return null;
		}
		Drawable drawable = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			InputStream input = conn.getInputStream();
			if (conn.getResponseCode() != 200){
			    log.e("getDrawableFromUri.getResponseCode() = " + conn.getResponseCode() + "\n" +
			    		"uri :" + uri + "is invalid!!!");
			    input.close();
				return null;
			}
			drawable = Drawable.createFromStream(input, "src"); 
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		//	log.e("getDrawableFromUri catch exception!!!e = " + e.getMessage());
		}
		
		return drawable;
	}
}
