package com.geniusgithub.mediaplayer.player.music;

import android.content.Context;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.player.common.IMediaOperator;

import java.util.List;

public class MusicControlCenter implements IMediaOperator {

	private Context mContext;
	private int mCurPlayIndex = 0;
	private List<MediaItem> mMusicList;
	private MusicPlayEngineImpl mPlayerEngineImpl;
	private long playNextTimeMill = 0;
	
	public MusicControlCenter(Context context){
		mContext = context;
	}
	
	public void updateMediaInfo(int index, List<MediaItem> list){
		mCurPlayIndex = index;
		mMusicList = list;
	}
	
	public void bindMusicPlayEngine(MusicPlayEngineImpl object){
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
	
		mPlayerEngineImpl.playMedia(mMusicList.get(mCurPlayIndex));
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
	
		mPlayerEngineImpl.playMedia(mMusicList.get(mCurPlayIndex));
		return true;
	}

	@Override
	public void skipTo(int time) {
		mPlayerEngineImpl.skipTo(time);
	}
	
	
	private boolean isHaveFile(){
		if (mMusicList != null && mMusicList.size() > 0){
			return true;
		}
		
		return false;
	}
	
	private int reviceIndex(int index)
	{
		if (index < 0)
		{
			index = mMusicList.size() - 1;
		}
		
		if (index >= mMusicList.size())
		{
			index = 0;
		}
		
		return index;
	}

}
