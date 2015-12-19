package com.geniusgithub.mediaplayer.picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureUtil {

	private static final CommonLog log = LogFactory.createLog();

	public static interface IScalCallback{
		public void isScalBitmap(final boolean flag);
	}
	
	public static Bitmap decodeOptionsFile(String filePath, int screenWidth, int screenHeight, IScalCallback callback) {
		  try {
		  	File file = new File(filePath);
		      BitmapFactory.Options o = new BitmapFactory.Options();
		      o.inJustDecodeBounds = true;
		      BitmapFactory.decodeStream(new FileInputStream(file),null,o);         
		      int width_tmp=o.outWidth, height_tmp=o.outHeight;
		      int scale = 1;
		      if (width_tmp <= screenWidth && height_tmp <= screenHeight)
		      {
		      	scale = 1;
		      	callback.isScalBitmap(false);
		      }else{
		      	double widthFit = width_tmp * 1.0 / screenWidth;
		          double heightFit = height_tmp * 1.0 / screenHeight;
		          double fit = widthFit > heightFit ? widthFit : heightFit; 
		          scale = (int) (fit + 0.5);    
		      		callback.isScalBitmap(true);
		      }
		      Bitmap bitmap = null;
		      if(scale == 1)
		      { 	
		      	bitmap =  BitmapFactory.decodeStream(new FileInputStream(file));
		      }else{
		      	BitmapFactory.Options o2 = new BitmapFactory.Options();
		          o2.inSampleSize = scale;
		          bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
		      }
		      
		      return bitmap;
		      
		  } catch (FileNotFoundException e) {
		  		log.e("fileNotFoundException, e: " + e.toString());
		  }
		  return null;
	}
}
