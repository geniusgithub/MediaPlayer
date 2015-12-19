package com.geniusgithub.mediaplayer.music;

import org.cybergarage.util.CommonLog;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.geniusgithub.mediaplayer.util.LogFactory;


public class LoaderHelper {

private static final CommonLog log = LogFactory.createLog();
	

	public LoaderHelper(){

	}

	public  static boolean syncDownLoadDrawable(final String requestUrl,final Handler handler,final int msgID){
		ITaskCompleteCallback callback = new ITaskCompleteCallback(){

			@Override
			public void onTaskComplete(Drawable drawable) {
				if (handler != null){
					Message msg = handler.obtainMessage(msgID);
					if (drawable != null){
						msg.obj = drawable;
					}else{
						msg.obj = null;
					}
					msg.sendToTarget();
				}
			}
			
		};
		syncDownLoadDrawable(requestUrl, callback);
		return true;	
	}
	
	public static boolean syncDownLoadDrawable(String requestUrl, ITaskCompleteCallback callback){
		InnerThread thread = new InnerThread(requestUrl, callback);
		thread.start();
		return true;	
	}
	
	public static interface ITaskCompleteCallback{
		public void onTaskComplete(Drawable drawable);
	}
	
	public static class InnerThread extends Thread{

		private ITaskCompleteCallback mCallback;
		private String mUri;
		public InnerThread(String uri, ITaskCompleteCallback callback){
			mCallback = callback;
			mUri = uri;
		}
		
		@Override
		public void run() {

			Drawable drawable = NetUtils.requestDrawableByUri(mUri);
			if (mCallback != null){
				mCallback.onTaskComplete(drawable);
			}
		}
		
	}
}
