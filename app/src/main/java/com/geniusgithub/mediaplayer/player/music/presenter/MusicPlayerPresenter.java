package com.geniusgithub.mediaplayer.player.music.presenter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.browse.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.CheckDelayTimer;
import com.geniusgithub.mediaplayer.player.MusicPlayEngineImpl;
import com.geniusgithub.mediaplayer.player.PlayerEngineListener;
import com.geniusgithub.mediaplayer.player.music.LoaderHelper;
import com.geniusgithub.mediaplayer.player.music.MusicControlCenter;
import com.geniusgithub.mediaplayer.player.music.lrc.LrcDownLoadHelper;
import com.geniusgithub.mediaplayer.player.music.lrc.MusicUtils;
import com.geniusgithub.mediaplayer.player.music.ui.MusicPlayerView;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

import java.io.File;

public class MusicPlayerPresenter implements IBaseFragmentPresent, IMusicPlayerPresenter,  LrcDownLoadHelper.ILRCDownLoadCallback{




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
        public void showPlayErrorTip();
        public void setSeekbarMax(int max);
        public void setTotalTime(int totalTime);
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
    }

    @Override
    public void onResume() {

    }



    @Override
    public void onDestroy() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public void onPlay() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onPlayPre() {

    }

    @Override
    public void onPlayNext() {

    }


    @Override
    public void lrcDownLoadComplete(boolean isSuccess, String song, String artist) {

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
            boolean need = checkNeedDownLyric(itemInfo);
            log.e("checkNeedDownLyric need = " + need);
            if (need) {
                mLrcDownLoadHelper.syncDownLoadLRC(itemInfo.title, itemInfo.artist, MusicPlayerPresenter.this);
            }
            mIMusicPlayerView.updateLyricView(itemInfo);
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
