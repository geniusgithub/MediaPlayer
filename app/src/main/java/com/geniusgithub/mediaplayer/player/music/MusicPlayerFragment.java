package com.geniusgithub.mediaplayer.player.music;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.MusicPlayEngineImpl;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.LyricView;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;

import java.io.File;

public class MusicPlayerFragment extends Fragment implements MediaPlayer.OnBufferingUpdateListener,
                                                    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener,
                                                    LrcDownLoadHelper.ILRCDownLoadCallback{

    private Context mContext;

    public static final String PLAY_INDEX = "player_index";

    private static final CommonLog log = LogFactory.createLog();

    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int CHECK_DELAY = 0x0003;
    private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;
    private final static int UPDATE_LRC_VIEW = 0x0007;


    private UIManager mUIManager;
    private MusicPlayEngineImpl mPlayerEngineImpl;
    private MusicPlayEngineListener mPlayEngineListener;
    private MusicControlCenter mMusicControlCenter;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;
    private CheckDelayTimer mCheckDelayTimer;

    private boolean isDestroy = false;


    private LrcDownLoadHelper mLrcDownLoadHelper;



    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_player_layout, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupsView(view);
        initData();
        refreshIntent(getActivity().getIntent());
    }

    public void setupsView(View view)
    {
        mUIManager = new UIManager();
        mUIManager.bindView(view);
    }

    public void initData(){
        mPlayPosTimer = new SingleSecondTimer(mContext);
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what)
                {
                    case REFRESH_CURPOS:
                        refreshCurPos();
                        mUIManager.refreshLyrc(mPlayerEngineImpl.getCurPosition());
                        break;
                    case REFRESH_SPEED:
                        refreshSpeed();
                        break;
                    case CHECK_DELAY:
                        checkDelay();
                        break;
                    case LOAD_DRAWABLE_COMPLETE:
                        Object object = msg.obj;
                        Drawable drawable = null;
                        if (object != null){
                            drawable = (Drawable) object;
                        }
                        onLoadDrawableComplete(drawable);
                        break;
                    case UPDATE_LRC_VIEW:
                        mUIManager.updateLyricView(mMediaInfo);
                        break;
                }
            }

        };

        mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);

        mNetWorkTimer = new SingleSecondTimer(mContext);
        mNetWorkTimer.setHandler(mHandler, REFRESH_SPEED);
        mCheckDelayTimer = new CheckDelayTimer(mContext);
        mCheckDelayTimer.setHandler(mHandler, CHECK_DELAY);

        mPlayerEngineImpl = new MusicPlayEngineImpl(mContext);
        mPlayerEngineImpl.setOnBuffUpdateListener(this);
        mPlayerEngineImpl.setOnSeekCompleteListener(this);
        mPlayerEngineImpl.setDataCaptureListener(mUIManager);
        mPlayEngineListener = new MusicPlayEngineListener();
        mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);

        mMusicControlCenter = new MusicControlCenter(mContext);
        mMusicControlCenter.bindMusicPlayEngine(mPlayerEngineImpl);


        mNetWorkTimer.startTimer();
        mCheckDelayTimer.startTimer();

        mLrcDownLoadHelper = new LrcDownLoadHelper();
        mLrcDownLoadHelper.init();

        mUIManager.showLRCView(false);

        boolean ret = FileHelper.createDirectory(MusicUtils.getLyricDir());
        log.e(" FileHelper.createDirectory:" + MusicUtils.getLyricDir() + ", ret = " + ret);
    }


    public void refreshIntent(Intent intent){
        log.e("refreshIntent");
        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }


        mMusicControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getMusicList());

        mUIManager.updateMediaInfoView(mMediaInfo);
        mPlayerEngineImpl.playMedia(mMediaInfo);

        mUIManager.showPrepareLoadView(true);
        mUIManager.showLoadView(false);
        mUIManager.showControlView(false);

    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {

        isDestroy = true;
        mUIManager.unInit();
        mLrcDownLoadHelper.unInit();
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mMusicControlCenter.exit();

        super.onDestroy();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //	log.e("onBufferingUpdate --> percen = " + percent + ", curPos = " + mp.getCurrentPosition());

        int duration = mPlayerEngineImpl.getDuration();
        int time = duration * percent / 100;
        mUIManager.setSeekbarSecondProgress(time);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        log.e("onSeekComplete ...");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mUIManager.showPlayErrorTip();
        log.e("onError what = " + what + ", extra = " + extra);
        return false;
    }


    @Override
    public void lrcDownLoadComplete(boolean isSuccess, String song, String artist) {

        if (isSuccess && song.equals(mMediaInfo.title) && artist.equals(mMediaInfo.artist)){
            Message msg = mHandler.obtainMessage(UPDATE_LRC_VIEW);
            msg.sendToTarget();
        }
    }


    protected void onNewIntent(Intent intent) {
        log.e("onNewIntent");
        refreshIntent(intent);

    }


    public void switchLRCView(boolean flag) {
        mUIManager.showLRCView(flag);
    }

    public void toggleLRCView() {
        if (mUIManager.isLRCViewShow()){
            mUIManager.showLRCView(false);
        }else{
            mUIManager.showLRCView(true);
        }

    }

    public void refreshCurPos(){
        int pos = mPlayerEngineImpl.getCurPosition();

        mUIManager.setSeekbarProgress(pos);

    }



    public void refreshSpeed(){
        //	if (mUIManager.isLoadViewShow()){
        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mUIManager.setSpeed(speed);
        //	}
    }


    public void checkDelay(){
        int pos = mPlayerEngineImpl.getCurPosition();

        boolean ret = mCheckDelayTimer.isDelay(pos);
        if (ret){
            mUIManager.showLoadView(true);
        }else{
            mUIManager.showLoadView(false);
        }

        mCheckDelayTimer.setPos(pos);

    }

    /*---------------------------------------------------------------------------*/
    class UIManager implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Visualizer.OnDataCaptureListener {

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

        public UIManager() {

        }

        public void bindView(View rootView) {

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
            mTVCurTime = (TextView) rootView.findViewById(R.id.tv_curTime);
            mTVTotalTime = (TextView) rootView.findViewById(R.id.tv_totalTime);
            mVisualizerView = (VisualizerView) rootView.findViewById(R.id.mp_freq_view);
            mIVAlbum = (ImageView) rootView.findViewById(R.id.iv_album);
            setSeekbarListener(this);


            mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200.0f);
            mHideDownTransformation.setDuration(1000);

            mAlphaHideTransformation = new AlphaAnimation(1, 0);
            mAlphaHideTransformation.setDuration(1000);

            updateAlbumPIC(getResources().getDrawable(R.drawable.mp_music_default));

            mSongInfoView = rootView.findViewById(R.id.song_info_view);

            mLyricView = (LyricView) rootView.findViewById(R.id.lrc_view);
        }


        public void unInit() {

        }

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

        public boolean isLRCViewShow() {
            return lrcShow;
        }


        public void updateAlbumPIC(Drawable drawable) {
            Bitmap bitmap = ImageUtils.createRotateReflectedMap(mContext, drawable);
            if (bitmap != null) {
                mIVAlbum.setImageBitmap(bitmap);
            }
        }

        public void showPrepareLoadView(boolean isShow) {
            if (isShow) {
                mPrepareView.setVisibility(View.VISIBLE);
            } else {
                mPrepareView.setVisibility(View.GONE);
            }
        }

        public void showControlView(boolean show) {
            if (show) {
                mControlView.setVisibility(View.VISIBLE);
            } else {
                mControlView.setVisibility(View.GONE);
            }

        }

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

        private boolean isSeekbarTouch = false;

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_play:
                    mMusicControlCenter.replay();
                    break;
                case R.id.btn_pause:
                    mMusicControlCenter.pause();
                    break;
                case R.id.btn_playpre:
                    mMusicControlCenter.prev();
                    break;
                case R.id.btn_playnext:
                    mMusicControlCenter.next();
                    break;
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            mUIManager.setcurTime(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekbarTouch = true;

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekbarTouch = false;
            seek(seekBar.getProgress());
        }

        public void showPlay(boolean bShow) {
            if (bShow) {
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.INVISIBLE);
            } else {
                mBtnPlay.setVisibility(View.INVISIBLE);
                mBtnPause.setVisibility(View.VISIBLE);
            }
        }

        public void togglePlayPause() {
            if (mBtnPlay.isShown()) {
                mMusicControlCenter.replay();
            } else {
                mMusicControlCenter.pause();
            }
        }

        public void setSeekbarProgress(int time) {
            if (!isSeekbarTouch) {
                mSeekBar.setProgress(time);
            }
        }


        public void setSeekbarSecondProgress(int time) {
            mSeekBar.setSecondaryProgress(time);
        }

        public void setSeekbarMax(int max) {
            mSeekBar.setMax(max);
        }

        public void setcurTime(int curTime) {
            String timeString = DlnaUtils.formateTime(curTime);
            mTVCurTime.setText(timeString);
        }

        public void setTotalTime(int totalTime) {
            String timeString = DlnaUtils.formateTime(totalTime);
            mTVTotalTime.setText(timeString);
        }

        public void updateMediaInfoView(MediaItem mediaInfo) {
            setcurTime(0);
            setTotalTime(0);
            setSeekbarMax(100);
            setSeekbarProgress(0);

            mTVSongName.setText(mediaInfo.getTitle());
            mTVArtist.setText(mediaInfo.getArtist());
            mTVAlbum.setText(mediaInfo.getAlbum());
        }

        public void setSpeed(float speed) {
            String showString = (int) speed + "KB/" + getResources().getString(R.string.second);
            mTVPrepareSpeed.setText(showString);
            mTVLoadSpeed.setText(showString);
        }


        public void setSeekbarListener(SeekBar.OnSeekBarChangeListener listener) {
            mSeekBar.setOnSeekBarChangeListener(listener);
        }

        public boolean isControlViewShow() {
            return mControlView.getVisibility() == View.VISIBLE ? true : false;
        }

        public boolean isLoadViewShow() {
            if (mLoadView.getVisibility() == View.VISIBLE ||
                    mPrepareView.getVisibility() == View.VISIBLE) {
                return true;
            }

            return false;
        }

        public void showPlayErrorTip() {
            Toast.makeText(mContext, R.string.toast_musicplay_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                                     int samplingRate) {
            mVisualizerView.updateVisualizer(fft);
        }

        @Override
        public void onWaveFormDataCapture(Visualizer visualizer,
                                          byte[] waveform, int samplingRate) {
            mVisualizerView.updateVisualizer(waveform);
        }

        private final static int DRAW_OFFSET_Y = 200;

        public void updateLyricView(MediaItem mMediaInfo) {
            log.e("updateLyricView song:" + mMediaInfo.title + ", artist:" + mMediaInfo.artist);

            mLyricView.read(mMediaInfo.title, mMediaInfo.artist);
            int pos = 0;
            pos = mPlayerEngineImpl.getCurPosition();
            refreshLyrc(pos);
        }

        public void refreshLyrc(int pos) {
            if (pos > 0) {
                mLyricView.setOffsetY(DRAW_OFFSET_Y - mLyricView.selectIndex(pos)
                        * (mLyricView.getSIZEWORD() + LyricView.INTERVAL - 1));
            } else {
                mLyricView.setOffsetY(DRAW_OFFSET_Y);
            }
            mLyricView.invalidate();
        }
    }

        public void onLoadDrawableComplete(Drawable drawable) {
            if (isDestroy || drawable == null) {
                return;
            }

            mUIManager.updateAlbumPIC(drawable);

        }

        public void seek(int pos) {
            mMusicControlCenter.skipTo(pos);
            mUIManager.setSeekbarProgress(pos);

        }

        private boolean checkNeedDownLyric(MediaItem mediaInfo) {
            String lyricPath = MusicUtils.getLyricFile(mediaInfo.title, mediaInfo.artist);
            if (lyricPath != null) {
                File f = new File(lyricPath);
                if (f.exists()) {
                    return false;
                }
            }

            return true;
        }


        private class MusicPlayEngineListener implements PlayerEngineListener {

            @Override
            public void onTrackPlay(MediaItem itemInfo) {

                mPlayPosTimer.startTimer();
                LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
                mUIManager.showPlay(false);
                mUIManager.showPrepareLoadView(false);
                mUIManager.showControlView(true);
            }

            @Override
            public void onTrackStop(MediaItem itemInfo) {

                mPlayPosTimer.stopTimer();
                mUIManager.showPlay(true);
                mUIManager.updateMediaInfoView(mMediaInfo);
                mUIManager.showLoadView(false);
            }

            @Override
            public void onTrackPause(MediaItem itemInfo) {

                mPlayPosTimer.stopTimer();
                mUIManager.showPlay(true);
            }

            @Override
            public void onTrackPrepareSync(MediaItem itemInfo) {

                mPlayPosTimer.stopTimer();
                mUIManager.updateMediaInfoView(itemInfo);
                mUIManager.showPlay(false);
                mUIManager.showPrepareLoadView(true);
                mUIManager.showControlView(false);

                mMediaInfo = itemInfo;
                boolean need = checkNeedDownLyric(itemInfo);
                log.e("checkNeedDownLyric need = " + need);
                if (need) {
                    mLrcDownLoadHelper.syncDownLoadLRC(itemInfo.title, itemInfo.artist, MusicPlayerFragment.this);
                }
                mUIManager.updateLyricView(itemInfo);
            }

            @Override
            public void onTrackPrepareComplete(MediaItem itemInfo) {

                mPlayPosTimer.stopTimer();
                int duration = mPlayerEngineImpl.getDuration();
                mUIManager.setSeekbarMax(duration);
                mUIManager.setTotalTime(duration);


            }

            @Override
            public void onTrackStreamError(MediaItem itemInfo) {
                log.e("onTrackStreamError");
                mPlayPosTimer.stopTimer();
                mMusicControlCenter.stop();
                mUIManager.showPlayErrorTip();
            }

            @Override
            public void onTrackPlayComplete(MediaItem itemInfo) {
                log.e("onTrackPlayComplete");
                boolean ret = mMusicControlCenter.next();
                if (!ret) {
                    mUIManager.showPlayErrorTip();
                    mUIManager.updateMediaInfoView(itemInfo);
                    mUIManager.showPlay(false);
                    mUIManager.showPrepareLoadView(false);
                    mUIManager.showControlView(true);

                }
            }
        }
}
