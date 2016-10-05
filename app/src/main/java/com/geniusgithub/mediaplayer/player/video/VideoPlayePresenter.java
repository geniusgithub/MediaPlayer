package com.geniusgithub.mediaplayer.player.video;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.SeekBar;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.common.util.CommonUtil;
import com.geniusgithub.mediaplayer.component.MediaItemFactory;
import com.geniusgithub.mediaplayer.component.MediaManager;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.base.MediaItemPlayList;
import com.geniusgithub.mediaplayer.player.base.PlayStateCallback;


public class VideoPlayePresenter implements  VideoPlayerContact.IPresenter{

    private final static String TAG = VideoPlayePresenter.class.getSimpleName();

    private Context mContext;
    private VideoPlayerContact.IView mIVideoPlayerView;

    public static final String PLAY_INDEX = "player_index";

    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int CHECK_DELAY = 0x0003;
    private final static int HIDE_TOOL = 0x0004;
    private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;


    private final static int HIDE_DELAY_TIME = 3000;


    private VideoPlayerEngine mPlayerEngineImpl;
    private VideoPlayStateListener mPlayEngineListener;
    private MediaPlayerListener mMediaPlayListener;
    private MediaItemPlayList mPLayList;


    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;
    private CheckDelayTimer mCheckDelayTimer;

    private boolean isDestroy = false;
    private boolean isFirstPlay = true;
    private int mCurProgress = 0;

    public VideoPlayePresenter(Context context){
        mContext = context;
    }

    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(VideoPlayerContact.IView view) {
        mIVideoPlayerView = view;
        mIVideoPlayerView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }


    @Override
    public void onVideoRePlay(SurfaceHolder holder) {
        mPlayerEngineImpl.setHolder(holder);
        if (isFirstPlay){
            mPlayerEngineImpl.play(mPLayList);
            isFirstPlay = false;
        }else{
            if (mCurProgress != 0){
                mPlayerEngineImpl.setInitPlayProgress(mCurProgress);
                mCurProgress = 0;
            }
            mPlayerEngineImpl.play();
        }

    }

    @Override
    public void onVideoPlay() {
        mPlayerEngineImpl.play();
    }

    @Override
    public void onVideoPause() {
        mPlayerEngineImpl.pause();
    }

    @Override
    public void onVideoStop() {
        mCurProgress = mPlayerEngineImpl.getProgress();
        mPlayerEngineImpl.stop();
    }

    @Override
    public void onPlayPre() {
        mPlayerEngineImpl.playLast();
    }

