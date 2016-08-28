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
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerContact;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerPresenter;
import com.geniusgithub.mediaplayer.player.music.lrc.LyricView;
import com.geniusgithub.mediaplayer.player.music.util.ImageUtils;

import org.cybergarage.util.AlwaysLog;


public class MusicPlayerFragment extends BaseFragment{

    public static final String TAG = MusicPlayerFragment.class.getSimpleName();

    private View mRootView;
    private MusicPlayerPresenter mMusicPlayerPresenter;
    private MusicPlayerContact.IView mMusicPlayerView;


  /*  private IToolBar mExternToolbar;
    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }*/



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


 /*   public void updateToolTitle(String title){
        if (mExternToolbar != null){
            mExternToolbar.updateToolTitle(title);
        }
    }
*/





    public class MusicPlayerView implements MusicPlayerContact.IView,    View.OnClickListener,
                                                                     SeekBar.OnSeekBarChangeListener {

        private Context mContext;
        private MusicPlayerContact.IPresenter  mMsuciPlayerPresenter;


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

            mTVSongName.setText(itemInfo.getTitle());
            mTVArtist.setText(itemInfo.getArtist());
            mTVAlbum.setText(itemInfo.getAlbum());
        }


        @Override
        public void updateAlbumPIC(Drawable drawable) {
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

    }



}
