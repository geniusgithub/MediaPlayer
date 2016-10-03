package com.geniusgithub.mediaplayer.player.base;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.dlna.model.MediaEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public  abstract  class Player implements IPlayback,MediaPlayer.OnPreparedListener,
                                MediaPlayer.OnCompletionListener,
                                 MediaPlayer.OnBufferingUpdateListener,
                                    MediaPlayer.OnErrorListener{

    protected static final String TAG = Player.class.getSimpleName();

    protected MediaPlayer mPlayer;
    protected PlayList mPlayList;
    protected int mEnumPlayState;
    protected  boolean isErrorHappen = false;
    protected int mInitProgress = 0;

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private MediaPlayer.OnErrorListener mErrorListener;

    protected List<PlayStateCallback> mCallbacks = new ArrayList<>(2);

    public Player(){
        mPlayer = new MediaPlayer();
        mPlayList = new PlayList();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnErrorListener(this);
        mEnumPlayState = EnumPlayState.MPS_STOP;
    }

    public void setOnBuffUpdateListener(MediaPlayer.OnBufferingUpdateListener listener){
        mBufferingUpdateListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener){
        mErrorListener = listener;
    }

    protected  MediaPlayer getMediaPlayer(){
        return mPlayer;
    }

    @Override
    public void setPlayList(PlayList list) {
        if (list == null){
            list = new PlayList();
        }
        mPlayList = list;
    }

    @Override
    public void setPlayMode(PlayMode mode) {
        if (mPlayList != null){
            mPlayList.setPlayMode(mode);
        }
    }

    @Override
    public void setInitPlayProgress(int progress) {
        mInitProgress = progress;
    }

    @Override
    public boolean play() {
        AlwaysLog.i(TAG, "play mEnumPlayState = " + mEnumPlayState);
        switch (mEnumPlayState) {
            case EnumPlayState.MPS_PAUSE:
                mPlayer.start();
                mEnumPlayState = EnumPlayState.MPS_PLAYING;
                notifyPlayState(mEnumPlayState);
                return true;
            default:
                return prepareSelf();
        }
    }

    @Override
    public boolean play(PlayList list) {
        if (list == null) return false;
        AlwaysLog.i(TAG, "play list size = " + list.getMediaCount());
        setPlayList(list);
        mEnumPlayState = EnumPlayState.MPS_STOP;
        return play();

    }

    @Override
    public boolean play(PlayList list, int startIndex) {

        if (list == null || startIndex < 0 || startIndex >= list.getMediaCount()) return false;
        list.setPlayingIndex(startIndex);
        setPlayList(list);
        mEnumPlayState = EnumPlayState.MPS_STOP;
        return play();
    }

    @Override
    public boolean playLast() {
        AlwaysLog.i(TAG, "playLast");
        boolean hasLast = mPlayList.hasLast();
        if (hasLast) {
            MediaEntry last = mPlayList.last();
            mEnumPlayState = EnumPlayState.MPS_STOP;
            play();
            return true;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        AlwaysLog.i(TAG, "playNext");
        boolean hasLast = mPlayList.hasNext(false);
        if (hasLast) {
            MediaEntry last = mPlayList.next();
            mEnumPlayState = EnumPlayState.MPS_STOP;
            play();
            return true;
        }
        return false;
    }

    @Override
    public boolean pause() {
        AlwaysLog.i(TAG, "pause mEnumPlayState = " + mEnumPlayState);
        switch (mEnumPlayState) {
            case EnumPlayState.MPS_PLAYING:
                mPlayer.pause();
                mEnumPlayState = EnumPlayState.MPS_PAUSE;
                notifyPlayState(mEnumPlayState);
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean isPause(){
       return mEnumPlayState == EnumPlayState.MPS_PAUSE;
    }

    @Override
    public void stop() {
        AlwaysLog.i(TAG, "stop");
        mPlayer.stop();
        mEnumPlayState = EnumPlayState.MPS_STOP;
        notifyPlayState(mEnumPlayState);
    }

    @Override
    public int getProgress() {
        if (mEnumPlayState == EnumPlayState.MPS_PLAYING || mEnumPlayState == EnumPlayState.MPS_PAUSE || mEnumPlayState == EnumPlayState.MPS_STOP)
        {
            return mPlayer.getCurrentPosition();
        }

        return 0;
    }


    @Override
    public int getDuration() {
        switch(mEnumPlayState){
            case EnumPlayState.MPS_PLAYING:
            case EnumPlayState.MPS_PAUSE:
            case EnumPlayState.MPS_PARECOMPLETE:
                return mPlayer.getDuration();
        }

        return 0;
    }

    @Override
    public boolean seekTo(int progress) {
        AlwaysLog.i(TAG, "seekTo = " + progress);
        switch (mEnumPlayState) {
            case EnumPlayState.MPS_PLAYING:
            case EnumPlayState.MPS_PAUSE:
                mPlayer.seekTo(reviceSeekValue(progress));
                int time = reviceSeekValue(progress);
                mPlayer.seekTo(time);
                break;
            default:
                break;
        }


        return false;
    }

    @Override
    public void releasePlayer() {
        mPlayList = null;
        mPlayer.reset();
        mPlayer.release();
        isErrorHappen = false;
        mPlayer = null;
        mInitProgress = 0;
    }

    @Override
    public void registerCallback(PlayStateCallback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(PlayStateCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        AlwaysLog.i(TAG, "onPrepared");
        prepareComplete(mp);
        mEnumPlayState = EnumPlayState.MPS_PARECOMPLETE;
        notifyPlayState(mEnumPlayState);
        mPlayer.start();
        if (mInitProgress != 0){
            mPlayer.seekTo(mInitProgress);
            mInitProgress = 0;
        }
        mEnumPlayState = EnumPlayState.MPS_PLAYING;
        notifyPlayState(mEnumPlayState);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        AlwaysLog.i(TAG, "onCompletion isErrorHappen =s" + isErrorHappen);
        if (isErrorHappen){
            return ;
        }

        MediaEntry next = null;
        mEnumPlayState = EnumPlayState.MPS_STOP;
        if (mPlayList.getPlayMode() == PlayMode.SINGLE) {
            next = mPlayList.getCurrentMedia();
            play();
        } else {
            boolean hasLast = mPlayList.hasNext(true);
            if (hasLast) {
                MediaEntry last = mPlayList.next();
                play();
            }
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mBufferingUpdateListener != null){
            mBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        isErrorHappen = true;
       if (mErrorListener != null){
           return mErrorListener.onError(mp, what, extra);
       }

        return false;
    }

    protected abstract void prepareComplete(MediaPlayer mp);

    protected boolean prepareSelf() {
        AlwaysLog.i(TAG, "prepareSelf");
        mPlayer.reset();
        isErrorHappen = false;
        MediaEntry entry = mPlayList.getCurrentMedia();
        if (entry == null){
            mEnumPlayState = EnumPlayState.MPS_STOP;
            notifyPlayState(mEnumPlayState);
            return false;
        }

        try {
            mPlayer.setDataSource(entry.getDataSource());
        } catch (IOException e) {
            e.printStackTrace();
            mEnumPlayState = EnumPlayState.MPS_INVALID;
            notifyPlayState(mEnumPlayState);
            return false;
        }

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.prepareAsync();
        AlwaysLog.i(TAG, "prepareAsync path = " + entry.getDataSource());
        mEnumPlayState = EnumPlayState.MPS_PARESYNC;
        notifyPlayState(mEnumPlayState);

        return true;
    }




    private int reviceSeekValue(int value)
    {
        if (value < 0)
        {
            value = 0;
        }

        if (value > mPlayer.getDuration())
        {
            value = mPlayer.getDuration();
        }

        return value;
    }

    protected void notifyPlayState(int playState)
    {

        if (mCallbacks.isEmpty()){
            return ;
        }

        switch(playState){
            case EnumPlayState.MPS_INVALID:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackStreamError();
                }
                break;
            case EnumPlayState.MPS_STOP:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackStop();
                }
                break;
            case EnumPlayState.MPS_PLAYING:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackPlay();
                }
                break;
            case EnumPlayState.MPS_PAUSE:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackPause();
                }
                break;
            case EnumPlayState.MPS_PARESYNC:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackPrepareSync();
                }
                break;
            case EnumPlayState.MPS_PARECOMPLETE:
                for (PlayStateCallback callback : mCallbacks) {
                    callback.onTrackPrepareComplete();
                }
                break;
        }
    }



}
