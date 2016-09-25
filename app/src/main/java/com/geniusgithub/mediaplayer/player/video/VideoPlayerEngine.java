package com.geniusgithub.mediaplayer.player.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.player.base.Player;
import com.geniusgithub.mediaplayer.util.CommonUtil;

public class VideoPlayerEngine extends Player implements MediaPlayer.OnInfoListener {

    private final static String TAG = VideoPlayerEngine.class.getSimpleName();

    private Context mContext;
    private SurfaceHolder mHolder = null;

    public VideoPlayerEngine(Context context){
        mContext = context;
    }

    @Override
    protected void prepareComplete(MediaPlayer mp) {
        if (mHolder != null){
            mp.setDisplay(mHolder);
            CommonUtil.ViewSize viewSize = CommonUtil.getFitSize(mContext, mp);
            mHolder.setFixedSize(viewSize.width, viewSize.height);
        }
        mp.setOnInfoListener(this);
    }

    public void setHolder(SurfaceHolder holder){
        mHolder = holder;
        getMediaPlayer().setDisplay(holder);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        AlwaysLog.e(TAG, "onInfo --> what = " + what);

        return false;
    }
}
