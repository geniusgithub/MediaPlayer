package com.geniusgithub.mediaplayer.player.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.picture.model.DownLoadHelper;
import com.geniusgithub.mediaplayer.player.picture.model.PictureControlCenter;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class PicturePlayerPresenter implements PicturePlayerContact.IPresenter,  DownLoadHelper.IDownLoadCallback, PictureUtil.IScalCallback {

    private static final CommonLog log = LogFactory.createLog();
    public static final String PLAY_INDEX = "player_index";


    private Context mContext;
    private PicturePlayerContact.IView mView;
    private DelCacheFileManager mDelCacheFileManager;
    private PictureControlCenter mControlCenter;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    private MediaItem mMediaInfo = new MediaItem();
    private Handler mHandler;

    public PicturePlayerPresenter(Context contect){
        mContext = contect;
    }


    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(PicturePlayerContact.IView view) {
        mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }


    @Override
    public void onPicturePlay() {
        mControlCenter.startAutoPlay(true);
        togglePlayPause();
    }

    @Override
    public void onPicturePause() {
        mControlCenter.startAutoPlay(false);
        togglePlayPause();
    }

    @Override
    public void onPlayPre() {
        mControlCenter.prev();
    }

    @Override
    public void onPlayNext() {
        mControlCenter.next();
    }
    ///////////////////////////////////////     presenter callback end


    @Override
    public void startDownLoad(String title) {
        mView.showProgress(true);
        mView.updateToolTitle(title);
    }

    @Override
    public void downLoadComplete(boolean isSuccess, String savePath) {
        onTransDelLoadResult(isSuccess, savePath);
    }

    @Override
    public void isScalBitmap(boolean flag) {
        mView.setScaleFlag(flag);
    }


    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context) {
        initData();
    }


    public void onNewIntent(Intent intent) {
        refreshIntent(intent);
    }


    public void onUiDestroy() {
        unInitData();
    }
    ///////////////////////////////////////     lifecycle or ui operator end


    public void initData(){
        mDelCacheFileManager = new DelCacheFileManager();

        mControlCenter = new PictureControlCenter(mContext);
        mControlCenter.init();
        mControlCenter.setDownLoadCallback(this);

        mScreenWidth =  CommonUtil.getScreenWidth(mContext);
        mScreenHeight = CommonUtil.getScreenHeight(mContext);

        mHandler = new Handler();

    }

    private void refreshIntent(Intent intent){
        log.e("refreshIntent");
        int curIndex = 0;
        if (intent != null){
            curIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }

        mControlCenter.updateMediaInfo(curIndex, MediaManager.getInstance().getPictureList());

        mControlCenter.play(curIndex);
        mView.showProgress(true);
    }

    public void togglePlayPause(){
        if (mView.isPlayShow()){
            mView.showPlayBtn(false);
        }else{
            mView.showPlayBtn(true);
        }
    }

    private void onTransDelLoadResult(final boolean isSuccess,final String savePath){

        final Bitmap bitmap = PictureUtil.decodeOptionsFile(savePath, mScreenWidth, mScreenHeight, this);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.showProgress(false);

                if (!isSuccess){
                    mView.showLoadFailTip();
                    return ;
                }


                if (bitmap == null){
                    mView.showParseFailTip();
                    return ;
                }

                mView.setBitmap(bitmap);
            }
        });
    }

    private void unInitData(){
        mDelCacheFileManager.clearBrowseCache();
        mControlCenter.unInit();
    }


}
