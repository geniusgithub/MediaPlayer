package com.geniusgithub.common.cache;

import android.content.Context;
import android.util.Log;

import com.geniusgithub.common.util.FileHelper;

import java.io.File;


public abstract class AbstractFileCache {

	private static final String TAG = AbstractFileCache.class.getSimpleName();
	private String dirString;
	
	public AbstractFileCache(Context context) {
		
		dirString = getCacheDir();
		boolean ret = FileHelper.createDirectory(dirString);
		Log.i(TAG, "FileHelper.createDirectory:" + dirString + ", ret = " + ret);
	}
	
	public File getFile(String url) {
		File f = new File(getSavePath(url));
		return f;
	}
	
	public abstract String  getSavePath(String url);
	public abstract String  getCacheDir();

	public void clear() {
		FileHelper.deleteDirectory(dirString);
	}

}
