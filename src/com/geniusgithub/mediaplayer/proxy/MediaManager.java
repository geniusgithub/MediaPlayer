package com.geniusgithub.mediaplayer.proxy;

import java.util.ArrayList;
import java.util.List;

import com.geniusgithub.mediaplayer.upnp.MediaItem;


public class MediaManager {

	private static MediaManager mInstance;
	
	private List<MediaItem> mMusicList;
	private List<MediaItem> mVideoList;
	private List<MediaItem> mPictureist;
	
	public synchronized static MediaManager getInstance(){
		if (mInstance == null){
			mInstance = new MediaManager();
		}
		return mInstance;
	}
	
	private MediaManager()
	{
		mMusicList = new ArrayList<MediaItem>();
		mVideoList = new ArrayList<MediaItem>();
		mPictureist = new ArrayList<MediaItem>();
	}
	
	public void setMusicList(List<MediaItem> list){
		if (list != null){
			mMusicList = list;
		}
	
	}	
	public List<MediaItem> getMusicList(){
		return mMusicList;
	}	
	public void clearMusicList(){
		mMusicList = new ArrayList<MediaItem>();
	}
	
	public void setVideoList(List<MediaItem> list){
		if (list != null){
			mVideoList = list;
		}
	
	}	
	public List<MediaItem> getVideoList(){
		return mVideoList;
	}	
	public void clearVideoList(){
		mVideoList = new ArrayList<MediaItem>();
	}
	
	public void setPictureList(List<MediaItem> list){
		if (list != null){
			mPictureist = list;
		}
	
	}	
	public List<MediaItem> getPictureList(){
		return mPictureist;
	}	
	public void clearPictureList(){
		mPictureist = new ArrayList<MediaItem>();
	}
	
}
