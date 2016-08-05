package com.geniusgithub.mediaplayer.player.video;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.DlnaUtils;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

public class VideoPlayerView implements VideoPlayerPresenter.IVideoPlayerView, View.OnClickListener,
                                        SeekBar.OnSeekBarChangeListener,  SurfaceHolder.Callback{

    private Context mContext;
    private IVideoPlayerPresenter mVideoPlayerPresenter;

    public View mPrepareView;
    public TextView mTVPrepareSpeed;

    public View mLoadView;
    public TextView mTVLoadSpeed;

    public View mControlView;
    public TextView mTitle;

    public ImageButton mBtnPlay;
    public ImageButton mBtnPause;
    public ImageButton mBtnPre;
    public ImageButton mBtnNext;
    public SeekBar mSeekBar;
    public TextView mTVCurTime;
    public TextView mTVTotalTime;

    private SurfaceView mSurfaceView;
    private SurfaceHolder holder = null;

    public TranslateAnimation mHideDownTransformation;
    public AlphaAnimation mAlphaHideTransformation;

    @Override
    public void bindView(Context context, View container) {
        mContext = context;
        initView(container);
    }

    @Override
    public void bindPresent(IVideoPlayerPresenter presenter) {
        mVideoPlayerPresenter = presenter;
    }

    @Override
    public void showPlay(boolean bShow) {
        if (bShow)
        {
            mBtnPlay.setVisibility(View.VISIBLE);
            mBtnPause.setVisibility(View.INVISIBLE);
        }else{
            mBtnPlay.setVisibility(View.INVISIBLE);
            mBtnPause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showPrepareLoadView(boolean bShow) {
        if (bShow){
            mPrepareView.setVisibility(View.VISIBLE);
        }else{
            mPrepareView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showControlView(boolean bShow) {
        if (bShow){
            mControlView.setVisibility(View.VISIBLE);
       //    delayToHideControlPanel();
        }else{
            mControlView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadView(boolean bShow) {
        if (bShow){
            mLoadView.setVisibility(View.VISIBLE);
        }else{
            if (mLoadView.isShown()){
                mLoadView.startAnimation(mAlphaHideTransformation);
                mLoadView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void updateMediaInfoView(MediaItem mediaInfo){
        setcurTime(0);
        setTotalTime(0);
        setSeekbarMax(100);
        setSeekbarProgress(0);
        setTitle(mediaInfo.getTitle());
    }

    @Override
    public void showPlayErrorTip() {
        Toast.makeText(mContext, R.string.toast_videoplay_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setSeekbarMax(int max) {
            mSeekBar.setMax(max);
    }

    @Override
    public void setSeekbarSecondProgress(int max) {
            mSeekBar.setSecondaryProgress(max);
    }

    @Override
    public void setTotalTime(int totalTime) {
        String timeString = DlnaUtils.formateTime(totalTime);
        mTVTotalTime.setText(timeString);
    }

    @Override
    public void setSeekbarProgress(int pos) {
        if (!isSeekbarTouch)
        {
            mSeekBar.setProgress(pos);
        }
    }

    @Override
    public boolean isLoadViewShow() {
        if (mLoadView.getVisibility() == View.VISIBLE ||
                mPrepareView.getVisibility() == View.VISIBLE){
            return true;
        }

        return false;
    }

    @Override
    public boolean isControlViewShow(){
        return mControlView.getVisibility() == View.VISIBLE ? true : false;
    }

    @Override
    public void setSpeed(float speed) {
        String showString = (int)speed + "KB/" + mContext.getResources().getString(R.string.second);
        mTVPrepareSpeed.setText(showString);
        mTVLoadSpeed.setText(showString);
    }

    @Override
    public void setcurTime(int curTime) {
        String timeString = DlnaUtils.formateTime(curTime);
        mTVCurTime.setText(timeString);
    }

    @Override
    public SurfaceHolder getSurfaceHolder(){
        return holder;
    }

    @Override
    public boolean isSurfaceCreate() {
        return isSurfaceCreate;
    }

    public void initView(View rootView){

        mPrepareView = rootView.findViewById(R.id.prepare_panel);
        mTVPrepareSpeed = (TextView) rootView.findViewById(R.id.tv_prepare_speed);

        mLoadView = rootView.findViewById(R.id.loading_panel);
        mTVLoadSpeed = (TextView) rootView.findViewById(R.id.tv_speed);

        mControlView = rootView.findViewById(R.id.control_panel);

        mBtnPlay = (ImageButton) rootView.findViewById(R.id.btn_play);
        mBtnPause = (ImageButton) rootView.findViewById(R.id.btn_pause);
        mBtnPre = (ImageButton) rootView.findViewById(R.id.btn_playpre);
        mBtnNext = (ImageButton) rootView.findViewById(R.id.btn_playnext);
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.playback_seeker);
        mTVCurTime = (TextView) rootView.findViewById(R.id.tv_curTime);
        mTVTotalTime = (TextView) rootView.findViewById(R.id.tv_totalTime);
        mTitle = (TextView) rootView.findViewById(R.id.tv_title);
        mSeekBar.setOnSeekBarChangeListener(this);

        mSurfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);




        mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f,0.0f,200.0f);
        mHideDownTransformation.setDuration(1000);

        mAlphaHideTransformation = new AlphaAnimation(1, 0);
        mAlphaHideTransformation.setDuration(1000);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_play:
                mVideoPlayerPresenter.onVideoPlay();
                break;
            case R.id.btn_pause:
                mVideoPlayerPresenter.onVideoPause();
                break;
            case R.id.btn_playpre:
                mVideoPlayerPresenter.onPlayPre();
                break;
            case R.id.btn_playnext:
                 mVideoPlayerPresenter.onPlayNext();
                break;
        }
    }

    private boolean isSeekbarTouch = false;
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mVideoPlayerPresenter.onSeekProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekbarTouch = true;
        mVideoPlayerPresenter.onSeekStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekbarTouch = false;
        mVideoPlayerPresenter.onSeekStopTrackingTouch(seekBar);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private boolean isSurfaceCreate = false;
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        isSurfaceCreate = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreate = false;
    }



    public void setTitle(String title){
        mTitle.setText(title);
    }




}
