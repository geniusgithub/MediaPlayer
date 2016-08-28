package com.geniusgithub.common.util;


public class FileManager {

	public static String getCacheFileSavePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/cache/";
		} else {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/cache/";
		}
	}
	
	public static String getDownloadFileSavePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/files/";
		}
	}
}
