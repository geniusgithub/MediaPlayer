package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.content.res.Resources;

import com.geniusgithub.mediaplayer.base.adapter.BaseListAdapter;
import com.geniusgithub.mediaplayer.component.ImageLoader;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

public class ContentAdapter extends BaseListAdapter<MediaItem, ContentItemView>{

    private Context mContext;
    private boolean mBusy = false;

    public ContentAdapter(Context context, List<MediaItem> data) {
        super(context, data);
        mContext = context;
        Resources res = context.getResources();
        ContentItemView.loadDefaultDrawable(res);
    }

    @Override
    public ContentItemView createView(Context context, int viewType) {
        ContentItemView itemView = new ContentItemView(context);
        return itemView;
    }


    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
        if (mBusy){
            ImageLoader.pauseRequests(mContext);
        }else{
            ImageLoader.resumeRequests(mContext);
        }

    }

}
