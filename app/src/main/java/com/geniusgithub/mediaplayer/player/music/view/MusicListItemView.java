package com.geniusgithub.mediaplayer.player.music.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.adapter.IBaseAdapterView;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.util.TimeUtil;

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
    private int mCurPos = -1;

    private static int defaultColorName;
    private static int defaultColorArtist;
    private static int defaultColorDuration;
    private static int selectColor;

    static {
        selectColor = AllShareApplication.getInstance().getResources().getColor(R.color.colorPrimary);
        defaultColorName = AllShareApplication.getInstance().getResources().getColor(R.color.white);
        defaultColorArtist = AllShareApplication.getInstance().getResources().getColor(R.color.gold);
        defaultColorDuration = AllShareApplication.getInstance().getResources().getColor(R.color.white);
    }

    public MusicListItemView(Context context) {
        super(context);
        mContext = context;
        View.inflate(context, R.layout.music_playlist_item, this);
        ButterKnife.bind(this);


    }

    @Override
    public void bindView(MediaItem data, int position) {
        mMediaItem = data;
        mCurPos = position;
        mTVName.setText(data.getTitle());
        mTVArtist.setText("- " + data.getArtist());
        mTVDuration.setText(TimeUtil.formateTime(data.getDuration()));
    }

    public void updateSelColor(int selPos){
        if (selPos == mCurPos){
            mTVName.setTextColor(selectColor);
            mTVArtist.setTextColor(selectColor);
            mTVDuration.setTextColor(selectColor);
        }else{
            mTVName.setTextColor(defaultColorName);
            mTVArtist.setTextColor(defaultColorArtist);
            mTVDuration.setTextColor(defaultColorDuration);
        }
    }

}
