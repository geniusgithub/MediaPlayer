package com.geniusgithub.common.util;


public class FileManager {


	public static String getBrowseCacheRootDir() {
		return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/BrowseCache/";
	}


	public static String getBrowseCacheFullPath(String uri) {
		return getBrowseCacheRootDir() + getFormatUri(uri);
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

/*	public static String getDownloadFileSavePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.geniusgithub.mediaplayer/files/";
		}
	}*/








}
