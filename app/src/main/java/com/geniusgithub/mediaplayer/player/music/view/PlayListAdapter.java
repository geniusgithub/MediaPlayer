package com.geniusgithub.mediaplayer.player.music.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.geniusgithub.mediaplayer.base.adapter.BaseListAdapter;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

public class PlayListAdapter extends BaseListAdapter<MediaItem, MusicListItemView> {


    private int mCurSelPos = -1;

    public PlayListAdapter(Context context, List<MediaItem> data) {
        super(context, data);
    }

    @Override
    public MusicListItemView createView(Context context, int viewType) {
        MusicListItemView itemView = new MusicListItemView(context);
        return itemView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       super.onBindViewHolder(holder, position);
        MusicListItemView itemView = (MusicListItemView) holder.itemView;
        itemView.updateSelColor(mCurSelPos);
    }

    public void updateSelPos(int pos){
        mCurSelPos = pos;
        notifyDataSetChanged();
    }
}
