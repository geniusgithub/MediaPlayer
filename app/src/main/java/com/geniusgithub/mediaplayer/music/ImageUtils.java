package com.geniusgithub.mediaplayer.music;

import org.cybergarage.util.CommonLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.geniusgithub.mediaplayer.util.LogFactory;


public class ImageUtils {
	private static final CommonLog log = LogFactory.createLog();
	
	public static Bitmap createRotateReflectedMap(Bitmap originalBitmap) {
		float width = ((float)200) / (originalBitmap.getWidth());
		float height =  ((float)200) / (originalBitmap.getHeight());
		Matrix matrix = new Matrix();
		matrix.postScale(width, height);
		originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(),
				                             matrix, true);
		Bitmap bitmap = createReflectedImage(originalBitmap);
		bitmap = createRotateImage(bitmap);
		return bitmap;
	}

	public static Bitmap createRotateReflectedMap(Context ctx, Drawable resId) {

		Bitmap bitmap = ((BitmapDrawable)resId).getBitmap();
		if (bitmap != null) {
			log.e("bitmap is not null");
			return createRotateReflectedMap(bitmap);
		}
		return null;
	}

	public static Bitmap createRotateImage(Bitmap originalBitmap) {
		Camera camera = new Camera();
		camera.save();
		camera.rotateY(10f);
		Matrix mMatrix = new Matrix();
		camera.getMatrix(mMatrix);
		camera.restore();

		Bitmap bm = Bitmap.createBitmap(originalBitmap, 0, 0,
				originalBitmap.getWidth(), originalBitmap.getHeight(), mMatrix,
				true);
		//Bitmap bm = Bitmap.createBitmap(originalBitmap, 0, 0,270,270,mMatrix,true);
		return bm;
	}

	public static Bitmap createReflectedImage(Bitmap originalBitmap) {
		final int reflectionGap = 4;

		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();
		
	    
		Matrix matrix = new Matrix();
		
		matrix.preScale(1, -1);
		Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0,
				height / 2, width, height / 2, matrix, false);
		Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height
				+ height / 2 + reflectionGap), Config.ARGB_8888);

		Canvas canvas = new Canvas(withReflectionBitmap);
		canvas.drawBitmap(originalBitmap, 0, 0, null);

		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalBitmap.getHeight(), 0,
				withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff,
				TileMode.MIRROR);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(),
				paint);

		return withReflectionBitmap;
	}
}
