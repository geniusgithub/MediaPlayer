package com.geniusgithub.mediaplayer.player.music.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Visualizer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.player.music.ImageUtils;
import com.geniusgithub.mediaplayer.player.music.VisualizerView;
import com.geniusgithub.mediaplayer.player.music.lrc.LyricView;
import com.geniusgithub.mediaplayer.player.music.presenter.IMusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.presenter.MusicPlayerPresenter;

import org.cybergarage.util.AlwaysLog;

public class MusicPlayerView implements MusicPlayerPresenter.IMusicPlayerView,
                                        View.OnClickListener,
                                        SeekBar.OnSeekBarChangeListener {
    private final static String TAG = MusicPlayerView.class.getSimpleName();
    private Context mContext;
    private IMusicPlayerPresenter mMsuciPlayerPresenter;


    public View mPrepareView;
    public TextView mTVPrepareSpeed;

    public View mLoadView;
    public TextView mTVLoadSpeed;

    public View mControlView;
    public TextView mTVSongName;
    public TextView mTVArtist;
    public TextView mTVAlbum;

    public ImageButton mBtnPlay;
    public ImageButton mBtnPause;
    public ImageButton mBtnPre;
    public ImageButton mBtnNext;
    public SeekBar mSeekBar;
    public TextView mTVCurTime;
    public TextView mTVTotalTime;
    public VisualizerView mVisualizerView;
    public ImageView mIVAlbum;

    public TranslateAnimation mHideDownTransformation;
    public AlphaAnimation mAlphaHideTransformation;

    public View mSongInfoView;
    public LyricView mLyricView;
    public boolean lrcShow = false;



    private final static int DRAW_OFFSET_Y = 200;

    @Override
    public void bindView(Context context, View container) {
        mContext = context;

        initView(container);
    }

    private void initView(View rootView) {
        mPrepareView = rootView.findViewById(R.id.prepare_panel);
        mTVPrepareSpeed = (TextView) rootView.findViewById(R.id.tv_prepare_speed);

        mLoadView = rootView.findViewById(R.id.loading_panel);
        mTVLoadSpeed = (TextView) rootView.findViewById(R.id.tv_speed);

        mControlView = rootView.findViewById(R.id.control_panel);
        mTVSongName = (TextView) rootView.findViewById(R.id.tv_title);
        mTVArtist = (TextView) rootView.findViewById(R.id.tv_artist);
        mTVAlbum = (TextView) rootView.findViewById(R.id.tv_album);

        mBtnPlay = (ImageButton) rootView.findViewById(R.id.btn_play);
        mBtnPause = (ImageButton) rootView.findViewById(R.id.btn_pause);
        mBtnPre = (ImageButton) rootView.findViewById(R.id.btn_playpre);
        mBtnNext = (ImageButton) rootView.findViewById(R.id.btn_playnext);
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.playback_seeker);
        mSeekBar.setOnSeekBarChangeListener(this);

        mTVCurTime = (TextView) rootView.findViewById(R.id.tv_curTime);
        mTVTotalTime = (TextView) rootView.findViewById(R.id.tv_totalTime);
        mVisualizerView = (VisualizerView) rootView.findViewById(R.id.mp_freq_view);
        mIVAlbum = (ImageView) rootView.findViewById(R.id.iv_album);



        mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200.0f);
        mHideDownTransformation.setDuration(1000);

        mAlphaHideTransformation = new AlphaAnimation(1, 0);
        mAlphaHideTransformation.setDuration(1000);

        updateAlbumPIC(mContext.getResources().getDrawable(R.drawable.mp_music_default));

        mSongInfoView = rootView.findViewById(R.id.song_info_view);

        mLyricView = (LyricView) rootView.findViewById(R.id.lrc_view);


    }

    @Override
    public void bindPresent(IMusicPlayerPresenter presenter) {
        mMsuciPlayerPresenter = presenter;
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
    public void showPrepareLoadView(boolean isShow) {
        if (isShow) {
            mPrepareView.setVisibility(View.VISIBLE);
        } else {
            mPrepareView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showControlView(boolean show) {
        if (show) {
            mControlView.setVisibility(View.VISIBLE);
        } else {
            mControlView.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateLyricView(MediaItem mMediaInfo) {
        mLyricView.read(mMediaInfo.title, mMediaInfo.artist);
    }

    @Override
    public void updateMediaInfoView(MediaItem mediaInfo) {
        setcurTime(0);
        setTotalTime(0);
        setSeekbarMax(100);
        setSeekbarProgress(0);

        mTVSongName.setText(mediaInfo.getTitle());
        mTVArtist.setText(mediaInfo.getArtist());
        mTVAlbum.setText(mediaInfo.getAlbum());
    }

    @Override
    public void showLoadView(boolean isShow) {
        if (isShow) {
            mLoadView.setVisibility(View.VISIBLE);
        } else {
            if (mLoadView.isShown()) {
                mLoadView.startAnimation(mAlphaHideTransformation);
                mLoadView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showLRCView(boolean bshow) {
        lrcShow = bshow;
        if (bshow) {
            mLyricView.setVisibility(View.VISIBLE);
            mSongInfoView.setVisibility(View.GONE);
        } else {
            mLyricView.setVisibility(View.GONE);
            mSongInfoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showPlayErrorTip() {
        Toast.makeText(mContext, R.string.toast_musicplay_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setSeekbarMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void setSeekbarSecondProgress(int time) {
        mSeekBar.setSecondaryProgress(time);
    }

    @Override
    public void setTotalTime(int totalTime) {
        String timeString = DlnaUtils.formateTime(totalTime);
        mTVTotalTime.setText(timeString);
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
    public boolean isLRCViewShow() {
        return lrcShow;
    }

    @Override
    public void setSeekbarProgress(int time) {
        if (!isSeekbarTouch) {
            mSeekBar.setProgress(time);
        }else{
            AlwaysLog.e(TAG, "isSeekbarTouch = true, so ignore seek operator");
        }
    }

    @Override
    public boolean isLoadViewShow() {
        if (mLoadView.getVisibility() == View.VISIBLE ||
                mPrepareView.getVisibility() == View.VISIBLE) {
            return true;
        }

        return false;
    }

    @Override
    public void setSpeed(float speed) {
        String showString = (int) speed + "KB/" + mContext.getResources().getString(R.string.second);
        mTVPrepareSpeed.setText(showString);
        mTVLoadSpeed.setText(showString);
    }

    @Override
    public void updateAlbumPIC(Drawable drawable) {
        Bitmap bitmap = ImageUtils.createRotateReflectedMap(mContext, drawable);
        if (bitmap != null) {
            mIVAlbum.setImageBitmap(bitmap);
        }
    }



    @Override
    public void setcurTime(int curTime) {
        String timeString = DlnaUtils.formateTime(curTime);
        mTVCurTime.setText(timeString);
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
        mMsuciPlayerPresenter.onSeekProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekbarTouch = true;
        mMsuciPlayerPresenter.onSeekStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekbarTouch = false;
        mMsuciPlayerPresenter.onSeekStopTrackingTouch(seekBar);
    }

}
