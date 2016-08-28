package com.geniusgithub.mediaplayer.player.picture;

import android.graphics.Bitmap;

import com.geniusgithub.mediaplayer.base.BasePresenter;
import com.geniusgithub.mediaplayer.base.BaseView;

public class PicturePlayerContact {
    public interface IView extends BaseView<IPresenter> {
        public boolean isPlayShow();
        public void showPlayBtn(boolean bShow);
        public void setBitmap(Bitmap bitmap);
        public void showLoadFailTip();
        public void showParseFailTip();
        public void showProgress(boolean bShow);
        public void setScaleFlag(boolean flag);
        public void updateToolTitle(String title);
    }

    public interface IPresenter extends BasePresenter<IView> {
        public void onPicturePlay();
        public void onPicturePause();
        public void onPlayPre();
        public void onPlayNext();
    }
}
