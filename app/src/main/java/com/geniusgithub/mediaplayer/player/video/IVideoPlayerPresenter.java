package com.geniusgithub.mediaplayer.player.video;

import android.widget.SeekBar;

public interface IVideoPlayerPresenter {
    public void onVideoPlay();
    public void onVideoPause();
    public void onPlayPre();
    public void onPlayNext();
    public void onSeekProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    public void onSeekStartTrackingTouch(SeekBar seekBar);
    public void onSeekStopTrackingTouch(SeekBar seekBar);
}

