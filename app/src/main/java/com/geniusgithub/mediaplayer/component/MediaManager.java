package com.geniusgithub.mediaplayer.component;

import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.util.UpnpUtil;

import java.util.ArrayList;
import java.util.List;


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
	
	public int filterMusicList(List<MediaItem> list, int index){
		MediaFilter mFilter = new AudioFilter();
		mMusicList.clear();
		return mFilter.filter(list, index, mMusicList);
	}

	public List<MediaItem> getMusicList(){
		return mMusicList;
	}	
	public void clearMusicList(){
		mMusicList = new ArrayList<MediaItem>();
	}


	public int filterVideoList(List<MediaItem> list, int index){
		MediaFilter mFilter = new VideoFilter();
		mVideoList.clear();
		return mFilter.filter(list, index, mVideoList);
	}

	public List<MediaItem> getVideoList(){
		return mVideoList;
	}	
	public void clearVideoList(){
		mVideoList = new ArrayList<MediaItem>();
	}


	public int filterPictureList(List<MediaItem> list, int index){
		MediaFilter mFilter = new PhotoFilter();
		mPictureist.clear();
		return mFilter.filter(list, index, mPictureist);
	}

	public List<MediaItem> getPictureList(){
		return mPictureist;
	}	
	public void clearPictureList(){
		mPictureist = new ArrayList<MediaItem>();
	}



	public static abstract class MediaFilter{

		public abstract boolean isFilterSuccess(MediaItem item);

		public int filter(List<MediaItem> inList, int selIndex, List<MediaItem> outList) {
			int resultIndex = selIndex;
			int index = 0;
			for (MediaItem item : inList) {
				if (isFilterSuccess(item)){
					outList.add(item);
					if (index == selIndex){
						resultIndex = outList.size() - 1;
					}
				}

				index++;
			}

			return resultIndex;
		}
	}


	public static class AudioFilter extends MediaFilter{

		@Override
		public boolean isFilterSuccess(MediaItem item) {
			return UpnpUtil.isAudioItem(item);
		}
	}

	public static class VideoFilter extends MediaFilter{

		@Override
		public boolean isFilterSuccess(MediaItem item) {
			return UpnpUtil.isVideoItem(item);
		}
	}

	public static class PhotoFilter extends MediaFilter{

		@Override
		public boolean isFilterSuccess(MediaItem item) {
			return UpnpUtil.isPictureItem(item);
		}
	}
}
