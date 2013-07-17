package com.geniusgithub.mediaplayer.music;


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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.MusicPlayEngineImpl;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.proxy.MediaManager;
import com.geniusgithub.mediaplayer.upnp.MediaItem;
import com.geniusgithub.mediaplayer.upnp.MediaItemFactory;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.DlnaUtils;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class MusicPlayerActivity extends Activity implements OnBufferingUpdateListener,
												OnSeekCompleteListener, OnErrorListener{

	public static final String PLAY_INDEX = "player_index";
	
	private static final CommonLog log = LogFactory.createLog();
	
	private final static int REFRESH_CURPOS = 0x0001;
	private final static int REFRESH_SPEED = 0x0002;
	private final static int CHECK_DELAY = 0x0003;
	private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;
	
	

	private UIManager mUIManager;
	private MusicPlayEngineImpl mPlayerEngineImpl;
	private MusicPlayEngineListener mPlayEngineListener;
	private MusicControlCenter mMusicControlCenter;
	
	private Context mContext;
	private MediaItem mMediaInfo = new MediaItem();	
	private Handler mHandler;
	
	private AbstractTimer mPlayPosTimer;
	private AbstractTimer mNetWorkTimer;
	private CheckDelayTimer mCheckDelayTimer;
	
	private boolean isDestroy = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		log.e("onCreate");
		setContentView(R.layout.music_player_layout);
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
		mMusicControlCenter.exit();
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
					case LOAD_DRAWABLE_COMPLETE:
						Object object = msg.obj;
						Drawable drawable = null;
						if (object != null){
							drawable = (Drawable) object;
						}
						onLoadDrawableComplete(drawable);
						break;
				}
			}
			
		};
		
		mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);
		
		mNetWorkTimer = new SingleSecondTimer(this);
		mNetWorkTimer.setHandler(mHandler, REFRESH_SPEED);
		mCheckDelayTimer = new CheckDelayTimer(this);
		mCheckDelayTimer.setHandler(mHandler, CHECK_DELAY);

		mPlayerEngineImpl = new MusicPlayEngineImpl(this);
		mPlayerEngineImpl.setOnBuffUpdateListener(this);
		mPlayerEngineImpl.setOnSeekCompleteListener(this);
		mPlayerEngineImpl.setDataCaptureListener(mUIManager);
		mPlayEngineListener = new MusicPlayEngineListener();
		mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);
		
		mMusicControlCenter = new MusicControlCenter(this);
		mMusicControlCenter.bindMusicPlayEngine(mPlayerEngineImpl);
		
		
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
		
		mMusicControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getMusicList());

		mUIManager.updateMediaInfoView(mMediaInfo);
		mPlayerEngineImpl.playMedia(mMediaInfo);
		
		mUIManager.showPrepareLoadView(true);
		mUIManager.showLoadView(false);
		mUIManager.showControlView(false);

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
	
	public void onLoadDrawableComplete(Drawable drawable){
		if (isDestroy || drawable == null){
			return ;
		}
		
		mUIManager.updateAlbumPIC(drawable);
		
	}
	
	public void seek(int pos){
		mMusicControlCenter.skipTo(pos);
		mUIManager.setSeekbarProgress(pos);
		
	}

	private class MusicPlayEngineListener implements PlayerEngineListener
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
			mMusicControlCenter.stop();	
			mUIManager.showPlayErrorTip();
		}

		@Override
		public void onTrackPlayComplete(MediaItem itemInfo) {
			log.e("onTrackPlayComplete");
			boolean ret = mMusicControlCenter.next();
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
	class UIManager implements OnClickListener, OnSeekBarChangeListener, OnDataCaptureListener{
		
		public View mPrepareView;
		public TextView mTVPrepareSpeed;
		
		public View mLoadView;
		public TextView mTVLoadSpeed;
		
		public View mControlView;	
		public TextView mTVSongName;
		public TextView mTVArtist;
		public TextView mTVAlbum;
	
		public ImageButton mBtnPlay;
		public ImageButton mBtnPause;
		public ImageButton mBtnPre;
		public ImageButton mBtnNext;
		public SeekBar mSeekBar;
		public TextView mTVCurTime;
		public TextView mTVTotalTime;
		public VisualizerView mVisualizerView;
		public ImageView mIVAlbum; 
		
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
			mTVSongName = (TextView) findViewById(R.id.tv_title);
			mTVArtist = (TextView) findViewById(R.id.tv_artist);
			mTVAlbum = (TextView) findViewById(R.id.tv_album);
			
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
			mVisualizerView = (VisualizerView) findViewById(R.id.mp_freq_view);
			mIVAlbum = (ImageView) findViewById(R.id.iv_album);
			setSeekbarListener(this);
		
		    
			mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f,0.0f,200.0f);  
	    	mHideDownTransformation.setDuration(1000);
	    	
	    	mAlphaHideTransformation = new AlphaAnimation(1, 0);
	    	mAlphaHideTransformation.setDuration(1000);
	    	
	    	updateAlbumPIC(getResources().getDrawable(R.drawable.mp_music_default));
		}

		
		public void unInit(){
			
		}

		public void updateAlbumPIC(Drawable drawable){
			Bitmap bitmap = ImageUtils.createRotateReflectedMap(mContext, drawable);
			if (bitmap != null){
				mIVAlbum.setImageBitmap(bitmap);
			}
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
					mMusicControlCenter.replay();
					break;
				case R.id.btn_pause:
					mMusicControlCenter.pause();
					break;
				case R.id.btn_playpre:
					mMusicControlCenter.prev();
					break;
				case R.id.btn_playnext:
					mMusicControlCenter.next();
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
				mMusicControlCenter.replay();
			}else{
				mMusicControlCenter.pause();
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
		
		public void updateMediaInfoView(MediaItem mediaInfo){
			setcurTime(0);
			setTotalTime(0);
			setSeekbarMax(100);
			setSeekbarProgress(0);
	
			mTVSongName.setText(mediaInfo.getTitle());
			mTVArtist.setText(mediaInfo.getArtist());
			mTVAlbum.setText(mediaInfo.getAlbum());
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
			Toast.makeText(MusicPlayerActivity.this, R.string.toast_musicplay_fail, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFftDataCapture(Visualizer visualizer, byte[] fft,
				int samplingRate) {
			mVisualizerView.updateVisualizer(fft);		
		}

		@Override
		public void onWaveFormDataCapture(Visualizer visualizer,
				byte[] waveform, int samplingRate) {
			mVisualizerView.updateVisualizer(waveform);
		}
	}

}
