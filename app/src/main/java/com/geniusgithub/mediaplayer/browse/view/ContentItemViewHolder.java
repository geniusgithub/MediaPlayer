package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.component.ImageLoader;
import com.geniusgithub.mediaplayer.dlna.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

	public	static Drawable foldIcon ;
	public	static Drawable musicIcon;
	public	static Drawable picIcon;
	public	static Drawable videoIcon;

	private Context mContext;

	public static void  loadDefaultDrawable(Resources resource){
		foldIcon = resource.getDrawable(R.drawable.ic_menu_archive);
		musicIcon = resource.getDrawable(R.drawable.tab_icon_music);
		picIcon = resource.getDrawable(R.drawable.tab_icon_pic);
		videoIcon = resource.getDrawable(R.drawable.tab_icon_video);
	}

	public static interface onItemClickListener{
		public void onItemClick(int pos, MediaItem item);
	}

	@BindView(R.id.imageView)
	public ImageView mImageView;

	@BindView(R.id.ll_root)
	public View mRootView;

	@BindView(R.id.tv_content)
	public TextView mTVName;

	public onItemClickListener mOnItemClickListener;
	private MediaItem mMediaItem;
	private int mPos;

	public ContentItemViewHolder(Context context, View itemView) {
		super(itemView);
		mContext = context;
		ButterKnife.bind(this, itemView);
		mRootView.setOnClickListener(this);
	}

	public void bindInfo(int pos, MediaItem dataItem, boolean isbusy){
		mMediaItem = dataItem;
		mPos = pos;
		mTVName.setText(dataItem.getTitle());
		if (UpnpUtil.isAudioItem(dataItem)){
			ImageLoader.loadThumail(mContext, dataItem.getAlbumUri(), mImageView, musicIcon);
		}else if (UpnpUtil.isVideoItem(dataItem)){
		//	mImageView.setImageDrawable(videoIcon);
			ImageLoader.loadThumail(mContext, dataItem.getRes(), mImageView, videoIcon);
		}else if (UpnpUtil.isPictureItem(dataItem)){
			ImageLoader.loadThumail(mContext, dataItem.getRes(), mImageView, picIcon);
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
