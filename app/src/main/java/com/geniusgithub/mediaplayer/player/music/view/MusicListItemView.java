package com.geniusgithub.mediaplayer.player.music.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.adapter.IBaseAdapterView;
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicListItemView extends LinearLayout implements IBaseAdapterView<MediaItem>{

    private Context mContext;

    @BindView(R.id.tv_name)
    public TextView mTVName;

    @BindView(R.id.tv_artist)
    public TextView mTVArtist;

    @BindView(R.id.tv_duration)
    public TextView mTVDuration;

    private MediaItem mMediaItem;

    public MusicListItemView(Context context) {
        super(context);
        mContext = context;
        View.inflate(context, R.layout.music_playlist_item, this);
        ButterKnife.bind(this);

    }

    @Override
    public void bindView(MediaItem data, int position) {
        mMediaItem = data;
        mTVName.setText(data.getTitle());
        mTVArtist.setText("- " + data.getArtist());
        mTVDuration.setText(DlnaUtils.formateTime(data.getDuration()));
    }

}
