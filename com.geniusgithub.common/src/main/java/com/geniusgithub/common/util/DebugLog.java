package com.geniusgithub.common.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

/**
 * Ideafriend默认的Log打印工具，需要打开Debug开关才能看到。
 * 
 * <h3>使用范围：</h3>
 * <p>
 * 不能使用<code>android.util.Log</code>类，要使用这个类替代。
 * 
 * <h3>Log打开方法：</h3>
 * 
 * <h4>方法一：</h4>
 * <ol>
 * 进入拨号盘，输入“*#*#277564#*#*”（对应英文为“*#*#applog#*#*”）选择“打开 app log”。
 * </ol>
 * 
 * <h4>方法二：</h4> 
 * <ol>
 * <li>在手机默认存储（路径为/sdcard）下新建一个名为.app-debug空的隐藏文件。</li>
 * <li>强制停止ideafriend进程</li>
 * </ol>
 * 
 * @see AlwaysLog
 * @see DebugLog
 * @see DynamicLog
 */
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
