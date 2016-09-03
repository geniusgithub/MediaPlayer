package com.geniusgithub.mediaplayer.player.music;

import android.graphics.drawable.Drawable;
import android.media.audiofx.Visualizer;
import android.widget.SeekBar;

import com.geniusgithub.mediaplayer.base.BasePresenter;
import com.geniusgithub.mediaplayer.base.BaseView;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

public class MusicPlayerContact {

    public interface IView extends BaseView<IPresenter> {
        public void updateToolTitle(String title,String author);
        public void showPrepareLoadView(boolean bShow);
        public void showControlView(boolean bShow);
        public void showLoadView(boolean bShow);
        public void showLRCView(boolean bShow);
        public void showPlay(boolean bShow);
        public void showPlayErrorTip();

        public void setSeekbarProgress(int pos);
        public void setSeekbarSecondProgress(int max);
        public void setSeekbarMax(int max);
        public void setTotalTime(int totalTime);
        public void setcurTime(int curTime);
        public void setSpeed(float speed);


        public void refreshLyrc(int pos);
        public void updateLyricView(MediaItem itemInfo);
        public void updateMediaInfoView(MediaItem itemInfo);
        public void updateAlbumPIC(Drawable drawable);

        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate);
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate);
    }

    public interface IPresenter extends BasePresenter<IView> {
        public void onMusicPlay();
        public void onMusicPause();
        public void onPlayPre();
        public void onPlayNext();
        public void onSeekStopTrackingTouch(SeekBar seekBar);
    }
}
