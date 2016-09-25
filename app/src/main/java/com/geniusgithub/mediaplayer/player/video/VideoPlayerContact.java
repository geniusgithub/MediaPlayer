package com.geniusgithub.mediaplayer.player.video;

import android.view.SurfaceHolder;
import android.widget.SeekBar;

import com.geniusgithub.mediaplayer.base.BasePresenter;
import com.geniusgithub.mediaplayer.base.BaseView;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

public class VideoPlayerContact {


    public interface IView extends BaseView<IPresenter> {
        public void showPlay(boolean bShow);
        public void showPrepareLoadView(boolean bShow);
        public void showControlView(boolean bShow);
        public void showLoadView(boolean bShow);
        public void showPlayErrorTip();

        public void setSeekbarMax(int max);
        public void setSeekbarSecondProgress(int max);
        public void setSeekbarProgress(int pos);
        public void setSpeed(float speed);
        public void setcurTime(int curTime);
        public void setTotalTime(int totalTime);

        public boolean isControlViewShow();
        public SurfaceHolder getSurfaceHolder();
        public boolean isSurfaceCreate();
        public void updateMediaInfoView(MediaItem mediaInfo);
    }

    public interface IPresenter extends BasePresenter<IView> {
        public void onVideoRePlay(SurfaceHolder holder);
        public void onVideoPlay();
        public void onVideoPause();
        public void onVideoStop();
        public void onPlayPre();
        public void onPlayNext();
        public void onSeekStopTrackingTouch(SeekBar seekBar);
    }
}
