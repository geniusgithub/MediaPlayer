package com.geniusgithub.mediaplayer.dlna.util;

import android.util.Log;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.util.AlwaysLog;

import java.util.List;

public class ParseUtil {

	private final static String TAG = ParseUtil.class.getSimpleName();
	
/*	public static MediaItem.ResInfo getBestResInfo(String objectClass, List<MediaItem.ResInfo> list){
		
		if (objectClass == null || list == null || list.size() == 0){
			return null;
		}
		
		if (!objectClass.contains(UpnpUtil.DLNA_OBJECTCLASS_PHOTOID)){
			return list.get(0);
		}
		
		int maxIndex = 0;
		int size = list.size();
		for(int i = 1; i < size; i++){
			boolean ret = compareBetweenResolution(list.get(maxIndex).resolution, list.get(i).resolution);
			if (!ret){
				maxIndex = i;
			}
		}

		return list.get(maxIndex);
	}*/
	
	public static boolean compareBetweenResolution(String resolution1, String resolution2){
	
		int resolutionInt1 = formatResolution(resolution1);
		int resolutionInt2 = formatResolution(resolution2);
		
		return resolutionInt1 >= resolutionInt2 ? true : false;
	}
	
	
	public static long formatSizeString(String sizeString) {
		long size = 0;
		if (sizeString == null || sizeString.length() < 1){
			return size;
		}
		try {
			size = Long.parseLong(sizeString);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("", "sizeString = " + sizeString);
		}
		return size;
	}



	public static int formatDurationString(String durationString) {
		int duration = 0;
		if(durationString == null || durationString.length() == 0){
			return duration;
		}
		try {
			String sArray[] = durationString.split(":");
			double hour = Double.valueOf(sArray[0]);
			double minute = Double.valueOf(sArray[1]);
			double second = Double.valueOf(sArray[2]);		
			return (int) ((hour * 60 + minute) * 60 + second) * 1000;
		} catch (Exception e) {

		}

		return duration;
	}
	
	public static int formatResolution(String resolutionString){
		int value = 0;
		if(resolutionString == null || resolutionString.length() == 0){
			return value;
		}
		
		try {
			String array[] = resolutionString.split("x");
			int v1 = Integer.valueOf(array[0]);
			int v2 = Integer.valueOf(array[1]);
			
			value = v1 * v2;
		} catch (Exception e) {
			
		}
		
		return value;
	}



	public static boolean parseResult(ContainerNode rootNode, List<MediaItem> list){

	//	AlwaysLog.i(TAG, "parseResult rootNode = \n" + rootNode.toString());
		int childCount = rootNode.getChildCount();
		ContentNode node = null;
		for(int i = 0; i < childCount; i++){
			node = rootNode.getContentNode(i);

			MediaItem item = MediaItem.Builder.create(node);
			if (item != null){
				list.add(item);
			}else{
				AlwaysLog.e(TAG, "unknow node??? index = " + i);
			}
		}

		return false;
	}


}


