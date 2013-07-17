package com.geniusgithub.mediaplayer.video;

import org.cybergarage.util.CommonLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.music.LoaderHelper;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.VideoPlayEngineImpl;
import com.geniusgithub.mediaplayer.proxy.MediaManager;
import com.geniusgithub.mediaplayer.upnp.MediaItem;
import com.geniusgithub.mediaplayer.upnp.MediaItemFactory;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.DlnaUtils;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class VideoPlayerActivity extends Activity implements OnBufferingUpdateListener,
											OnSeekCompleteListener, OnErrorListener{

public static final String PLAY_INDEX = "player_index";
	
	private static final CommonLog log = LogFactory.createLog();
	
	private final static int REFRESH_CURPOS = 0x0001;
	private final static int REFRESH_SPEED = 0x0002;
	private final static int CHECK_DELAY = 0x0003;
	private final static int HIDE_TOOL = 0x0004;
	private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;
	

	private final static int HIDE_DELAY_TIME = 3000;
	

	private UIManager mUIManager;
	private VideoPlayEngineImpl mPlayerEngineImpl;
	private VideoPlayEngineListener mPlayEngineListener;
	private VideoControlCenter mVideoControlCenter;
	
	private Context mContext;
	private MediaItem mMediaInfo = new MediaItem();	
	private Handler mHandler;
	
	private AbstractTimer mPlayPosTimer;
	private AbstractTimer mNetWorkTimer;
	private CheckDelayTimer mCheckDelayTimer;
	
	private boolean isSurfaceCreate = false;
	private boolean isDestroy = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		log.e("onCreate");
		setContentView(R.layout.video_player_layout);
		setupsView();	
		initData();
		
		refreshIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		log.e("onNewIntent");
		refreshIntent(intent);

		super.onNewIntent(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	//	finish();
	}

	@Override
	protected void onDestroy() {
		log.e("onDestroy");
		isDestroy = true;
		mUIManager.unInit();
		mCheckDelayTimer.stopTimer();
		mNetWorkTimer.stopTimer();
		mPlayPosTimer.stopTimer();
		mVideoControlCenter.exit();
		super.onDestroy();

	}

	public void setupsView()
	{
		mContext = this;
		mUIManager = new UIManager();
	}
	
	public void initData(){
		mPlayPosTimer = new SingleSecondTimer(this);
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what)
				{
					case REFRESH_CURPOS:					
						refreshCurPos();
						break;
					case REFRESH_SPEED:
						refreshSpeed();
						break;
					case CHECK_DELAY:
						checkDelay();				
						break;
					case HIDE_TOOL:
						if (!mPlayerEngineImpl.isPause()){
							mUIManager.showControlView(false);
						}
						break;
				}
			}
			
		};
		
		mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);
		
		mNetWorkTimer = new SingleSecondTimer(this);
		mNetWorkTimer.setHandler(mHandler, REFRESH_SPEED);
		mCheckDelayTimer = new CheckDelayTimer(this);
		mCheckDelayTimer.setHandler(mHandler, CHECK_DELAY);

		mPlayerEngineImpl = new VideoPlayEngineImpl(this, mUIManager.holder);
		mPlayerEngineImpl.setOnBuffUpdateListener(this);
		mPlayerEngineImpl.setOnSeekCompleteListener(this);

		mPlayEngineListener = new VideoPlayEngineListener();
		mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);
		
		mVideoControlCenter = new VideoControlCenter(this);
		mVideoControlCenter.bindVideoPlayEngine(mPlayerEngineImpl);
		
		
		mNetWorkTimer.startTimer();
		mCheckDelayTimer.startTimer();
		
	
	}
	
	
	private void refreshIntent(Intent intent){
		log.e("refreshIntent");
		int curIndex = 0;
		if (intent != null){
			curIndex = intent.getIntExtra(PLAY_INDEX, 0);		
			mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
		}
		
		mVideoControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getVideoList());

		mUIManager.updateMediaInfoView(mMediaInfo);
		if (isSurfaceCreate){
			mPlayerEngineImpl.playMedia(mMediaInfo);
		}else{
			delayToPlayMedia(mMediaInfo);
		}
		
		mUIManager.showPrepareLoadView(true);
		mUIManager.showLoadView(false);
		mUIManager.showControlView(false);

	}	
	
	private void delayToPlayMedia(final MediaItem mMediaInfo){
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (!isDestroy){
					mPlayerEngineImpl.playMedia(mMediaInfo);
				}else{
					log.e("activity destroy...so don't playMedia...");
				}
			}
		}, 1000);
	}
	
	
	public boolean dispatchTouchEvent(MotionEvent ev) {

		int action = ev.getAction();
		int actionIdx = ev.getActionIndex();
		int actionMask = ev.getActionMasked();
	
		if(actionIdx == 0 && action == MotionEvent.ACTION_UP) {
			if(!mUIManager.isControlViewShow()) {	
				mUIManager.showControlView(true);	
				return true;
			}else{
				delayToHideControlPanel();
			}
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	private void removeHideMessage(){
		mHandler.removeMessages(HIDE_TOOL);
	}
	
	private void delayToHideControlPanel(){
		removeHideMessage();
		mHandler.sendEmptyMessageDelayed(HIDE_TOOL, HIDE_DELAY_TIME);
	}
	
	
	public void refreshCurPos(){
		int pos = mPlayerEngineImpl.getCurPosition();
	
		mUIManager.setSeekbarProgress(pos);
	
	}
	
	
	
	public void refreshSpeed(){
		if (mUIManager.isLoadViewShow()){
			float speed = CommonUtil.getSysNetworkDownloadSpeed();
			mUIManager.setSpeed(speed);
		}
	}
	
	public void checkDelay(){
		int pos = mPlayerEngineImpl.getCurPosition();

		boolean ret = mCheckDelayTimer.isDelay(pos);
		if (ret){
			mUIManager.showLoadView(true);
		}else{
			mUIManager.showLoadView(false);
		}
		
		mCheckDelayTimer.setPos(pos);
		
	}
	
	public void seek(int pos){
		mVideoControlCenter.skipTo(pos);
		mUIManager.setSeekbarProgress(pos);
		
	}

	private class VideoPlayEngineListener implements PlayerEngineListener
	{

		@Override
		public void onTrackPlay(MediaItem itemInfo) {
		
			mPlayPosTimer.startTimer();
			LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
			mUIManager.showPlay(false);
			mUIManager.showPrepareLoadView(false);
			mUIManager.showControlView(true);
		}

		@Override
		public void onTrackStop(MediaItem itemInfo) {

			mPlayPosTimer.stopTimer();
			mUIManager.showPlay(true);
			mUIManager.updateMediaInfoView(mMediaInfo);
			mUIManager.showLoadView(false);
		}

		@Override
		public void onTrackPause(MediaItem itemInfo) {
	
			mPlayPosTimer.stopTimer();
			mUIManager.showPlay(true);
		}

		@Override
		public void onTrackPrepareSync(MediaItem itemInfo) {

			mPlayPosTimer.stopTimer();
			mUIManager.updateMediaInfoView(itemInfo);
			mUIManager.showPlay(false);
			mUIManager.showPrepareLoadView(true);
			mUIManager.showControlView(false);
		}

		@Override
		public void onTrackPrepareComplete(MediaItem itemInfo) {

			mPlayPosTimer.stopTimer();
			int duration = mPlayerEngineImpl.getDuration();
			mUIManager.setSeekbarMax(duration);
			mUIManager.setTotalTime(duration);
			
		}
		
		@Override
		public void onTrackStreamError(MediaItem itemInfo) {
			log.e("onTrackStreamError");
			mPlayPosTimer.stopTimer();		
			mVideoControlCenter.stop();	
			mUIManager.showPlayErrorTip();
		}

		@Override
		public void onTrackPlayComplete(MediaItem itemInfo) {
			log.e("onTrackPlayComplete");
			boolean ret = mVideoControlCenter.next();
			if (!ret){
				mUIManager.showPlayErrorTip();
				mUIManager.updateMediaInfoView(itemInfo);
				mUIManager.showPlay(false);
				mUIManager.showPrepareLoadView(false);
				mUIManager.showControlView(true);
			}
		}

	

	}
	
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	//	log.e("onBufferingUpdate --> percen = " + percent + ", curPos = " + mp.getCurrentPosition());
	  
		int duration = mPlayerEngineImpl.getDuration();
		int time = duration * percent / 100;
		mUIManager.setSeekbarSecondProgress(time);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
	
		log.e("onSeekComplete ...");
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mUIManager.showPlayErrorTip();
		log.e("onError what = " + what + ", extra = " + extra);
		return false;
	}

	
	
	/*---------------------------------------------------------------------------*/
	class UIManager implements OnClickListener, OnSeekBarChangeListener, SurfaceHolder.Callback{
		
		public View mPrepareView;
		public TextView mTVPrepareSpeed;
		
		public View mLoadView;
		public TextView mTVLoadSpeed;
		
		public View mControlView;	
		public TextView mTitle;
	
		public ImageButton mBtnPlay;
		public ImageButton mBtnPause;
		public ImageButton mBtnPre;
		public ImageButton mBtnNext;
		public SeekBar mSeekBar;
		public TextView mTVCurTime;
		public TextView mTVTotalTime;
		
		private SurfaceView mSurfaceView;
		private SurfaceHolder holder = null;  
		
		public TranslateAnimation mHideDownTransformation;
		public AlphaAnimation mAlphaHideTransformation;
		
		
		public UIManager(){
			initView();
		}

		public void initView(){
			
			mPrepareView = findViewById(R.id.prepare_panel);
			mTVPrepareSpeed = (TextView) findViewById(R.id.tv_prepare_speed);
			
			mLoadView = findViewById(R.id.loading_panel);
			mTVLoadSpeed = (TextView) findViewById(R.id.tv_speed);
			
			mControlView = findViewById(R.id.control_panel);	
			
			mBtnPlay = (ImageButton) findViewById(R.id.btn_play);
			mBtnPause = (ImageButton) findViewById(R.id.btn_pause);
			mBtnPre = (ImageButton) findViewById(R.id.btn_playpre);
			mBtnNext = (ImageButton) findViewById(R.id.btn_playnext);
			mBtnPlay.setOnClickListener(this);
			mBtnPause.setOnClickListener(this);	
			mBtnPre.setOnClickListener(this);
			mBtnNext.setOnClickListener(this);
			
			mSeekBar = (SeekBar) findViewById(R.id.playback_seeker);
			mTVCurTime = (TextView) findViewById(R.id.tv_curTime);
			mTVTotalTime = (TextView) findViewById(R.id.tv_totalTime);
			mTitle = (TextView) findViewById(R.id.tv_title);
			setSeekbarListener(this);
			
			mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
			holder = mSurfaceView.getHolder();
		    holder.addCallback(this);  
		    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		    
		
			
			
			mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f,0.0f,200.0f);  
	    	mHideDownTransformation.setDuration(1000);
	    	
	    	mAlphaHideTransformation = new AlphaAnimation(1, 0);
	    	mAlphaHideTransformation.setDuration(1000);
	    	
		}

		
		public void unInit(){
			
		}
		
		public void showPrepareLoadView(boolean isShow){
			if (isShow){
				mPrepareView.setVisibility(View.VISIBLE);		
			}else{
				mPrepareView.setVisibility(View.GONE);
			}
		}
		
		public void showControlView(boolean show){
			if (show){
				mControlView.setVisibility(View.VISIBLE);
				delayToHideControlPanel();
			}else{
				mControlView.setVisibility(View.GONE);
			}
			
		}
		
		public void showLoadView(boolean isShow){
			if (isShow){
				mLoadView.setVisibility(View.VISIBLE);
			}else{
				if (mLoadView.isShown()){
					mLoadView.startAnimation(mAlphaHideTransformation);
					mLoadView.setVisibility(View.GONE);
				}
			}
		}
		
		private boolean isSeekbarTouch = false;	

		@Override
		public void onClick(View v) {

			switch(v.getId())
			{
				case R.id.btn_play:
					mVideoControlCenter.replay();
					break;
				case R.id.btn_pause:
					mVideoControlCenter.pause();
					break;
				case R.id.btn_playpre:
					mVideoControlCenter.prev();
					break;
				case R.id.btn_playnext:
					mVideoControlCenter.next();
					break;
			}
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
	
			mUIManager.setcurTime(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			isSeekbarTouch = true;
		
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			isSeekbarTouch = false;			
			seek(seekBar.getProgress());
		}
		
		
		public void showPlay(boolean bShow)
		{
			if (bShow)
			{
				mBtnPlay.setVisibility(View.VISIBLE);
				mBtnPause.setVisibility(View.INVISIBLE);
			}else{
				mBtnPlay.setVisibility(View.INVISIBLE);
				mBtnPause.setVisibility(View.VISIBLE);
			}
		}
		
		public void togglePlayPause(){
			if (mBtnPlay.isShown()){
				mVideoControlCenter.replay();
			}else{
				mVideoControlCenter.pause();
			}
		}
		
		public void setSeekbarProgress(int time)
		{
			if (!isSeekbarTouch)
			{
				mSeekBar.setProgress(time);	
			}
		}
		
		public void setSeekbarSecondProgress(int time)
		{
			mSeekBar.setSecondaryProgress(time);	
		}
		
		public void setSeekbarMax(int max){
			mSeekBar.setMax(max);
		}
		
		public void setcurTime(int curTime){
			String timeString = DlnaUtils.formateTime(curTime);
			mTVCurTime.setText(timeString);
		}
		
		public void setTotalTime(int totalTime){
			String timeString = DlnaUtils.formateTime(totalTime);
			mTVTotalTime.setText(timeString);
		}
		
		public void setTitle(String title){
			mTitle.setText(title);
		}
		
		public void updateMediaInfoView(MediaItem mediaInfo){
			setcurTime(0);
			setTotalTime(0);
			setSeekbarMax(100);
			setSeekbarProgress(0);
			setTitle(mediaInfo.getTitle());
		}
		
		public void setSpeed(float speed){
			String showString = (int)speed + "KB/" + getResources().getString(R.string.second);
			mTVPrepareSpeed.setText(showString);
			mTVLoadSpeed.setText(showString);
		}
		

		public void setSeekbarListener(OnSeekBarChangeListener listener)
		{
			mSeekBar.setOnSeekBarChangeListener(listener);
		}

		public boolean isControlViewShow(){
			return mControlView.getVisibility() == View.VISIBLE ? true : false;
		}
		
		public boolean isLoadViewShow(){
			if (mLoadView.getVisibility() == View.VISIBLE || 
					mPrepareView.getVisibility() == View.VISIBLE){
				return true;
			}
			
			return false;
		}
		
		public void showPlayErrorTip(){
			Toast.makeText(VideoPlayerActivity.this, R.string.toast_videoplay_fail, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			isSurfaceCreate = true;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			isSurfaceCreate = false;
		}
	}
}
