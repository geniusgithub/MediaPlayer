package com.geniusgithub.mediaplayer.player.music;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.common.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.player.music.util.LoaderHelper;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;

import org.cybergarage.util.AlwaysLog;

import java.io.File;

public class MusicPlayerPresenter implements IBaseFragmentPresent, IMusicPlayerPresenter, LrcDownLoadHelper.ILRCDownLoadCallback{


    private final static String TAG = MusicPlayerPresenter.class.getSimpleName();

    /////////////////////////////////////////////////
    public static interface IMusicPlayerView{
        public void bindView(Context context, View container);
        public void bindPresent(IMusicPlayerPresenter presenter);
        public void showPlay(boolean bShow);
        public void showPrepareLoadView(boolean bShow);
        public void showControlView(boolean bShow);
        public void updateLyricView(MediaItem itemInfo);
        public void updateMediaInfoView(MediaItem itemInfo);
        public void showLoadView(boolean bShow);
        public void showLRCView(boolean bShow);
        public void showPlayErrorTip();
        public void setSeekbarMax(int max);
        public void setSeekbarSecondProgress(int max);
        public void setTotalTime(int totalTime);
        public void refreshLyrc(int pos);
        public boolean isLRCViewShow();
        public void setSeekbarProgress(int pos);
        public boolean isLoadViewShow();
        public void setSpeed(float speed);
        public void updateAlbumPIC(Drawable drawable);
        public void setcurTime(int curTime);
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate);
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate);
    }


    private IMusicPlayerView mIMusicPlayerView;
    private IMusicPlayerView createMusicPlayerView(){
        return new MusicPlayerView();
    }
    /////////////////////////////////////////////////

    private Context mContext;
    private Fragment mFragmentInstance;

    public static final String PLAY_INDEX = "player_index";

    private static final CommonLog log = LogFactory.createLog();

    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int CHECK_DELAY = 0x0003;
    private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;
    private final static int UPDATE_LRC_VIEW = 0x0007;


    private MusicPlayEngineImpl mPlayerEngineImpl;
    private MusicPlayEngineListener mPlayEngineListener;
    private MusicControlCenter mMusicControlCenter;

    private MediaPlayerListener mMediaPlayerListener;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;
    private CheckDelayTimer mCheckDelayTimer;

    private boolean isDestroy = false;


    private LrcDownLoadHelper mLrcDownLoadHelper;


    @Override
    public void bindFragment(Fragment fragment) {
        mFragmentInstance = fragment;
    }


    @Override
    public void onNewIntent(Intent intent) {
        log.e("onNewIntent");
        refreshIntent(intent);

    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIMusicPlayerView = createMusicPlayerView();
        mIMusicPlayerView.bindPresent(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_player_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mIMusicPlayerView.bindView(mContext, view);

        initData();
        refreshIntent(mFragmentInstance.getActivity().getIntent());
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }


    @Override
    public void onDestroy() {
        isDestroy = true;
        mLrcDownLoadHelper.unInit();
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mMusicControlCenter.exit();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }


    ///////////////////////////////////////////////// presenter callback begin
    @Override
    public void onMusicPlay() {
        AlwaysLog.i(TAG, "onMusicPlay");
        mMusicControlCenter.replay();
    }

    @Override
    public void onMusicPause() {
        AlwaysLog.i(TAG, "onMusicPause");
        mMusicControlCenter.pause();
    }

    @Override
    public void onPlayPre() {
        AlwaysLog.i(TAG, "onPlayPre");
        mMusicControlCenter.prev();
    }

    @Override
    public void onPlayNext() {
        AlwaysLog.i(TAG, "onPlayNext");
        mMusicControlCenter.next();
    }

    @Override
    public void onSeekProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    //    AlwaysLog.i(TAG, "onSeekProgressChanged progress = " + progress);
        mIMusicPlayerView.setcurTime(progress);
    }

    @Override
    public void onSeekStartTrackingTouch(SeekBar seekBar) {
        AlwaysLog.i(TAG, "onSeekStartTrackingTouch ");
    }

    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        AlwaysLog.i(TAG, "onSeekStopTrackingTouch ");
        seek(seekBar.getProgress());
    }
    ///////////////////////////////////////////////// presenter callback end


    @Override
    public void lrcDownLoadComplete(boolean isSuccess, String song, String artist) {
        if (isSuccess && song.equals(mMediaInfo.title) && artist.equals(mMediaInfo.artist)){
            Message msg = mHandler.obtainMessage(UPDATE_LRC_VIEW);
            msg.sendToTarget();
        }
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
                        mIMusicPlayerView.refreshLyrc(mPlayerEngineImpl.getCurPosition());
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
                        updateLyricView(mMediaInfo);
                        break;
                }
            }

        };

        mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);

        mNetWorkTimer = new SingleSecondTimer(mContext);
        mNetWorkTimer.setHandler(mHandler, REFRESH_SPEED);
        mCheckDelayTimer = new CheckDelayTimer(mContext);
        mCheckDelayTimer.setHandler(mHandler, CHECK_DELAY);

        mMediaPlayerListener = new MediaPlayerListener();

        mPlayerEngineImpl = new MusicPlayEngineImpl(mContext);
        mPlayerEngineImpl.setOnBuffUpdateListener(mMediaPlayerListener);
        mPlayerEngineImpl.setOnSeekCompleteListener(mMediaPlayerListener);
        mPlayerEngineImpl.setDataCaptureListener(mMediaPlayerListener);
        mPlayEngineListener = new MusicPlayEngineListener();
        mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);

        mMusicControlCenter = new MusicControlCenter(mContext);
        mMusicControlCenter.bindMusicPlayEngine(mPlayerEngineImpl);


        mNetWorkTimer.startTimer();
        mCheckDelayTimer.startTimer();

        mLrcDownLoadHelper = new LrcDownLoadHelper();
        mLrcDownLoadHelper.init();

        mIMusicPlayerView.showLRCView(false);

        boolean ret = FileHelper.createDirectory(MusicUtils.getLyricDir());
        log.i(" FileHelper.createDirectory:" + MusicUtils.getLyricDir() + ", ret = " + ret);
    }


    public void refreshIntent(Intent intent){

        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }

        AlwaysLog.i(TAG, "refreshIntent curIndex = " + curIndex);

        mMusicControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getMusicList());

        mIMusicPlayerView.updateMediaInfoView(mMediaInfo);
        mPlayerEngineImpl.playMedia(mMediaInfo);

        mIMusicPlayerView.showPrepareLoadView(true);
        mIMusicPlayerView.showLoadView(false);
        mIMusicPlayerView.showControlView(false);

    }

    private void seek(int pos) {
        AlwaysLog.i(TAG, "seek pos =  " + pos);
        mMusicControlCenter.skipTo(pos);
        mIMusicPlayerView.setSeekbarProgress(pos);

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

/*    public void toggleLRCView() {
        if (mIMusicPlayerView.isLRCViewShow()){
            mIMusicPlayerView.showLRCView(false);
        }else{
            mIMusicPlayerView.showLRCView(true);
        }

    }*/

    private void refreshCurPos(){
        int pos = mPlayerEngineImpl.getCurPosition();

        mIMusicPlayerView.setSeekbarProgress(pos);

    }



    private void refreshSpeed(){
      //  if (mIMusicPlayerView.isLoadViewShow()){
            float speed = CommonUtil.getSysNetworkDownloadSpeed();
            mIMusicPlayerView.setSpeed(speed);
     //   }
    }

    private void checkDelay(){
        int pos = mPlayerEngineImpl.getCurPosition();

        boolean ret = mCheckDelayTimer.isDelay(pos);
        if (ret){
            mIMusicPlayerView.showLoadView(true);
        }else{
            mIMusicPlayerView.showLoadView(false);
        }

        mCheckDelayTimer.setPos(pos);

    }

    public void onLoadDrawableComplete(Drawable drawable) {
        if (isDestroy || drawable == null) {
            return;
        }

        mIMusicPlayerView.updateAlbumPIC(drawable);

    }

    private void updateLyricView(MediaItem mMediaInfo) {
        //     log.e("updateLyricView song:" + mMediaInfo.title + ", artist:" + mMediaInfo.artist);

        mIMusicPlayerView.updateLyricView(mMediaInfo);
        int pos = 0;
        pos = mPlayerEngineImpl.getCurPosition();
        mIMusicPlayerView.refreshLyrc(pos);
    }


    private class MediaPlayerListener implements  MediaPlayer.OnBufferingUpdateListener,
                                                  MediaPlayer.OnErrorListener,
                                                  SeekBar.OnSeekBarChangeListener,
                                                    MediaPlayer.OnSeekCompleteListener,
                                                   Visualizer.OnDataCaptureListener{

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            int duration = mPlayerEngineImpl.getDuration();
            int time = duration * percent / 100;

            mIMusicPlayerView.setSeekbarSecondProgress(time);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mIMusicPlayerView.showPlayErrorTip();
            log.e("onError what = " + what + ", extra = " + extra);
            return false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mIMusicPlayerView.setcurTime(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seek(seekBar.getProgress());
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            log.i("onSeekComplete ...");
        }

        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
             mIMusicPlayerView.onWaveFormDataCapture(visualizer, waveform, samplingRate);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            mIMusicPlayerView.onFftDataCapture(visualizer, fft, samplingRate);
        }


    }

    private class MusicPlayEngineListener implements PlayerEngineListener {

        @Override
        public void onTrackPlay(MediaItem itemInfo) {

            mPlayPosTimer.startTimer();
            LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
            mIMusicPlayerView.showPlay(false);
            mIMusicPlayerView.showPrepareLoadView(false);
            mIMusicPlayerView.showControlView(true);
        }

        @Override
        public void onTrackStop(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIMusicPlayerView.showPlay(true);
            mIMusicPlayerView.updateMediaInfoView(mMediaInfo);
            mIMusicPlayerView.showLoadView(false);
        }

        @Override
        public void onTrackPause(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIMusicPlayerView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIMusicPlayerView.updateMediaInfoView(itemInfo);
            mIMusicPlayerView.showPlay(false);
            mIMusicPlayerView.showPrepareLoadView(true);
            mIMusicPlayerView.showControlView(false);

            mMediaInfo = itemInfo;
         /*   boolean need = checkNeedDownLyric(itemInfo);
            log.i("checkNeedDownLyric need = " + need);
            if (need) {
                mLrcDownLoadHelper.syncDownLoadLRC(itemInfo.title, itemInfo.artist, MusicPlayerPresenter.this);
            }*/
            updateLyricView(itemInfo);
        }

        @Override
        public void onTrackPrepareComplete(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            int duration = mPlayerEngineImpl.getDuration();
            mIMusicPlayerView.setSeekbarMax(duration);
            mIMusicPlayerView.setTotalTime(duration);


        }

        @Override
        public void onTrackStreamError(MediaItem itemInfo) {
            log.e("onTrackStreamError");
            mPlayPosTimer.stopTimer();
            mMusicControlCenter.stop();
            mIMusicPlayerView.showPlayErrorTip();
        }

        @Override
        public void onTrackPlayComplete(MediaItem itemInfo) {
            log.e("onTrackPlayComplete");
            boolean ret = mMusicControlCenter.next();
            if (!ret) {
                mIMusicPlayerView.showPlayErrorTip();
                mIMusicPlayerView.updateMediaInfoView(itemInfo);
                mIMusicPlayerView.showPlay(false);
                mIMusicPlayerView.showPrepareLoadView(false);
                mIMusicPlayerView.showControlView(true);

            }
        }
    }
}
