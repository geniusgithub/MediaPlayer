package com.geniusgithub.mediaplayer.player.music.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseFragment;
import com.geniusgithub.mediaplayer.base.IToolBar;
import com.geniusgithub.mediaplayer.base.ToolEntry;
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerContact;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.lrc.LyricView;
import com.geniusgithub.mediaplayer.player.music.util.ImageUtils;

import org.cybergarage.util.AlwaysLog;

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
        View view = inflater.inflate(R.layout.music_player_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
        mRootView = view.findViewById(R.id.rootframeview);

        mMusicPlayerPresenter = new MusicPlayerPresenter();
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
                                                                     SeekBar.OnSeekBarChangeListener {

        private Context mContext;
        private MusicPlayerContact.IPresenter  mMsuciPlayerPresenter;

        @BindView(R.id.prepare_panel)
        public View mPrepareView;

        @BindView(R.id.tv_prepare_speed)
        public TextView mTVPrepareSpeed;

        @BindView(R.id.loading_panel)
        public View mLoadView;

        @BindView(R.id.tv_speed)
        public TextView mTVLoadSpeed;

        @BindView(R.id.control_panel)
        public View mControlView;

        @BindView(R.id.tv_album)
        public TextView mTVAlbum;

        @BindView(R.id.btn_play)
        public ImageButton mBtnPlay;

        @BindView(R.id.btn_pause)
        public ImageButton mBtnPause;

        @BindView(R.id.btn_playpre)
        public ImageButton mBtnPre;

        @BindView(R.id.btn_playnext)
        public ImageButton mBtnNext;

        @BindView(R.id.playback_seeker)
        public SeekBar mSeekBar;

        @BindView(R.id.tv_curTime)
        public TextView mTVCurTime;

        @BindView(R.id.tv_totalTime)
        public TextView mTVTotalTime;

        @BindView(R.id.mp_freq_view)
        public VisualizerView mVisualizerView;

        @BindView(R.id.iv_album)
        public ImageView mIVAlbum;

        @BindView(R.id.song_info_view)
        public View mSongInfoView;

        @BindView(R.id.lrc_view)
        public LyricView mLyricView;

        public TranslateAnimation mHideDownTransformation;
        public AlphaAnimation mAlphaHideTransformation;

        public boolean lrcShow = false;
        public Drawable mDefaultDrawable;

        private final static int DRAW_OFFSET_Y = 200;

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
            } else {
                mPrepareView.setVisibility(View.GONE);
            }
        }

        @Override
        public void showControlView(boolean bShow) {
            if (bShow) {
                mControlView.setVisibility(View.VISIBLE);
            } else {
                mControlView.setVisibility(View.GONE);
            }
        }

        @Override
        public void showLoadView(boolean bShow) {
            if (bShow) {
                mLoadView.setVisibility(View.VISIBLE);
            } else {
                if (mLoadView.isShown()) {
                    mLoadView.startAnimation(mAlphaHideTransformation);
                    mLoadView.setVisibility(View.GONE);
                }
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
            String timeString = DlnaUtils.formateTime(totalTime);
            mTVTotalTime.setText(timeString);
        }


        @Override
        public void setcurTime(int curTime) {
            String timeString = DlnaUtils.formateTime(curTime);
            mTVCurTime.setText(timeString);
        }

        @Override
        public void setSpeed(float speed) {
            String showString = (int) speed + "KB/" + mContext.getResources().getString(R.string.second);
            mTVPrepareSpeed.setText(showString);
            mTVLoadSpeed.setText(showString);
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
        public void updateMediaInfoView(MediaItem itemInfo) {
            setcurTime(0);
            setTotalTime(0);
            setSeekbarMax(100);
            setSeekbarProgress(0);

            updateToolTitle(itemInfo.getTitle(), itemInfo.getArtist());
            mTVAlbum.setText(itemInfo.getAlbum());
        }


        @Override
        public void updateAlbumPIC(Drawable drawable) {
            if (drawable == null){
                drawable = mDefaultDrawable;
            }

            Bitmap bitmap = ImageUtils.createRotateReflectedMap(mContext, drawable);
            if (bitmap != null) {
                mIVAlbum.setImageBitmap(bitmap);
            }

        }



        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            mVisualizerView.updateVisualizer(waveform);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            mVisualizerView.updateVisualizer(fft);
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
            mSeekBar.setOnSeekBarChangeListener(this);

            mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200.0f);
            mHideDownTransformation.setDuration(1000);

            mAlphaHideTransformation = new AlphaAnimation(1, 0);
            mAlphaHideTransformation.setDuration(1000);


            mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.mp_music_default);
            updateAlbumPIC(mDefaultDrawable);
        }

        @Override
        public void updateToolTitle(String title,String author) {
            MusicPlayerFragment.this.updateToolTitle(title, author);
        }
    }



}
