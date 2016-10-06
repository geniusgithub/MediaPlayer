package com.geniusgithub.mediaplayer.player.music;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.component.MediaItemFactory;
import com.geniusgithub.mediaplayer.component.MediaManager;
import com.geniusgithub.mediaplayer.dlna.control.base.TransportState;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaEntry;
import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.control.rendercontrol.RemotePlayer;
import com.geniusgithub.mediaplayer.player.AbstractTimer;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.base.MediaItemPlayList;
import com.geniusgithub.mediaplayer.player.base.PlayMode;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class RemoteMusicPlayerPresenter extends MusicPlayerPresenter implements  RemotePlayer.Callback{

    private final static String TAG = RemoteMusicPlayerPresenter.class.getSimpleName();

    private Context mContext;

    private final static int REFRESH_CURPOS = 0x0001;
    private final static int GET_TRANSPORTSTATE = 0x0002;

    private Device mCurDevice;
    private RemotePlayer mRemotePlayer;


    private MediaItemPlayList mPLayList;
    private PlayMode mCurPlayMode;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    private AbstractTimer mPlayPosTimer;
    private AbstractTimer mGetTransportTimer;

    private boolean isDestroy = false;



    public RemoteMusicPlayerPresenter(){
        mContext = AllShareApplication.getInstance();
    }

    public void setDMRDevice(Device device){
        mCurDevice = device;
    }

    ///////////////////////////////////////     presenter callback begin
    @Override
    public void onMusicPlay() {
        AlwaysLog.i(TAG, "onMusicPlay");
        mRemotePlayer.play(mCurDevice);
    }

    @Override
    public void onMusicPause() {
        AlwaysLog.i(TAG, "onMusicPause");
        mRemotePlayer.pause(mCurDevice);
    }

    @Override
    public void onPlayPre() {
        AlwaysLog.i(TAG, "onPlayPre");
        boolean hasLast = mPLayList.hasLast();
        if (hasLast) {
            MediaEntry last = mPLayList.last();
            mMediaInfo = mPLayList.getCurrentMedia();
            replay(mMediaInfo);
        }

    }

    @Override
    public void onPlayNext() {
        AlwaysLog.i(TAG, "onPlayNext");

        boolean hasLast = mPLayList.hasNext(false);
        if (hasLast) {
            MediaEntry last = mPLayList.next();
            mMediaInfo = mPLayList.getCurrentMedia();
            replay(mMediaInfo);
        }
    }

    @Override
    public void onToggleMode() {
        AlwaysLog.i(TAG, "onToggleMode");

        mCurPlayMode = PlayMode.switchNextMode(mCurPlayMode);
        mPLayList.setPlayMode(mCurPlayMode);
        mView.updatePlayMode(mCurPlayMode);

    }


    @Override
    public void onSeekStopTrackingTouch(SeekBar seekBar) {
        int pos = seekBar.getProgress();
        AlwaysLog.i(TAG, "onSeekStopTrackingTouch pos = " + pos);

        mRemotePlayer.seekTo(mCurDevice, pos);
        mView.setSeekbarProgress(pos);
    }

    @Override
    public void onPlayItemClick(MediaItem data, int position) {
        AlwaysLog.i(TAG, "onPlayItemClick  position = " + position);
        mPLayList.setPlayingIndex(position);
        mMediaInfo = mPLayList.getCurrentMedia();
        replay(mMediaInfo);
    }
    ///////////////////////////////////////     presenter callback end




    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context){
        mContext = context;

        initData();
    }

    public void onUiDestroy(){
        isDestroy = true;
        mPlayPosTimer.stopTimer();
        mGetTransportTimer.stopTimer();;
        mRemotePlayer.stop(mCurDevice);
    }

    public void onNewIntent(Intent intent) {
        refreshIntent(intent);

    }
    ///////////////////////////////////////     lifecycle or ui operator end



    ///////////////////////////////////////     remote callback begin
    @Override
    public void onRePlayComplete(boolean isSuccess) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onRePlayComplete false!!!");
        }


        if (isSuccess){
            mRemotePlayer.getDuration(mCurDevice);
        }

    }



    @Override
    public void onPlayComplete(boolean isSuccess) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onPlayComplete false!!!");
        }
    }

    @Override
    public void onPauseComplete(boolean isSuccess) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onPauseComplete false!!!");
        }
    }

    @Override
    public void onStopComplete(boolean isSuccess) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onStopComplete false!!!");
        }
    }

    @Override
    public void onGetProgressComplete(boolean isSuccess, int progress) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onGetProgressComplete false!!!");
        }else{

        }
    }

    @Override
    public void onGetDurationComplete(boolean isSuccess, int duration) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onGetDurationComplete false!!!");
        }else{
            mView.setSeekbarMax(duration);
            mView.setTotalTime(duration);
        }


    }

    @Override
    public void onGetTransportComplete(boolean isSuccess, int state) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onGetTransportComplete false!!!");
        }else{
            doTransportEvent(state);
        }
    }

    @Override
    public void onSeekComplete(boolean isSuccess) {
        if (!isSuccess){
            AlwaysLog.e(TAG, "onSeekComplete false!!!");
        }
    }
    ///////////////////////////////////////     remote callback end


    public void initData(){
        mPlayPosTimer = new SingleSecondTimer(mContext);
        mGetTransportTimer = new SingleSecondTimer(mContext);
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what)
                {
                    case REFRESH_CURPOS:
                        mRemotePlayer.getProgress(mCurDevice);
                        break;
                    case GET_TRANSPORTSTATE:
                        mRemotePlayer.getTransportState(mCurDevice);
                        break;
                }
            }

        };


        mPlayPosTimer.setHandler(mHandler, REFRESH_CURPOS);
        mGetTransportTimer.setHandler(mHandler, GET_TRANSPORTSTATE);

        mPLayList = new MediaItemPlayList();
        mCurPlayMode = PlayMode.getDefault();
        mPLayList.setPlayMode(mCurPlayMode);
        mRemotePlayer = new RemotePlayer();

        mView.updatePlayMode(mCurPlayMode);
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
        replay(mMediaInfo);
        mView.updatePlayList(MediaManager.getInstance().getMusicList());
        mView.showPrepareLoadView(false);
    }


    private void replay(MediaItem mediaItem){
        AlwaysLog.i(TAG, "replay url =  " + mediaItem.getRes());
        mRemotePlayer.play(mCurDevice, mediaItem.getRes());
        mGetTransportTimer.startTimer();
        mView.updateMediaInfoView(mediaItem, mPLayList.getPlayingIndex());
    }


    private void doTransportEvent(int state){
        AlwaysLog.d(TAG, "doTransportEvent state = " + state);

        switch(state){
            case TransportState.UNKNOW:
                break;
            case TransportState.TRANSITIONING:
                mPlayPosTimer.stopTimer();
                mView.showPlay(true);
                break;
            case TransportState.PLAYING:
                mPlayPosTimer.startTimer();
                mView.showPlay(false);
                break;
            case TransportState.PAUSED_PLAYBACK:
                mPlayPosTimer.stopTimer();
                mView.showPlay(true);
                break;
            case TransportState.STOPPED:
                mPlayPosTimer.stopTimer();
                mView.showPlay(true);
                break;
        }
    }

}