    @Override
    public void onPlayNext() {
        mPlayerEngineImpl.playNext();
    }


    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        seek(seekBar.getProgress());
    }
    ///////////////////////////////////////     presenter callback end







    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context){
        initData();

    }

    public void onNewIntent(Intent intent) {
        refreshIntent(intent);
    }

    public void onUiDestroy(){
        isDestroy = true;
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mPlayerEngineImpl.releasePlayer();
    }


    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int actionIdx = ev.getActionIndex();
        int actionMask = ev.getActionMasked();

        if(actionIdx == 0 && action == MotionEvent.ACTION_UP) {
            if(!mIVideoPlayerView.isControlViewShow()) {
                showControlView(true);
                return true;
            }else{
                delayToHideControlPanel();
            }
        }

        return false;
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
                        break;
                    case REFRESH_SPEED:
                        refreshSpeed();
                        break;
                    case CHECK_DELAY:
                        checkDelay();
                        break;
                    case HIDE_TOOL:
                        if (!mPlayerEngineImpl.isPause()){
                            showControlView(false);
                        }
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
        mPlayerEngineImpl = new VideoPlayerEngine(mContext);

        mMediaPlayListener = new MediaPlayerListener();
        mPlayerEngineImpl.setOnBuffUpdateListener(mMediaPlayListener);
        mPlayerEngineImpl.setOnErrorListener(mMediaPlayListener);


        mPlayEngineListener = new VideoPlayStateListener();
        mPlayerEngineImpl.registerCallback(mPlayEngineListener);

        mNetWorkTimer.startTimer();
        mCheckDelayTimer.startTimer();
    }

    private void refreshIntent(Intent intent){
        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }

        AlwaysLog.i(TAG, "refreshIntent curIndex = " + curIndex);
        mPLayList.setMediaList(MediaManager.getInstance().getVideoList());
        mPLayList.setPlayingIndex(curIndex);

        mIVideoPlayerView.showPrepareLoadView(true);
        mIVideoPlayerView.showLoadView(false);
        showControlView(false);
    }

    private void removeHideMessage(){
        mHandler.removeMessages(HIDE_TOOL);
    }

    private void delayToHideControlPanel(){
        removeHideMessage();
        mHandler.sendEmptyMessageDelayed(HIDE_TOOL, HIDE_DELAY_TIME);
    }


    public void refreshCurPos(){
        int pos = mPlayerEngineImpl.getProgress();
        mIVideoPlayerView.setSeekbarProgress(pos);

    }

    public void refreshSpeed(){

        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mIVideoPlayerView.setSpeed(speed);

    }

    public void checkDelay(){
        int pos = mPlayerEngineImpl.getProgress();

        boolean ret = mCheckDelayTimer.isDelay(pos);
        if (ret){
            mIVideoPlayerView.showLoadView(true);
        }else{
            mIVideoPlayerView.showLoadView(false);
        }

        mCheckDelayTimer.setPos(pos);

    }

    public void showControlView(boolean bShow){
        if (bShow) {
            delayToHideControlPanel();
        }
        mIVideoPlayerView.showControlView(bShow);
    }

    public void seek(int pos){
        mPlayerEngineImpl.seekTo(pos);
        mIVideoPlayerView.setSeekbarProgress(pos);

    }


    private class MediaPlayerListener implements  MediaPlayer.OnBufferingUpdateListener,
                                                    MediaPlayer.OnErrorListener{


        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            int duration = mPlayerEngineImpl.getDuration();
            int time = duration * percent / 100;
            mIVideoPlayerView.setSeekbarSecondProgress(time);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mIVideoPlayerView.showPlayErrorTip();
            AlwaysLog.e(TAG, "onError what = " + what + ", extra = " + extra);
            return false;
        }

    }

    private class VideoPlayStateListener implements PlayStateCallback {


        @Override
        public void onTrackPlay() {
            AlwaysLog.i(TAG, "onTrackPlay");
            mPlayPosTimer.startTimer();
            mIVideoPlayerView.showPlay(false);
        }

        @Override
        public void onTrackStop() {
            AlwaysLog.i(TAG, "onTrackStop");
            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.showPlay(true);
            mIVideoPlayerView.showLoadView(false);
        }

        @Override
        public void onTrackPause() {
            AlwaysLog.i(TAG, "onTrackPause");
            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync() {
            mMediaInfo = mPLayList.getCurrentMedia();
            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.updateMediaInfoView(mMediaInfo);
            mIVideoPlayerView.showPlay(false);
            mIVideoPlayerView.showPrepareLoadView(true);
            showControlView(false);
        }

        @Override
        public void onTrackPrepareComplete() {
            mIVideoPlayerView.showPrepareLoadView(false);
            mIVideoPlayerView.showControlView(true);

            int duration = mPlayerEngineImpl.getDuration();
            mIVideoPlayerView.setSeekbarMax(duration);
            mIVideoPlayerView.setTotalTime(duration);
        }

        @Override
        public void onTrackStreamError() {
                AlwaysLog.e(TAG, "onTrackStreamError");
                mPlayPosTimer.stopTimer();
                mPlayerEngineImpl.stop();
                mIVideoPlayerView.showPlayErrorTip();

        }
    }

}
