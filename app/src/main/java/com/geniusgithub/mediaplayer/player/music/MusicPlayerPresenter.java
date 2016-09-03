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
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.common.AbstractTimer;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.player.music.model.MusicControlCenter;
import com.geniusgithub.mediaplayer.player.music.model.MusicPlayEngineImpl;
import com.geniusgithub.mediaplayer.player.music.util.LoaderHelper;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;

import org.cybergarage.util.AlwaysLog;

import java.io.File;

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
  //      refreshIntent(mFragmentInstance.getActivity().getIntent());
    }

    public void onUiDestroy(){
        isDestroy = true;
        mLrcDownLoadHelper.unInit();
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mMusicControlCenter.exit();
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
                        mView.refreshLyrc(mPlayerEngineImpl.getCurPosition());
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

        mView.showLRCView(false);

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

        mView.updateMediaInfoView(mMediaInfo);
        mPlayerEngineImpl.playMedia(mMediaInfo);

        mView.showPrepareLoadView(true);
        mView.showLoadView(false);
        mView.showControlView(false);

    }

    private void seek(int pos) {
        AlwaysLog.i(TAG, "seek pos =  " + pos);
        mMusicControlCenter.skipTo(pos);
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
        int pos = mPlayerEngineImpl.getCurPosition();

        mView.setSeekbarProgress(pos);

    }

    private void refreshSpeed(){
        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mView.setSpeed(speed);
    }

    private void checkDelay(){
        int pos = mPlayerEngineImpl.getCurPosition();

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
        pos = mPlayerEngineImpl.getCurPosition();
        mView.refreshLyrc(pos);
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

            mView.setSeekbarSecondProgress(time);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mView.showPlayErrorTip();
            log.e("onError what = " + what + ", extra = " + extra);
            return false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mView.setcurTime(progress);
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
            mView.onWaveFormDataCapture(visualizer, waveform, samplingRate);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            mView.onFftDataCapture(visualizer, fft, samplingRate);
        }


    }



    private class MusicPlayEngineListener implements PlayerEngineListener {

        @Override
        public void onTrackPlay(MediaItem itemInfo) {

            mPlayPosTimer.startTimer();
            LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
            mView.showPlay(false);
            mView.showPrepareLoadView(false);
            mView.showControlView(true);
        }

        @Override
        public void onTrackStop(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mView.showPlay(true);
            mView.updateMediaInfoView(mMediaInfo);
            mView.showLoadView(false);
        }

        @Override
        public void onTrackPause(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mView.updateMediaInfoView(itemInfo);
            mView.showPlay(false);
            mView.showPrepareLoadView(true);
            mView.showControlView(false);

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
            mView.setSeekbarMax(duration);
            mView.setTotalTime(duration);


        }

        @Override
        public void onTrackStreamError(MediaItem itemInfo) {
            log.e("onTrackStreamError");
            mPlayPosTimer.stopTimer();
            mMusicControlCenter.stop();
            mView.showPlayErrorTip();
        }

        @Override
        public void onTrackPlayComplete(MediaItem itemInfo) {
            log.e("onTrackPlayComplete");
            boolean ret = mMusicControlCenter.next();
            if (!ret) {
                mView.showPlayErrorTip();
                mView.updateMediaInfoView(itemInfo);
                mView.showPlay(false);
                mView.showPrepareLoadView(false);
                mView.showControlView(true);

            }
        }
    }



}
