package com.geniusgithub.mediaplayer.player.music;

import android.widget.SeekBar;

public interface IMusicPlayerPresenter {
    public void onMusicPlay();
    public void onMusicPause();
    public void onPlayPre();
    public void onPlayNext();
    public void onSeekProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    public void onSeekStartTrackingTouch(SeekBar seekBar);
    public void onSeekStopTrackingTouch(SeekBar seekBar);
}

