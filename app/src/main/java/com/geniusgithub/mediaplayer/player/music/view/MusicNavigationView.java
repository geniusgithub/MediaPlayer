package com.geniusgithub.mediaplayer.player.music.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.adapter.OnItemClickListener;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicNavigationView extends LinearLayout {

    private Context mContext;
    private View mRootView;


    @BindView(R.id.rv_playlist)
    RecyclerView mPlayListView;

    private PlayListAdapter mAdapter;
    private onPlayItemClickListener mItemClickListener;

    public MusicNavigationView(Context context) {
        super(context);
    }

    public MusicNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        mRootView = LayoutInflater.from(context).inflate(R.layout.music_navigation_layout, this,true);
        ButterKnife.bind(this, mRootView);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initView();
    }

    private void initView(){

        mPlayListView.setHasFixedSize(true);
        mPlayListView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mPlayListView.setLayoutManager(mLayoutManager);

        mAdapter = new PlayListAdapter(mContext, new ArrayList<MediaItem>());
        mPlayListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new onPlayItemClickListener() {
            @Override
            public void onItemClick(MediaItem data, int position) {
                if (mItemClickListener != null){
                    mItemClickListener.onItemClick(data, position);
                }
            }
        });
    }

    public void updatePlayList(List<MediaItem> list){
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    public void addItemListener(onPlayItemClickListener listener){
        mItemClickListener = listener;
    }

    public static interface onPlayItemClickListener extends   OnItemClickListener<MediaItem>{

    }


}
