package com.geniusgithub.mediaplayer.player.video;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.common.AbstractTimer;
import com.geniusgithub.mediaplayer.player.music.util.LoaderHelper;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class VideoPlayerPresenter  implements IBaseFragmentPresent, IVideoPlayerPresenter,
                                                   MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {




    /////////////////////////////////////////////////
    public static interface IVideoPlayerView{
        public void bindView(Context context, View container);
        public void bindPresent(IVideoPlayerPresenter presenter);
        public void showPlay(boolean bShow);
        public void showPrepareLoadView(boolean bShow);
        public void showControlView(boolean bShow);
        public void showLoadView(boolean bShow);
        public void updateMediaInfoView(MediaItem mediaInfo);
        public void showPlayErrorTip();
        public void setSeekbarMax(int max);
        public void setSeekbarSecondProgress(int max);
        public void setTotalTime(int totalTime);
        public void setSeekbarProgress(int pos);
        public boolean isLoadViewShow();
        public boolean isControlViewShow();
        public void setSpeed(float speed);
        public void setcurTime(int curTime);
        public SurfaceHolder getSurfaceHolder();
        public boolean isSurfaceCreate();
    }


    private IVideoPlayerView mIVideoPlayerView;
    private IVideoPlayerView createVideoPlayerView(){
        return new VideoPlayerView();
    }
    /////////////////////////////////////////////////

    private Context mContext;
    private Fragment mFragmentInstance;

    public static final String PLAY_INDEX = "player_index";


    private static final CommonLog log = LogFactory.createLog();

    private final static int REFRESH_CURPOS = 0x0001;
    private final static int REFRESH_SPEED = 0x0002;
    private final static int CHECK_DELAY = 0x0003;
    private final static int HIDE_TOOL = 0x0004;
    private final static int LOAD_DRAWABLE_COMPLETE = 0x0006;


    private final static int HIDE_DELAY_TIME = 3000;


    private VideoPlayEngineImpl mPlayerEngineImpl;
    private VideoPlayEngineListener mPlayEngineListener;
    private VideoControlCenter mVideoControlCenter;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mNetWorkTimer;
    private CheckDelayTimer mCheckDelayTimer;

    private boolean isDestroy = false;



    @Override
    public void bindFragment(Fragment fragment) {
        mFragmentInstance = fragment;
    }

    @Override
    public void onNewIntent(Intent intent) {
        refreshIntent(intent);
    }

    @Override
    public void onAttach(Context context) {
            mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIVideoPlayerView = createVideoPlayerView();
        mIVideoPlayerView.bindPresent(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mIVideoPlayerView.bindView(mContext, view);

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
        log.e("onDestroy");
        isDestroy = true;
        mCheckDelayTimer.stopTimer();
        mNetWorkTimer.stopTimer();
        mPlayPosTimer.stopTimer();
        mVideoControlCenter.exit();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
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

    @Override
    public void onVideoPlay() {
        mVideoControlCenter.replay();
    }

    @Override
    public void onVideoPause() {
        mVideoControlCenter.pause();
    }

    @Override
    public void onPlayPre() {
        mVideoControlCenter.prev();
    }

    @Override
    public void onPlayNext() {
        mVideoControlCenter.next();
    }

    @Override
    public void onSeekProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mIVideoPlayerView.setcurTime(progress);
    }

    @Override
    public void onSeekStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        seek(seekBar.getProgress());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int duration = mPlayerEngineImpl.getDuration();
        int time = duration * percent / 100;
        mIVideoPlayerView.setSeekbarSecondProgress(time);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        log.e("onSeekComplete ...");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIVideoPlayerView.showPlayErrorTip();
        log.e("onError what = " + what + ", extra = " + extra);
        return false;
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

        mPlayerEngineImpl = new VideoPlayEngineImpl(mContext, mIVideoPlayerView.getSurfaceHolder());
        mPlayerEngineImpl.setOnBuffUpdateListener(this);
        mPlayerEngineImpl.setOnSeekCompleteListener(this);

        mPlayEngineListener = new VideoPlayEngineListener();
        mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);

        mVideoControlCenter = new VideoControlCenter(mContext);
        mVideoControlCenter.bindVideoPlayEngine(mPlayerEngineImpl);


        mNetWorkTimer.startTimer();
        mCheckDelayTimer.startTimer();
    }

    private void refreshIntent(Intent intent){
        log.e("refreshIntent");
        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }

        mVideoControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getVideoList());

        mIVideoPlayerView.updateMediaInfoView(mMediaInfo);
        if (mIVideoPlayerView.isSurfaceCreate()){
            mPlayerEngineImpl.playMedia(mMediaInfo);
        }else{
            delayToPlayMedia(mMediaInfo);
        }

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

    private void delayToPlayMedia(final MediaItem mMediaInfo){

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isDestroy){
                    mPlayerEngineImpl.playMedia(mMediaInfo);
                }else{
                    log.e("activity destroy...so don't playMedia...");
                }
            }
        }, 1000);
    }

    public void refreshCurPos(){
        int pos = mPlayerEngineImpl.getCurPosition();

        mIVideoPlayerView.setSeekbarProgress(pos);

    }

    public void refreshSpeed(){

        float speed = CommonUtil.getSysNetworkDownloadSpeed();
        mIVideoPlayerView.setSpeed(speed);

    }

    public void checkDelay(){
        int pos = mPlayerEngineImpl.getCurPosition();

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
        mVideoControlCenter.skipTo(pos);
        mIVideoPlayerView.setSeekbarProgress(pos);

    }

    private class VideoPlayEngineListener implements PlayerEngineListener {

        @Override
        public void onTrackPlay(MediaItem itemInfo) {

            mPlayPosTimer.startTimer();
            LoaderHelper.syncDownLoadDrawable(mMediaInfo.getAlbumUri(), mHandler, LOAD_DRAWABLE_COMPLETE);
            mIVideoPlayerView.showPlay(false);
            mIVideoPlayerView.showPrepareLoadView(false);
            showControlView(true);
        }

        @Override
        public void onTrackStop(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.showPlay(true);
            mIVideoPlayerView.updateMediaInfoView(mMediaInfo);
            mIVideoPlayerView.showLoadView(false);
        }

        @Override
        public void onTrackPause(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.showPlay(true);
        }

        @Override
        public void onTrackPrepareSync(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            mIVideoPlayerView.updateMediaInfoView(itemInfo);
            mIVideoPlayerView.showPlay(false);
            mIVideoPlayerView.showPrepareLoadView(true);
            showControlView(false);
        }

        @Override
        public void onTrackPrepareComplete(MediaItem itemInfo) {

            mPlayPosTimer.stopTimer();
            int duration = mPlayerEngineImpl.getDuration();
            mIVideoPlayerView.setSeekbarMax(duration);
            mIVideoPlayerView.setTotalTime(duration);

        }

        @Override
        public void onTrackStreamError(MediaItem itemInfo) {
            log.e("onTrackStreamError");
            mPlayPosTimer.stopTimer();
            mVideoControlCenter.stop();
            mIVideoPlayerView.showPlayErrorTip();
        }

        @Override
        public void onTrackPlayComplete(MediaItem itemInfo) {
            log.e("onTrackPlayComplete");
            boolean ret = mVideoControlCenter.next();
            if (!ret) {
                mIVideoPlayerView.showPlayErrorTip();
                mIVideoPlayerView.updateMediaInfoView(itemInfo);
                mIVideoPlayerView.showPlay(false);
                mIVideoPlayerView.showPrepareLoadView(false);
                showControlView(true);
            }
        }
    }
}
