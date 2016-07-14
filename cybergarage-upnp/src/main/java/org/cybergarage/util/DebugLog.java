// add by geniusgithub begin
package org.cybergarage.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DebugLog {
	private static final String TAG = "DebugLog";

	private static final String CONFIG_FILE = ".app-debug";

	public static boolean isLoggable = false;

	public static void init() {
		long startTime = System.currentTimeMillis();
		if(true) {
			isLoggable = true;
		} else {
			isLoggable = new File(Environment.getExternalStorageDirectory(), CONFIG_FILE).exists();
		}		
		
		// 性能调试Log
		AlwaysLog.d(TAG, "init debug log time " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * 设置是否调试
	 * 
	 * @param b
	 */
	public static void setLoggable(boolean b) {
		File f = new File(Environment.getExternalStorageDirectory(), CONFIG_FILE);
		isLoggable = b;
		if(isLoggable){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// Debug开关无法打开
				AlwaysLog.e(TAG, "setDebuggable failed " + e.getLocalizedMessage());
			}
		} else {
			f.delete();
		}
	}
	
	public static void d(String TAG, String log) {
		if (isLoggable) {
			Log.d(TAG, log);
		}
	}
	
    public static void d(String TAG, String log, Throwable tr) {
        if (isLoggable) {
            Log.d(TAG, log, tr);
        }
    }
    
	public static void i(String TAG, String log) {
		if (isLoggable) {
			Log.i(TAG, log);
		}
	}

    public static void i(String TAG, String log, Throwable tr) {
        if (isLoggable) {
            Log.i(TAG, log, tr);
        }
    }
    
	public static void w(String TAG, String log) {
		if (isLoggable) {
			Log.w(TAG, log);
		}
	}

    public static void w(String TAG, String log, Throwable tr) {
        if (isLoggable) {
            Log.w(TAG, log, tr);
        }
    }
    
	public static void e(String TAG, String log) {
		Log.e(TAG, log);
	}
	
    public static void e(String TAG, String log, Throwable tr) {
    	Log.e(TAG, log, tr);
    }
}
// add by geniusgithub end
