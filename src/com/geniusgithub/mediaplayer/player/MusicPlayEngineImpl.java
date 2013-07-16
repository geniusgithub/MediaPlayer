package com.geniusgithub.mediaplayer.player;

import org.cybergarage.util.CommonLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;

import com.geniusgithub.mediaplayer.util.LogFactory;




public class MusicPlayEngineImpl extends AbstractMediaPlayEngine{

	private final CommonLog log = LogFactory.createLog();	
	
	private OnBufferingUpdateListener mBufferingUpdateListener;
	private OnSeekCompleteListener mSeekCompleteListener;
	private OnDataCaptureListener mDataCaptureListener;
	
	private Visualizer mVisualizer;
	
	public  MusicPlayEngineImpl(Context context) {
		super(context);
	
	}
	
	public void setOnBuffUpdateListener(OnBufferingUpdateListener listener){
		mBufferingUpdateListener = listener;
	}
	
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener){
		mSeekCompleteListener = listener;
	}
	
	public void setDataCaptureListener(OnDataCaptureListener listener){
		mDataCaptureListener = listener;
	}
	
	public boolean reInitVisualizer(int sID){
		releaseVisualizer();
		
		final int maxCR = Visualizer.getMaxCaptureRate();
		mVisualizer = new Visualizer(sID);
		mVisualizer.setCaptureSize(256);
		if (mDataCaptureListener != null){
			mVisualizer.setDataCaptureListener(mDataCaptureListener, maxCR/2, false, true);
		}
		
		return true;
	}
	
	public void releaseVisualizer(){
		if (mVisualizer != null) {
			mVisualizer.setEnabled(false);
			mVisualizer.release();
			mVisualizer = null;
		}
	}
	
	public void enableVisualizer(boolean flag){
		if (mVisualizer != null){
			mVisualizer.setEnabled(flag);
		}
	}
	
	
	@Override
	public void play() {
		super.play();
		enableVisualizer(true);
	}

	@Override
	public void pause() {
		super.pause();
		enableVisualizer(false);
	}

	@Override
	public void stop() {	
		super.stop();
		enableVisualizer(false);
	}
	
	@Override
	public void exit(){
		super.exit();
		releaseVisualizer();
	}
	
	
	@Override
	protected boolean prepareSelf() {
	
		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource(mMediaInfo.getRes());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  

			if (mBufferingUpdateListener != null){
				mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			}
			mMediaPlayer.prepareAsync();
			log.e("mMediaPlayer.prepareAsync path = " + mMediaInfo.getRes());

			mPlayState = PlayState.MPS_PARESYNC;
			performPlayListener(mPlayState);
			enableVisualizer(true);
	
		} catch (Exception e) {
			e.printStackTrace();
			mPlayState = PlayState.MPS_INVALID;
			performPlayListener(mPlayState);
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean prepareComplete(MediaPlayer mp) {
		log.e("prepareComplete");	
		mPlayState = PlayState.MPS_PARECOMPLETE;
		if (mPlayerEngineListener != null){
			mPlayerEngineListener.onTrackPrepareComplete(mMediaInfo);
		}
		
		mMediaPlayer.start();
		
		mPlayState = PlayState.MPS_PLAYING;
		performPlayListener(mPlayState);
		reInitVisualizer(mMediaPlayer.getAudioSessionId());
		enableVisualizer(true);
		return true;
	}
	
}
