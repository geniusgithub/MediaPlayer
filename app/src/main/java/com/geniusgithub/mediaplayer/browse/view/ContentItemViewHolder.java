package com.geniusgithub.mediaplayer.browse.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniusgithub.common.cache.ImageLoaderEx;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

public class ContentItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

	public	static Drawable foldIcon ;
	public	static Drawable musicIcon;
	public	static Drawable picIcon;
	public	static Drawable videoIcon;



	public static void  loadDefaultDrawable(Resources resource){
		foldIcon = resource.getDrawable(R.drawable.ic_menu_archive);
		musicIcon = resource.getDrawable(R.drawable.tab_icon_music);
		picIcon = resource.getDrawable(R.drawable.tab_icon_pic);
		videoIcon = resource.getDrawable(R.drawable.tab_icon_video);
	}

	public static interface onItemClickListener{
		public void onItemClick(int pos, MediaItem item);
	}


	public ImageView mImageView;
	public View mRootView;
	public TextView mTVName;
	public onItemClickListener mOnItemClickListener;
	private MediaItem mMediaItem;
	private int mPos;
	public ContentItemViewHolder(View itemView) {
		super(itemView);

		mImageView = (ImageView) itemView.findViewById(R.id.imageView);
		mTVName = (TextView) itemView.findViewById(R.id.tv_content);
		mRootView = itemView.findViewById(R.id.ll_root);
		mRootView.setOnClickListener(this);
	}

	public void bindInfo(int pos, MediaItem dataItem, ImageLoaderEx imageLoaderEx){
		mMediaItem = dataItem;
		mPos = pos;
		mTVName.setText(dataItem.getTitle());
		if (UpnpUtil.isAudioItem(dataItem)){
			mImageView.setImageDrawable(musicIcon);
		}else if (UpnpUtil.isVideoItem(dataItem)){
			mImageView.setImageDrawable(videoIcon);
		}else if (UpnpUtil.isPictureItem(dataItem)){
			imageLoaderEx.DisplayImage(dataItem.getRes(), mImageView, picIcon);
		}else{
			mImageView.setImageDrawable(foldIcon);
		}


	}

	public void setOnItemClickListener(onItemClickListener listener){
		mOnItemClickListener = listener;
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.ll_root:
				if (mOnItemClickListener != null){
					mOnItemClickListener.onItemClick(mPos, mMediaItem);
				}
				break;
		}

	}
}
