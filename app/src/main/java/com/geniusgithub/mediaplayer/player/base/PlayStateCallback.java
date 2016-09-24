package com.geniusgithub.mediaplayer.player.base;

public interface PlayStateCallback {
    public void onTrackPlay();
    public void onTrackStop();
    public void onTrackPause();
    public void onTrackPrepareSync();
    public void onTrackPrepareComplete();
    public void onTrackStreamError();
//    public void onTrackPlayComplete();
}
