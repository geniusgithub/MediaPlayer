package com.geniusgithub.common.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.geniusgithub.common.util.AlwaysLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoaderEx {
	
	private final static String TAG = ImageLoaderEx.class.getSimpleName();

	private MemoryCache memoryCache = new MemoryCache();
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	private ExecutorService executorService;
	
	private int requestSize = 120;
	
	private Bitmap mDefaultBitmap = null;
	
	private boolean mIsLoadLocalBitmapQuick = false;
	private Handler mHandler;
	
	
	public ImageLoaderEx(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		mHandler = new Handler(){

		};
	}
	
	public void setScaleParam(int request_size){
		requestSize = request_size;
		AlwaysLog.e(TAG, "setScaleParam requestSize = " + requestSize);
	}
	
	public void setLoadLocalBitmapQuick(boolean flag){
		mIsLoadLocalBitmapQuick = flag;
	}
	
	public void setDefaultBitmap(Drawable drawable){
		if (drawable != null){
			BitmapDrawable bd = (BitmapDrawable) drawable;
			mDefaultBitmap = bd.getBitmap();
		}
	}

	// 最主要的方法
	public boolean DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		if (url == null || url.length() < 1 || imageView == null){
			return false;
		}

		
		imageViews.put(imageView, url);
		// 先从内存缓存中查找

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
			return true;	
		}
			
		// 如果直接从缓存文件加载
		if (mIsLoadLocalBitmapQuick){
			File f = fileCache.getFile(url);
			
			// 先从文件缓存中查找是否有
			Bitmap b = null;
			if (f != null && f.exists()){
				b = decodeFile(f);
			}
			if (b != null){
				imageView.setImageBitmap(b);
				return true;
			}
		}
		if (mDefaultBitmap != null){
			
			imageView.setImageBitmap(mDefaultBitmap);
		}
		if (!isLoadOnlyFromCache){			
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView);
		}
		
		return false;
		
	}
	
	public boolean syncLoadBitmap(String url, ImageView imageView){
		if (url == null || url.length() < 1 || imageView == null){
			return false;
		}
		
		queuePhoto(url, imageView);
		return true;
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);
		
		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}
		// 最后从指定的url中下载图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
		
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requestSize
						|| height_tmp / 2 < requestSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			
		//	log.e("w = " + width_tmp + ", h = " + height_tmp + ", scale = " + scale);
			
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {

			if (imageViewReused(photoToLoad)){
				return;
			}

			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad)){

				return;
			}

			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			mHandler.post(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
	
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e("", "CopyStream catch Exception...");
		}
	}
}