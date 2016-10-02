package com.geniusgithub.mediaplayer.player.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.base.MediaItemPlayList;
import com.geniusgithub.mediaplayer.player.base.PlayMode;
import com.geniusgithub.mediaplayer.player.base.PlayStateCallback;
import com.geniusgithub.mediaplayer.player.base.Player;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerPresenter implements MusicPlayerContact.IPresenter,  LrcDownLoadHelper.ILRCDownLoadCallback{

    private final static String TAG = MusicPlayerPresenter.class.getSimpleName();

    private Context mContext;
    private MusicPlayerContact.IView mView;


    public static final String PLAY_INDEX = "player_index";
    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int UPDATE_LRC_VIEW = 0x0003;
    private final static int SHOW_PREPARE_VIEW = 0x0004;
    private final static int SHOW_PREPARE_DELAY = 150;

    private Player mMusicPlayerEngineImpl;
    private MusicPlayStateListener mPlayStateCallback;
    private MediaPlayerListener mMediaPlayerListener;
    private MediaItemPlayList mPLayList;
    private PlayMode mCurPlayMode;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;

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
        mMusicPlayerEngineImpl.play();
    }

    @Override
    public void onMusicPause() {
        AlwaysLog.i(TAG, "onMusicPause");
        mMusicPlayerEngineImpl.pause();
    }

    @Override
    public void onPlayPre() {
        AlwaysLog.i(TAG, "onPlayPre");
        mMusicPlayerEngineImpl.playLast();
    }

    @Override
    public void onPlayNext() {
        AlwaysLog.i(TAG, "onPlayNext");
        mMusicPlayerEngineImpl.playNext();
    }

    @Override
    public void onToggleMode() {
        AlwaysLog.i(TAG, "onToggleMode");

        mCurPlayMode = PlayMode.switchNextMode(mCurPlayMode);
        mMusicPlayerEngineImpl.setPlayMode(mCurPlayMode);
        mView.updatePlayMode(mCurPlayMode);

    }


    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        AlwaysLog.i(TAG, "onSeekStopTrackingTouch ");
        seek(seekBar.getProgress());
    }

    @Override
    public void onPlayItemClick(MediaItem data, int position) {
        AlwaysLog.i(TAG, "onPlayItemClick  position = " + position);
        mPLayList.setPlayingIndex(position);
        mMusicPlayerEngineImpl.play(mPLayList);
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
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mMusicPlayerEngineImpl.releasePlayer();
    }

    public void onNewIntent(Intent intent) {
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
                        mView.refreshLyrc(mMusicPlayerEngineImpl.getProgress());
                        break;
                    case REFRESH_SPEED:
                        refreshSpeed();
                        break;
                    case UPDATE_LRC_VIEW:
                        updateLyricView(mMediaInfo);
                        break;
                    case SHOW_PREPARE_VIEW:
                        boolean bShow = (boolean) msg.obj;
                        AlwaysLog.i(TAG, "SHOW_PREPARE_VIEW bShow = " + bShow);
                        showPrepareView(bShow, 0);
                        break;
                }
            }

        };

        mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);

        mNetWorkTimer = new SingleSecondTimer(mContext);
        mNetWorkTimer.setHandler(mHandler, REFRESH_SPEED);



        mPLayList = new MediaItemPlayList();
        mCurPlayMode = PlayMode.getDefault();
        mPLayList.setPlayMode(mCurPlayMode);
        mMusicPlayerEngineImpl = new MusicPlayerEngine();

        mMediaPlayerListener = new MediaPlayerListener();
        mMusicPlayerEngineImpl.setOnBuffUpdateListener(mMediaPlayerListener);
        mMusicPlayerEngineImpl.setOnErrorListener(mMediaPlayerListener);
        mPlayStateCallback = new MusicPlayStateListener();
        mMusicPlayerEngineImpl.registerCallback(mPlayStateCallback);

        mNetWorkTimer.startTimer();

        mLrcDownLoadHelper = new LrcDownLoadHelper();
        mLrcDownLoadHelper.init();

        mView.showLRCView(false);
        mView.updatePlayMode(mCurPlayMode);
        showPrepareView(false, 0);
        boolean ret = FileHelper.createDirectory(MusicUtils.getLyricDir());
        AlwaysLog.i(TAG, " FileHelper.createDirectory:" + MusicUtils.getLyricDir() + ", ret = " + ret);
    }


    private int unitTest(){
/*        mMediaInfo.title = "天后";
        mMediaInfo.artist = "陈势安";
        mMediaInfo.album = "天后(台湾版)";
        mMediaInfo.resInfo.res = "http://192.168.1.5:57645/external/audio/media/7258.mp3";
        mMediaInfo.albumarturi = "http://192.168.1.5:57645/external/audio/albums/31.jpg";*/

        mMediaInfo.title = "想你的夜";
        mMediaInfo.artist = "陶波";
        mMediaInfo.album = "想你的夜";
        mMediaInfo.resInfo.res = "http://192.168.1.107:57645/external/audio/media/141.mp3";
        mMediaInfo.albumarturi = "http://192.168.1.107:57645/external/audio/albums/5.jpg";
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

      //  curIndex = unitTest();

        AlwaysLog.i(TAG, "refreshIntent curIndex = " + curIndex);
         AlwaysLog.i(TAG, "mMediaInfo = " + mMediaInfo.getShowString());

        mPLayList.setMediaList(MediaManager.getInstance().getMusicList());
        mPLayList.setPlayingIndex(curIndex);
        mMusicPlayerEngineImpl.play(mPLayList);

        mView.updatePlayList(MediaManager.getInstance().getMusicList());
        showPrepareView(true, SHOW_PREPARE_DELAY);

    }

    private void seek(int pos) {
        AlwaysLog.i(TAG, "seek pos =  " + pos);
        mMusicPlayerEngineImpl.seekTo(pos);
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
        int pos = mMusicPlayerEngineImpl.getProgress();

        mView.setSeekbarProgress(pos);

    }

    private void refreshSpeed(){
        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mView.setSpeed(speed);
    }


    private void updateLyricView(MediaItem mMediaInfo) {
        mView.updateLyricView(mMediaInfo);
        int pos = 0;
        pos = mMusicPlayerEngineImpl.getProgress();
        mView.refreshLyrc(pos);
    }

    private void showPrepareView(boolean bShow, long timeDelay){
        mHandler.removeMessages(SHOW_PREPARE_VIEW);
        if (timeDelay != 0){
            Message msg = mHandler.obtainMessage(SHOW_PREPARE_VIEW, bShow);
            mHandler.sendMessageDelayed(msg, SHOW_PREPARE_DELAY);
        }else{
            mView.showPrepareLoadView(bShow);
        }

    }


    private class MediaPlayerListener implements  MediaPlayer.OnBufferingUpdateListener,
                                                MediaPlayer.OnErrorListener{

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            int duration = mMusicPlayerEngineImpl.getDuration();
            int time = duration * percent / 100;

            mView.setSeekbarSecondProgress(time);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mView.showPlayErrorTip();
            AlwaysLog.e(TAG, "onError what = " + what + ", extra = " + extra);
            return false;
        }

    }



    private class MusicPlayStateListener implements PlayStateCallback {

        @Override
        public void onTrackPlay() {
            mPlayPosTimer.startTimer();
            mView.startRotateAnimation(true);
            mView.showPlay(false);
        }

        @Override
        public void onTrackStop() {
            mPlayPosTimer.stopTimer();
            mView.startRotateAnimation(false);
            mView.showPlay(true);
        }

        @Override
        public void onTrackPause() {
            mPlayPosTimer.stopTimer();
            mView.startRotateAnimation(false);
            mView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync() {
            mMediaInfo = mPLayList.getCurrentMedia();
            mPlayPosTimer.stopTimer();
            mView.updateMediaInfoView(mMediaInfo, mPLayList.getPlayingIndex());
            mView.showPlay(false);
            showPrepareView(true, SHOW_PREPARE_DELAY);

         /*   boolean need = checkNeedDownLyric(itemInfo);
            log.i("checkNeedDownLyric need = " + need);
            if (need) {
                mLrcDownLoadHelper.syncDownLoadLRC(itemInfo.title, itemInfo.artist, MusicPlayerPresenter.this);
            }
            updateLyricVisew(itemInfo);*/
        }

        @Override
        public void onTrackPrepareComplete() {
            showPrepareView(false, 0);
            int duration = mMusicPlayerEngineImpl.getDuration();
            mView.setSeekbarMax(duration);
            mView.setTotalTime(duration);
        }

        @Override
        public void onTrackStreamError() {
            AlwaysLog.e(TAG, "onTrackStreamError");
            mPlayPosTimer.stopTimer();
            mView.startRotateAnimation(false);
            mMusicPlayerEngineImpl.stop();
            mView.showPlayErrorTip();
        }

    }



}
