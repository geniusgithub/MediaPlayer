package com.geniusgithub.mediaplayer.player.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;

public class PicturePlayerView implements PicturePlayPresenter.IPicturePlayerView, View.OnClickListener {

    private Context mContext;
    private IPicturePlayPresenter mPicturePlayerPresenter;

    public ImageView mImageView;
    public ImageButton mBtnPre;
    public ImageButton mBtnNext;
    public ImageButton mBtnPlay;
    public ImageButton mBtnPause;
    public View mLoadView;

    public Bitmap recycleBitmap;
    public boolean mIsScalBitmap = false;

    @Override
    public void bindView(Context context, View container) {
        mContext = context;
        initView(container);
    }

    private void initView(View rootView) {
        mImageView = (ImageView) rootView.findViewById(R.id.imageview);
        mLoadView = rootView.findViewById(R.id.show_load_progress);


        mBtnPre = (ImageButton) rootView.findViewById(R.id.btn_playpre);
        mBtnNext = (ImageButton) rootView.findViewById(R.id.btn_playnext);
        mBtnPlay = (ImageButton) rootView.findViewById(R.id.btn_play);
        mBtnPause = (ImageButton) rootView.findViewById(R.id.btn_pause);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
    }

    @Override
    public void bindPresent(IPicturePlayPresenter presenter) {
        mPicturePlayerPresenter = presenter;
    }

    @Override
    public boolean isPlayShow(){
        return mBtnPlay.isShown();
    }

    @Override
    public void showPlayBtn(boolean bShow){
        if (bShow){
            mBtnPlay.setVisibility(View.VISIBLE);
            mBtnPause.setVisibility(View.INVISIBLE);
        }else{
            mBtnPlay.setVisibility(View.INVISIBLE);
            mBtnPause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setBitmap(Bitmap bitmap){
        if (recycleBitmap != null && !recycleBitmap.isRecycled()) {
            mImageView.setImageBitmap(null);
            recycleBitmap.recycle();
            recycleBitmap = null;
        }

        if (mIsScalBitmap) {
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        recycleBitmap = bitmap;
        mImageView.setImageBitmap(recycleBitmap);

        //		log.e("	mImageView.setImageBitmap over...");
    }

    @Override
    public void showLoadFailTip(){
        showToask(R.string.load_image_fail);
    }

    @Override
    public void showParseFailTip(){
        showToask(R.string.parse_image_fail);
    }

    @Override
    public void showProgress(boolean bShow)
    {
        if (bShow){
            mLoadView.setVisibility(View.VISIBLE);
        } else{
            mLoadView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setScaleFlag(boolean flag){
        mIsScalBitmap = flag;
    }

    private void showToask(int tip) {
        Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_playpre:
                mPicturePlayerPresenter.onPlayPre();
                break;
            case R.id.btn_playnext:
                mPicturePlayerPresenter.onPlayNext();
                break;
            case R.id.btn_play:
                mPicturePlayerPresenter.onPicturePlay();
                break;
            case R.id.btn_pause:
                mPicturePlayerPresenter.onPicturePause();
                break;
            default:
                break;
        }
    }
}
