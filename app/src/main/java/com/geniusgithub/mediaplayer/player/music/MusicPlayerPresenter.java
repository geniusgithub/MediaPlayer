package com.geniusgithub.mediaplayer.player.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.base.MediaItemPlayList;
import com.geniusgithub.mediaplayer.player.base.PlayStateCallback;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.player.music.util.LoaderHelper;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;

import org.cybergarage.util.AlwaysLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerPresenter implements MusicPlayerContact.IPresenter,  LrcDownLoadHelper.ILRCDownLoadCallback{

    private final static String TAG = MusicPlayerPresenter.class.getSimpleName();
    private static final CommonLog log = LogFactory.createLog();

    private Context mContext;
    private MusicPlayerContact.IView mView;


    public static final String PLAY_INDEX = "player_index";
    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int CHECK_DELAY = 0x0003;
    private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;
    private final static int UPDATE_LRC_VIEW = 0x0007;


    private MusicPlayerEngine mPlayerEngineImpl;
    private MusicPlayStateListener mPlayStateCallback;
    private MediaPlayerListener mMediaPlayerListener;
    private MediaItemPlayList mPLayList;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;
    private CheckDelayTimer mCheckDelayTimer;

    private boolean isDestroy = false;


    private LrcDownLoadHelper mLrcDownLoadHelper;


    public MusicPlayerPresenter(){
        mContext = AllShareApplication.getInstance();
    }

    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(MusicPlayerContact.IView view) {
        mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }


    @Override
    public void onMusicPlay() {
        AlwaysLog.i(TAG, "onMusicPlay");
        mPlayerEngineImpl.play();
    }

    @Override
    public void onMusicPause() {
        AlwaysLog.i(TAG, "onMusicPause");
        mPlayerEngineImpl.pause();
    }

    @Override
    public void onPlayPre() {
        AlwaysLog.i(TAG, "onPlayPre");
        mPlayerEngineImpl.playLast();
    }

    @Override
    public void onPlayNext() {
        AlwaysLog.i(TAG, "onPlayNext");
        mPlayerEngineImpl.playNext();
    }



    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        AlwaysLog.i(TAG, "onSeekStopTrackingTouch ");
        seek(seekBar.getProgress());
    }
    ///////////////////////////////////////     presenter callback end



    @Override
    public void lrcDownLoadComplete(boolean isSuccess, String song, String artist) {
        if (isSuccess && song.equals(mMediaInfo.title) && artist.equals(mMediaInfo.artist)){
            Message msg = mHandler.obtainMessage(UPDATE_LRC_VIEW);
            msg.sendToTarget();
        }
    }


    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context){
        mContext = context;

        initData();
    }

    public void onUiDestroy(){
        isDestroy = true;
        mLrcDownLoadHelper.unInit();
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mPlayerEngineImpl.releasePlayer();
    }

    public void onNewIntent(Intent intent) {
        log.e("onNewIntent");
        refreshIntent(intent);

    }
    ///////////////////////////////////////     lifecycle or ui operator end


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
                        mView.refreshLyrc(mPlayerEngineImpl.getProgress());
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


        mPLayList = new MediaItemPlayList();
        mPlayerEngineImpl = new MusicPlayerEngine();

        mMediaPlayerListener = new MediaPlayerListener();
        mPlayerEngineImpl.setOnBuffUpdateListener(mMediaPlayerListener);
        mPlayerEngineImpl.setOnErrorListener(mMediaPlayerListener);
        mPlayerEngineImpl.setDataCaptureListener(mMediaPlayerListener);

        mPlayStateCallback = new MusicPlayStateListener();
        mPlayerEngineImpl.registerCallback(mPlayStateCallback);


        mNetWorkTimer.startTimer();
        mCheckDelayTimer.startTimer();

        mLrcDownLoadHelper = new LrcDownLoadHelper();
        mLrcDownLoadHelper.init();

        mView.showLRCView(false);

        boolean ret = FileHelper.createDirectory(MusicUtils.getLyricDir());
        log.i(" FileHelper.createDirectory:" + MusicUtils.getLyricDir() + ", ret = " + ret);
    }


    private int unitTest(){
        mMediaInfo.title = "天后";
        mMediaInfo.artist = "陈势安";
        mMediaInfo.album = "天后(台湾版)";
        mMediaInfo.resInfo.res = "http://192.168.1.5:57645/external/audio/media/7258.mp3";
        mMediaInfo.albumarturi = "http://192.168.1.5:57645/external/audio/albums/31.jpg";
        List<MediaItem> list = new ArrayList<MediaItem>();
        list.add(mMediaInfo);
        MediaManager.getInstance().setMusicList(list);
        return 0;
    }

    public void refreshIntent(Intent intent){

        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }

     //   curIndex = unitTest();

        AlwaysLog.i(TAG, "refreshIntent curIndex = " + curIndex);
         AlwaysLog.i(TAG, "mMediaInfo = " + mMediaInfo.getShowString());

        mPLayList.setMediaList(MediaManager.getInstance().getMusicList());
        mPLayList.setPlayingIndex(curIndex);
        mPlayerEngineImpl.play(mPLayList);

        mView.showPrepareLoadView(true);
        mView.showLoadView(false);
        mView.showControlView(false);

    }

    private void seek(int pos) {
        AlwaysLog.i(TAG, "seek pos =  " + pos);
        mPlayerEngineImpl.seekTo(pos);
        mView.setSeekbarProgress(pos);

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

    private void refreshCurPos(){
        int pos = mPlayerEngineImpl.getProgress();

        mView.setSeekbarProgress(pos);

    }

    private void refreshSpeed(){
        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mView.setSpeed(speed);
    }

    private void checkDelay(){
        int pos = mPlayerEngineImpl.getProgress();

        boolean ret = mCheckDelayTimer.isDelay(pos);
        if (ret){
            mView.showLoadView(true);
        }else{
            mView.showLoadView(false);
        }

        mCheckDelayTimer.setPos(pos);

    }


    public void onLoadDrawableComplete(Drawable drawable) {
        if (isDestroy) {
            return;
        }

        mView.updateAlbumPIC(drawable);

    }

    private void updateLyricView(MediaItem mMediaInfo) {
        mView.updateLyricView(mMediaInfo);
        int pos = 0;
        pos = mPlayerEngineImpl.getProgress();
        mView.refreshLyrc(pos);
    }



    private class MediaPlayerListener implements  MediaPlayer.OnBufferingUpdateListener,
                                                MediaPlayer.OnErrorListener,
                                                Visualizer.OnDataCaptureListener{

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            int duration = mPlayerEngineImpl.getDuration();
            int time = duration * percent / 100;

            mView.setSeekbarSecondProgress(time);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mView.showPlayErrorTip();
            log.e("onError what = " + what + ", extra = " + extra);
            return false;
        }


        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            mView.onWaveFormDataCapture(visualizer, waveform, samplingRate);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            mView.onFftDataCapture(visualizer, fft, samplingRate);
        }


    }



    private class MusicPlayStateListener implements PlayStateCallback {

        @Override
        public void onTrackPlay() {
            mPlayPosTimer.startTimer();
            mView.showPlay(false);
        }

        @Override
        public void onTrackStop() {
            mPlayPosTimer.stopTimer();
            mView.showPlay(true);
            mView.showLoadView(false);
        }

        @Override
        public void onTrackPause() {
            mPlayPosTimer.stopTimer();
            mView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync() {
            mMediaInfo = mPLayList.getCurrentMedia();
            mPlayPosTimer.stopTimer();
            mView.updateMediaInfoView(mMediaInfo);
            mView.showPlay(false);
            mView.showPrepareLoadView(true);
            mView.showControlView(false);

            LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
         /*   boolean need = checkNeedDownLyric(itemInfo);
            log.i("checkNeedDownLyric need = " + need);
            if (need) {
                mLrcDownLoadHelper.syncDownLoadLRC(itemInfo.title, itemInfo.artist, MusicPlayerPresenter.this);
            }
            updateLyricVisew(itemInfo);*/
        }

        @Override
        public void onTrackPrepareComplete() {
            mView.showPrepareLoadView(false);
            mView.showControlView(true);

            int duration = mPlayerEngineImpl.getDuration();
            mView.setSeekbarMax(duration);
            mView.setTotalTime(duration);
        }

        @Override
        public void onTrackStreamError() {
            log.e("onTrackStreamError");
            mPlayPosTimer.stopTimer();
            mPlayerEngineImpl.stop();
            mView.showPlayErrorTip();
        }

    }



}
