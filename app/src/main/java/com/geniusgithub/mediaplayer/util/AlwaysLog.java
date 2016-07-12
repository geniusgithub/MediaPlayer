package com.geniusgithub.mediaplayer.util;

import android.util.Log;

/**
 * 强制Log打印，不受开关控制。
 * 
 * <h3>使用范围：</h3>
 * <p>
 * 在必需要打印的情况下，才使用。
 * 
 * <h3>注意事项：</h3>
 * <p>
 * 使用该Log工具必需添加注释，说明为什么这个地方要使用强制打印。请参考示例
 * 
 * <h3>示例：</h3>
 * <p>
 * <code>
 * // 该处为framework接口，必需打印 <br>
 * AlwaysLog.i(TAG, “onCreate start”);
 * </code>
 * 
 * @see AlwaysLog
 * @see DebugLog
 * @see DynamicLog
 */
public class AlwaysLog {
	public static void d(String TAG, String log) {
		Log.d(TAG, log);
	}

	public static void i(String TAG, String log) {
		Log.i(TAG, log);
	}

	public static void w(String TAG, String log) {
		Log.w(TAG, log);
	}

	public static void e(String TAG, String log) {
		Log.e(TAG, log);
	}
}
