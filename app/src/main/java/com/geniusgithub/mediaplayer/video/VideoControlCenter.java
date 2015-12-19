package com.geniusgithub.mediaplayer.video;

import java.util.List;

import android.content.Context;

import com.geniusgithub.mediaplayer.player.IMediaOperator;
import com.geniusgithub.mediaplayer.player.VideoPlayEngineImpl;
import com.geniusgithub.mediaplayer.upnp.MediaItem;

public class VideoControlCenter implements IMediaOperator{

	private Context mContext;
	private int mCurPlayIndex = 0;
	private List<MediaItem> mVideoList;
	private VideoPlayEngineImpl mPlayerEngineImpl;
	private long playNextTimeMill = 0;
	
	public VideoControlCenter(Context context){
		mContext = context;
	}
	
	public void updateMediaInfo(int index, List<MediaItem> list){
		mCurPlayIndex = index;
		mVideoList = list;
	}
	
	public void bindVideoPlayEngine(VideoPlayEngineImpl object){
		mPlayerEngineImpl = object;
	}

	
	@Override
	public void exit() {
		mPlayerEngineImpl.exit();
	}

	@Override
	public void replay() {
		mPlayerEngineImpl.play();
	}

	@Override
	public void pause() {
		mPlayerEngineImpl.pause();
	}

	@Override
	public void stop() {
		mPlayerEngineImpl.stop();
	}

	@Override
	public void prev() {
		if (!isHaveFile())
		{
			return ;
		}
		
		mCurPlayIndex--;
		mCurPlayIndex = reviceIndex(mCurPlayIndex);
	
		mPlayerEngineImpl.playMedia(mVideoList.get(mCurPlayIndex));
	}

	@Override
	public boolean next() {
		if (!isHaveFile())
		{
			return false;
		}
		long curTimeMill =  System.currentTimeMillis();
		long timeInterfal = Math.abs(curTimeMill - playNextTimeMill);
		if (timeInterfal < 1000){
			return false;
		}
		playNextTimeMill = curTimeMill;
		
		mCurPlayIndex++;
		mCurPlayIndex = reviceIndex(mCurPlayIndex);
	
		mPlayerEngineImpl.playMedia(mVideoList.get(mCurPlayIndex));
		return true;
	}

	@Override
	public void skipTo(int time) {
		mPlayerEngineImpl.skipTo(time);
	}
	
	
	private boolean isHaveFile(){
		if (mVideoList != null && mVideoList.size() > 0){
			return true;
		}
		
		return false;
	}
	
	private int reviceIndex(int index)
	{
		if (index < 0)
		{
			index = mVideoList.size() - 1;
		}
		
		if (index >= mVideoList.size())
		{
			index = 0;
		}
		
		return index;
	}
}
