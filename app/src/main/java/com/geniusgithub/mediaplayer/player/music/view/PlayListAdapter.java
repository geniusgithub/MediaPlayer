package com.geniusgithub.mediaplayer.player.music.view;

import android.content.Context;

import com.geniusgithub.mediaplayer.base.adapter.BaseListAdapter;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

public class PlayListAdapter extends BaseListAdapter<MediaItem, MusicListItemView> {


    public PlayListAdapter(Context context, List<MediaItem> data) {
        super(context, data);
    }

    @Override
    public MusicListItemView createView(Context context, int viewType) {
        MusicListItemView itemView = new MusicListItemView(context);
        return itemView;
    }
}
