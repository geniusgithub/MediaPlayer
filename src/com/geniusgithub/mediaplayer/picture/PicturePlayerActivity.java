package com.geniusgithub.mediaplayer.picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.proxy.MediaManager;
import com.geniusgithub.mediaplayer.upnp.MediaItem;
import com.geniusgithub.mediaplayer.upnp.MediaItemFactory;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PicturePlayerActivity extends Activity implements DownLoadHelper.IDownLoadCallback,
																		PictureUtil.IScalCallback{
	private static final CommonLog log = LogFactory.createLog();
	
	public static final String PLAY_INDEX = "player_index";
	
	private UIManager mUIManager;
	private DelCacheFileManager mDelCacheFileManager;
	private PictureControlCenter mControlCenter;
	
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	
	private MediaItem mMediaInfo = new MediaItem();	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picture_player_layout);
		
		initView();
		initData();
		
		refreshIntent(getIntent());
	}
	
	private void initView(){
		mUIManager = new UIManager();
		mDelCacheFileManager = new DelCacheFileManager();
	}

	
	private void initData(){
	
		mControlCenter = new PictureControlCenter(this);
		mControlCenter.init();
		mControlCenter.setDownLoadCallback(this);
		
		mScreenWidth =  CommonUtil.getScreenWidth(this);
		mScreenHeight = CommonUtil.getScreenHeight(this);	
	}
	
	private void unInitData(){
		mDelCacheFileManager.start(FileManager.getSaveRootDir());
		mControlCenter.unInit();
	}

	@Override
	protected void onDestroy() {
		unInitData();
		super.onDestroy();
	}

	
	private void refreshIntent(Intent intent){
		log.e("refreshIntent");
		int curIndex = 0;
		if (intent != null){
			curIndex = intent.getIntExtra(PLAY_INDEX, 0);		
			mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
		}
		
		mControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getPictureList());
		
		mControlCenter.play(curIndex);
		mUIManager.showProgress(true);
	}	
	
	class UIManager implements OnClickListener{
		
		public ImageView mImageView;
		public ImageButton mBtnPre;
		public ImageButton mBtnNext;
		public ImageButton mBtnPlay;
		public ImageButton mBtnPause;
		public View mLoadView;
		
		public Bitmap recycleBitmap;
		public boolean mIsScalBitmap = false;
		
		
		public UIManager(){
			initView();
		}
		
		
		private void initView() {
			mImageView = (ImageView) findViewById(R.id.imageview);
			mLoadView = findViewById(R.id.show_load_progress);
		
			
			mBtnPre = (ImageButton) findViewById(R.id.btn_playpre);
			mBtnNext = (ImageButton) findViewById(R.id.btn_playnext);
			mBtnPlay = (ImageButton) findViewById(R.id.btn_play);
			mBtnPause = (ImageButton) findViewById(R.id.btn_pause);
			mBtnPre.setOnClickListener(this);
			mBtnNext.setOnClickListener(this);
			mBtnPlay.setOnClickListener(this);
			mBtnPause.setOnClickListener(this);
		}
		
		public void setBitmap(Bitmap bitmap){
			if (recycleBitmap != null && !recycleBitmap.isRecycled()) {
				mImageView.setImageBitmap(null);
				recycleBitmap.recycle();
				recycleBitmap = null;
			}
						
			if (mIsScalBitmap) {
				mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
						
			recycleBitmap = bitmap;
			mImageView.setImageBitmap(recycleBitmap);
			
	//		log.e("	mImageView.setImageBitmap over...");
		}
		
		public void showProgress(boolean bShow)
		{
			if (bShow){
				mLoadView.setVisibility(View.VISIBLE);
			} else{
				mLoadView.setVisibility(View.GONE);
			}		
		}
		
		public void showLoadFailTip(){
			showToask(R.string.load_image_fail);
		}
		
		public void showParseFailTip(){
			showToask(R.string.parse_image_fail);
		}
		
		private void showToask(int tip) {
			Toast.makeText(PicturePlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_playpre:
				mControlCenter.prev();
				break;
			case R.id.btn_playnext:
				mControlCenter.next();
				break;
			case R.id.btn_play:
				mControlCenter.startAutoPlay(true);
				togglePlayPause();
				break;
			case R.id.btn_pause:
				mControlCenter.startAutoPlay(false);
				togglePlayPause();
				break;
			default:
				break;
			}
			
		}
		
		public void togglePlayPause(){
			if (mBtnPlay.isShown()){
				mBtnPlay.setVisibility(View.INVISIBLE);
				mBtnPause.setVisibility(View.VISIBLE);
			}else{
				mBtnPlay.setVisibility(View.VISIBLE);
				mBtnPause.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	
	
	class DelCacheFileManager implements Runnable
	{
		private Thread mThread;
		private String mFilePath;
		
		public DelCacheFileManager()
		{
			
		}
		
		@Override
		public void run() {
			
			long time = System.currentTimeMillis();
			log.e("DelCacheFileManager run...");
			try {
				FileHelper.deleteDirectory(mFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long interval = System.currentTimeMillis() - time;
			log.e("DelCacheFileManager del over, cost time = " + interval);
		}
		
		public boolean start(String directory)
		{		
			if (mThread != null)
			{
				if (mThread.isAlive())
				{
					return false;
				}			
			}
			mFilePath = directory;	
			mThread = new Thread(this);			
			mThread.start();	
			
			return true;
		}
		
	}

	@Override
	public void downLoadComplete(boolean isSuccess, String savePath) {

		onTransDelLoadResult(isSuccess, savePath);
	}
	
	private void onTransDelLoadResult(final boolean isSuccess,final String savePath){
	
		final Bitmap bitmap = PictureUtil.decodeOptionsFile(savePath, mScreenWidth, mScreenHeight, this);
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mUIManager.showProgress(false);
				
				if (!isSuccess){
					mUIManager.showLoadFailTip();
					return ;
				}
				
			
				if (bitmap == null){
					mUIManager.showParseFailTip();
					return ;
				}
				
				mUIManager.setBitmap(bitmap);
			}
		});
	
	
	}

	@Override
	public void isScalBitmap(boolean flag) {
		mUIManager.mIsScalBitmap = flag;
	}

	@Override
	public void startDownLoad() {
		mUIManager.showProgress(true);
	}
	
	
		
}