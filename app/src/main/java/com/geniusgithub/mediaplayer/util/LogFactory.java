package com.geniusgithub.mediaplayer.util;

import org.cybergarage.util.CommonLog;


public class LogFactory {
	private static final String TAG = "MediaPlayer";
	private static CommonLog log = null;

	public static CommonLog createLog() {
		if (log == null) {
    		log = new CommonLog();
		}
		
		log.setTag(TAG);
		return log;
	}
	
	public static CommonLog createLog(String tag) {
		if (log == null) {
			log = new CommonLog();
		}
		
		if (tag == null || tag.length() < 1) {
    		log.setTag(TAG);
		} else {
			log.setTag(tag);
		}
		return log;
	}
}