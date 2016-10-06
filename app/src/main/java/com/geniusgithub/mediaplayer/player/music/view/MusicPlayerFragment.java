package com.geniusgithub.mediaplayer.player.music.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseFragment;
import com.geniusgithub.mediaplayer.component.CircleTransform;
import com.geniusgithub.mediaplayer.component.IToolBar;
import com.geniusgithub.mediaplayer.component.ToolEntry;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.util.TimeUtil;
import com.geniusgithub.mediaplayer.player.base.PlayMode;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerContact;
import com.geniusgithub.mediaplayer.player.music.LocalMusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.lrc.LyricView;

import org.cybergarage.util.AlwaysLog;

import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MusicPlayerFragment extends BaseFragment{

    public static final String TAG = MusicPlayerFragment.class.getSimpleName();

    private View mRootView;
    private MusicPlayerPresenter mMusicPlayerPresenter;
    private MusicPlayerContact.IView mMusicPlayerView;


    private IToolBar mExternToolbar;
    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUIReady(view);
    }



    @Override
    public void onDestroy(){
        mMusicPlayerPresenter.onUiDestroy();
        super.onDestroy();

    }

    public void onNewIntent(Intent intent) {
        AlwaysLog.i(TAG, "onNewIntent");
        mMusicPlayerPresenter.onNewIntent(getActivity().getIntent());

    }


    private void onUIReady(View view){
        mRootView = view.findViewById(R.id.dl_music_drawer);

        mMusicPlayerPresenter = new LocalMusicPlayerPresenter();
        mMusicPlayerView = new MusicPlayerView(getActivity());
        mMusicPlayerView.setupView(mRootView);
        mMusicPlayerPresenter.bindView(mMusicPlayerView);
        mMusicPlayerPresenter.onUiCreate(getActivity());
        mMusicPlayerPresenter.onNewIntent(getActivity().getIntent());
    }


   public void updateToolTitle(String title,String author){
        if (mExternToolbar != null){
            ToolEntry entry = new ToolEntry();
            entry.title = title;
            entry.subTitle = author;
            mExternToolbar.updateToolTitle(entry);
        }
    }






    public class MusicPlayerView implements MusicPlayerContact.IView, View.OnClickListener,
                                                                     SeekBar.OnSeekBarChangeListener,
                                                MusicNavigationView.onPlayItemClickListener{

        private Context mContext;
        private MusicPlayerContact.IPresenter  mMsuciPlayerPresenter;

        @BindView(R.id.nv_navigation)
        public MusicNavigationView mNavView;

        @BindView(R.id.dl_music_drawer)
        public DrawerLayout mDrawerLayout;


        @BindView(R.id.prepare_panel)
        public View mPrepareView;

        @BindView(R.id.tv_prepare_speed)
        public TextView mTVPrepareSpeed;

        @BindView(R.id.control_panel)
        public View mControlView;

        @BindView(R.id.btn_play)
        public ImageView mBtnPlay;

        @BindView(R.id.btn_pause)
        public ImageView mBtnPause;

        @BindView(R.id.btn_playpre)
        public ImageView mBtnPre;

        @BindView(R.id.btn_playnext)
        public ImageView mBtnNext;

        @BindView(R.id.button_play_mode_toggle)
        public ImageView mBtnToggleMode;

        @BindView(R.id.button_play_list)
        public ImageView mBtnPlayList;

        @BindView(R.id.playback_seeker)
        public SeekBar mSeekBar;

        @BindView(R.id.tv_curTime)
        public TextView mTVCurTime;

        @BindView(R.id.tv_totalTime)
        public TextView mTVTotalTime;


        @BindView(R.id.iv_album)
        public com.geniusgithub.mediaplayer.widget.ShadowImageView mIVAlbum;

        @BindView(R.id.iv_albumbackground)
        public com.geniusgithub.mediaplayer.widget.ShadowImageView mIVAlbumBackground;

        @BindView(R.id.song_info_view)
        public View mSongInfoView;

        @BindView(R.id.lrc_view)
        public LyricView mLyricView;

        public boolean lrcShow = false;
        public Drawable mDefaultDrawable;

        private final static int DRAW_OFFSET_Y = 200;

        private Handler mHandler;
        public MusicPlayerView(Context context){
            mContext = context;
        }

        @Override
        public void bindPresenter(MusicPlayerContact.IPresenter presenter){
            mMsuciPlayerPresenter = presenter;
        }

        @Override
        public void setupView(View rootView){
            initView(rootView);
        }

        @Override
        public void showPrepareLoadView(boolean bShow) {

            if (bShow) {
                mPrepareView.setVisibility(View.VISIBLE);
                showControlView(false);
            } else {
                mPrepareView.setVisibility(View.GONE);
                showControlView(true);
            }


        }




        @Override
        public void showLRCView(boolean bShow) {
            lrcShow = bShow;
            if (bShow) {
                mLyricView.setVisibility(View.VISIBLE);
                mSongInfoView.setVisibility(View.GONE);
            } else {
                mLyricView.setVisibility(View.GONE);
                mSongInfoView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void showPlay(boolean bShow) {
            if (bShow) {
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.INVISIBLE);
            } else {
                mBtnPlay.setVisibility(View.INVISIBLE);
                mBtnPause.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void showPlayErrorTip() {
            Toast.makeText(mContext, R.string.toast_musicplay_fail, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void setSeekbarProgress(int pos) {
            if (!isSeekbarTouch) {
                mSeekBar.setProgress(pos);
            }else{
                AlwaysLog.e(TAG, "isSeekbarTouch = true, so ignore seek operator");
            }
        }

        @Override
        public void setSeekbarSecondProgress(int max) {
            mSeekBar.setSecondaryProgress(max);
        }

        @Override
        public void setSeekbarMax(int max) {
            mSeekBar.setMax(max);
        }

        @Override
        public void setTotalTime(int totalTime) {
            String timeString = TimeUtil.formateTime(totalTime);
            mTVTotalTime.setText(timeString);
        }


        @Override
        public void setcurTime(int curTime) {
            String timeString = TimeUtil.formateTime(curTime);
            mTVCurTime.setText(timeString);
        }

        @Override
        public void setSpeed(float speed) {
            String showString = (int) speed + "KB/" + mContext.getResources().getString(R.string.second);
            mTVPrepareSpeed.setText(showString);
        }



        @Override
        public void refreshLyrc(int pos) {
            if (pos > 0) {
                mLyricView.setOffsetY(DRAW_OFFSET_Y - mLyricView.selectIndex(pos)
                        * (mLyricView.getSIZEWORD() + LyricView.INTERVAL - 1));
            } else {
                mLyricView.setOffsetY(DRAW_OFFSET_Y);
            }
            mLyricView.invalidate();
        }




        @Override
        public void updateLyricView(MediaItem itemInfo) {
            mLyricView.read(itemInfo.title, itemInfo.artist);
        }

        @Override
        public void updateMediaInfoView(MediaItem itemInfo, int pos) {
            setcurTime(0);
            setTotalTime(0);
            setSeekbarMax(100);
            setSeekbarProgress(0);

            updateToolTitle(itemInfo.getTitle(), itemInfo.getArtist());
            mNavView.updateSelPos(pos);

            cancelAlumAnimation();
            loadAlbum(mContext, itemInfo.getAlbumUri(), mIVAlbum);
        }


        @Override
        public void startRotateAnimation(boolean rotate) {
            if (rotate){
                mIVAlbum.resumeRotateAnimation();
                mIVAlbumBackground.resumeRotateAnimation();
            }else{
                mIVAlbum.pauseRotateAnimation();
                mIVAlbumBackground.pauseRotateAnimation();
            }
        }

        private boolean isFirstUpdatePlayMode = true;
        @Override
        public void updatePlayMode(PlayMode playMode) {
            if (playMode == null) {
                playMode = PlayMode.getDefault();
            }
            switch (playMode) {
                case LOOP:
                    mBtnToggleMode.setBackgroundResource(R.drawable.selector_btn_mode_list);
                    if (!isFirstUpdatePlayMode) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.play_mode_list), Toast.LENGTH_SHORT).show();
                    }else{
                        isFirstUpdatePlayMode = false;
                    }
                    break;
                case SHUFFLE:
                    mBtnToggleMode.setBackgroundResource(R.drawable.selector_btn_mode_shuffle);
                    if (!isFirstUpdatePlayMode){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.play_mode_shuffle), Toast.LENGTH_SHORT).show();
                    }else{
                        isFirstUpdatePlayMode = false;
                    }
                    break;
                case SINGLE:
                    mBtnToggleMode.setBackgroundResource(R.drawable.selector_btn_mode_single);
                    if (!isFirstUpdatePlayMode){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.play_mode_single), Toast.LENGTH_SHORT).show();
                    }else{
                        isFirstUpdatePlayMode = false;
                    }
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
                    mMsuciPlayerPresenter.onMusicPlay();
                    break;
                case R.id.btn_pause:
                    mMsuciPlayerPresenter.onMusicPause();
                    break;
                case R.id.btn_playpre:
                    mMsuciPlayerPresenter.onPlayPre();
                    break;
                case R.id.btn_playnext:
                    mMsuciPlayerPresenter.onPlayNext();
                    break;
                case R.id.button_play_mode_toggle:
                    mMsuciPlayerPresenter.onToggleMode();
                    break;
                case R.id.button_play_list:
                    updateDrawableStatus();
                    break;
            }
        }

        private boolean isSeekbarTouch = false;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setcurTime(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekbarTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekbarTouch = false;
            mMsuciPlayerPresenter.onSeekStopTrackingTouch(seekBar);
        }


        private void initView(View rootView) {
            ButterKnife.bind(this, rootView);

            mBtnPlay.setOnClickListener(this);
            mBtnPause.setOnClickListener(this);
            mBtnPre.setOnClickListener(this);
            mBtnNext.setOnClickListener(this);
            mBtnToggleMode.setOnClickListener(this);
            mBtnPlayList.setOnClickListener(this);
            mSeekBar.setOnSeekBarChangeListener(this);

            mNavView.addItemListener(this);

            mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.mp_music_default);
        }


        @Override
        public void updatePlayList(List<MediaItem> list) {
            mNavView.updatePlayList(list);
        }

        @Override
        public void onItemClick(MediaItem data, int position) {
            mMusicPlayerPresenter.onPlayItemClick(data, position);
            updateDrawableStatus();
        }


        public void updateToolTitle(String title,String author) {
            MusicPlayerFragment.this.updateToolTitle(title, author);
        }

        private void updateDrawableStatus(){
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                mDrawerLayout.closeDrawers();
            }else{
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        }
        public void showControlView(boolean bShow) {
            if (bShow) {
                mControlView.setVisibility(View.VISIBLE);
            } else {
                mControlView.setVisibility(View.GONE);
            }
        }

        private void loadAlbum(Context context, String uri, ImageView imageView){
            Glide.with(mContext).load(uri).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).
                    transform(new CircleTransform(context)).into(imageView);
        }


        private void cancelAlumAnimation() {
            mIVAlbum.cancelRotateAnimation();
            mIVAlbumBackground.cancelRotateAnimation();
        }


    }



}
