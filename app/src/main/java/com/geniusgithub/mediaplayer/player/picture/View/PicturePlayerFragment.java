package com.geniusgithub.mediaplayer.player.picture.View;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseFragment;
import com.geniusgithub.mediaplayer.base.IToolBar;
import com.geniusgithub.mediaplayer.base.ToolEntry;
import com.geniusgithub.mediaplayer.player.picture.PicturePlayerContact;
import com.geniusgithub.mediaplayer.player.picture.PicturePlayerPresenter;

import org.cybergarage.util.AlwaysLog;


public class PicturePlayerFragment extends BaseFragment{

    public static final String TAG = PicturePlayerFragment.class.getSimpleName();

    private View mRootView;
    private PicturePlayerPresenter mPicturePlayerPresenter;
    private PicturePlayerContact.IView mPicturePlayerView;

    private IToolBar mExternToolbar;
    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_player_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        onUIReady(view);
    }



    @Override
    public void onDestroy(){
        mPicturePlayerPresenter.onUiDestroy();
        super.onDestroy();

    }

    public void onNewIntent(Intent intent) {
        AlwaysLog.i(TAG, "onNewIntent");
        mPicturePlayerPresenter.onNewIntent(getActivity().getIntent());

    }


    private void onUIReady(View view){
        mRootView = view.findViewById(R.id.rl_root);

        mPicturePlayerPresenter = new PicturePlayerPresenter(getActivity());
        mPicturePlayerView = new PicturePlayerView(getActivity());
        mPicturePlayerView.setupView(mRootView);
        mPicturePlayerPresenter.bindView(mPicturePlayerView);
        mPicturePlayerPresenter.onUiCreate(getActivity());
        mPicturePlayerPresenter.onNewIntent(getActivity().getIntent());
    }


    public void updateToolTitle(String title){
        if (mExternToolbar != null){
            ToolEntry entry = new ToolEntry();
            entry.title = title;
            mExternToolbar.updateToolTitle(entry);
        }
    }


    public class PicturePlayerView implements PicturePlayerContact.IView, View.OnClickListener {

        private Context mContext;
        private PicturePlayerContact.IPresenter mPicturePlayerPresenter;

        public ImageView mImageView;
        public ImageButton mBtnPre;
        public ImageButton mBtnNext;
        public ImageButton mBtnPlay;
        public ImageButton mBtnPause;
        public View mLoadView;

        public Bitmap recycleBitmap;
        public boolean mIsScalBitmap = false;

        public PicturePlayerView(Context context){
            mContext = context;
        }

        @Override
        public void bindPresenter(PicturePlayerContact.IPresenter presenter) {
            mPicturePlayerPresenter = presenter;
        }

        @Override
        public void setupView(View rootView) {
            initView(rootView);
        }

        @Override
        public boolean isPlayShow() {
            return mBtnPlay.isShown();
        }

        @Override
        public void showPlayBtn(boolean bShow) {
            if (bShow){
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.INVISIBLE);
            }else{
                mBtnPlay.setVisibility(View.INVISIBLE);
                mBtnPause.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void setBitmap(Bitmap bitmap) {
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
        }

        @Override
        public void showLoadFailTip() {
                showToask(R.string.load_image_fail);
        }

        @Override
        public void showParseFailTip() {
            showToask(R.string.parse_image_fail);
        }

        @Override
        public void showProgress(boolean bShow) {
            if (bShow){
                mLoadView.setVisibility(View.VISIBLE);
            } else{
                mLoadView.setVisibility(View.GONE);
            }
        }

        @Override
        public void setScaleFlag(boolean flag) {
            mIsScalBitmap = flag;
        }

        @Override
        public void updateToolTitle(String title) {
            PicturePlayerFragment.this.updateToolTitle(title);
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

        private void showToask(int tip) {

            Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
        }


    }





}
