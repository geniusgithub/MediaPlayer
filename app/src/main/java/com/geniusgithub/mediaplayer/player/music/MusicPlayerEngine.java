package com.geniusgithub.mediaplayer.player.music;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import com.geniusgithub.mediaplayer.player.base.Player;

public class MusicPlayerEngine extends Player {


    private Visualizer mVisualizer;
    private Visualizer.OnDataCaptureListener mDataCaptureListener;

    public MusicPlayerEngine() {
        super();

    }

    @Override
    protected void prepareComplete(MediaPlayer mp) {
        reInitVisualizer(mp.getAudioSessionId());
        enableVisualizer(true);
    }

    public void setDataCaptureListener(Visualizer.OnDataCaptureListener listener){
        mDataCaptureListener = listener;
    }


    public void enableVisualizer(boolean flag){
        if (mVisualizer != null){
            mVisualizer.setEnabled(flag);
        }
    }


    public void releaseVisualizer(){
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    public boolean reInitVisualizer(int sID){
        releaseVisualizer();

        final int maxCR = Visualizer.getMaxCaptureRate();
        mVisualizer = new Visualizer(sID);
        mVisualizer.setCaptureSize(256);
        if (mDataCaptureListener != null){
            mVisualizer.setDataCaptureListener(mDataCaptureListener, maxCR/2, false, true);
        }

        return true;
    }

    @Override
    public boolean play() {
        boolean ret = super.play();
        enableVisualizer(true);
        return ret;
    }

    @Override
    public boolean pause() {
        boolean ret = super.pause();
        enableVisualizer(false);
        return ret;
    }

    @Override
    public void stop() {
        super.stop();
        enableVisualizer(false);
    }

    @Override
    public void releasePlayer() {
        super.releasePlayer();
        releaseVisualizer();
    }
}
