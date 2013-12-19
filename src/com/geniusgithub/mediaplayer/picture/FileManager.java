package com.geniusgithub.mediaplayer.picture;

import com.geniusgithub.mediaplayer.util.CommonUtil;



public class FileManager {

	public static String getSaveRootDir() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "icons/";
		} else {
			return CommonUtil.getRootFilePath() + "com.geniusgithub/mediaplayer/icons/";
		}
	}
	
	public static String getSaveFullPath(String uri) {
		return getSaveRootDir() + getFormatUri(uri);
	}
	

	public static String getFormatUri(String uri)
	{
		uri  = uri.replace("/", "_");
		uri  = uri.replace(":", "");	
		uri  = uri.replace("?", "_");
		uri  = uri.replace("%", "_");	
		
		int length = uri.length();
		if (length > 150)
		{
			uri = uri.substring(length - 150);
		}
		
		
		return uri;
	}
	
}
