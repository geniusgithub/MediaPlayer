package com.geniusgithub.mediaplayer.player;


import org.cybergarage.util.CommonLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.view.SurfaceHolder;

import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;


public class VideoPlayEngineImpl extends AbstractMediaPlayEngine{
	
	private final CommonLog log = LogFactory.createLog();	
	private SurfaceHolder mHolder = null;  
	private OnBufferingUpdateListener mBufferingUpdateListener;
	private OnSeekCompleteListener mSeekCompleteListener;
	private OnErrorListener mOnErrorListener;
	
	public  VideoPlayEngineImpl(Context context, SurfaceHolder holder) {
		super(context);
	
		setHolder(holder);
	}
	
	public void setHolder(SurfaceHolder holder){
		mHolder = holder;
	}
	
	public void setOnBuffUpdateListener(OnBufferingUpdateListener listener){
		mBufferingUpdateListener = listener;
	}
	
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener){
		mSeekCompleteListener = listener;
	}
	
	public void setOnErrorListener(OnErrorListener listener){
		mOnErrorListener = listener;
	}
	
	@Override
	protected boolean prepareSelf() {

		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource(mMediaInfo.getRes());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
			if (mHolder != null){
				mMediaPlayer.setDisplay(mHolder);
			}
			if (mBufferingUpdateListener != null){
				mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			}
			if (mSeekCompleteListener != null){
				mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			}
			if (mOnErrorListener != null){
				mMediaPlayer.setOnErrorListener(mOnErrorListener);
			}
			mMediaPlayer.prepareAsync();
			log.e("mMediaPlayer.prepareAsync path = " + mMediaInfo.getRes());
			mPlayState = PlayState.MPS_PARESYNC;
			performPlayListener(mPlayState);
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

		mPlayState = PlayState.MPS_PARECOMPLETE;
		if (mPlayerEngineListener != null){
			mPlayerEngineListener.onTrackPrepareComplete(mMediaInfo);
		}
		
		if (mHolder != null){
			CommonUtil.ViewSize viewSize = CommonUtil.getFitSize(mContext, mp);
			mHolder.setFixedSize(viewSize.width, viewSize.height);
		}
		
		
		mMediaPlayer.start();
		
		mPlayState = PlayState.MPS_PLAYING;
		performPlayListener(mPlayState);
		
		return true;
	}

}
