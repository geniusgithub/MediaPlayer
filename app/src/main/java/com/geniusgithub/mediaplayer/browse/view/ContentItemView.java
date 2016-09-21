package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.adapter.IBaseAdapterView;
import com.geniusgithub.mediaplayer.base.adapter.OnItemClickListener;
import com.geniusgithub.mediaplayer.component.ImageLoader;
import com.geniusgithub.mediaplayer.dlna.UpnpUtil;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentItemView extends LinearLayout implements IBaseAdapterView<MediaItem>{

    public static interface onContentItemClickListener extends OnItemClickListener<MediaItem> {
    }

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

    @BindView(R.id.imageView)
    public ImageView mImageView;


    @BindView(R.id.tv_content)
    public TextView mTVName;

    private MediaItem mMediaItem;

    public ContentItemView(Context context) {
        super(context);
        mContext = context;
        View.inflate(context, R.layout.content_list_item, this);
        ButterKnife.bind(this);

    }

    @Override
    public void bindView(MediaItem data, int position) {
        mMediaItem = data;
        mTVName.setText(data.getTitle());
        if (UpnpUtil.isAudioItem(data)){
            ImageLoader.loadThumail(mContext, data.getAlbumUri(), mImageView, musicIcon);
        }else if (UpnpUtil.isVideoItem(data)){
            ImageLoader.loadThumail(mContext, data.getAlbumUri(), mImageView, videoIcon);
        }else if (UpnpUtil.isPictureItem(data)){
            ImageLoader.loadThumail(mContext, data.getAlbumUri(), mImageView, picIcon);
        }else{
            mImageView.setImageDrawable(foldIcon);
        }
    }

}
