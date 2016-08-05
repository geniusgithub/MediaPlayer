package com.geniusgithub.mediaplayer.player.picture;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class PicturePlayPresenter implements IBaseFragmentPresent, IPicturePlayPresenter,
                                              DownLoadHelper.IDownLoadCallback, PictureUtil.IScalCallback{


    private static final CommonLog log = LogFactory.createLog();

    /////////////////////////////////////////////////
    public static interface IPicturePlayerView{
        public void bindView(Context context, View container);
        public void bindPresent(IPicturePlayPresenter presenter);
        public boolean isPlayShow();
        public void showPlayBtn(boolean bShow);
        public void setBitmap(Bitmap bitmap);
        public void showLoadFailTip();
        public void showParseFailTip();
        public void showProgress(boolean bShow);
        public void setScaleFlag(boolean flag);
    }


    private IPicturePlayerView mIPicturePlayerView;
    private IPicturePlayerView createPicturePlayerView(){
        return new PicturePlayerView();
    }
    /////////////////////////////////////////////////

    private Context mContext;
    private Fragment mFragmentInstance;


    public static final String PLAY_INDEX = "player_index";

    private DelCacheFileManager mDelCacheFileManager;
    private PictureControlCenter mControlCenter;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    private MediaItem mMediaInfo = new MediaItem();


    @Override
    public void bindFragment(Fragment fragment) {
        mFragmentInstance = fragment;
    }

    @Override
    public void onNewIntent(Intent intent) {
        refreshIntent(intent);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIPicturePlayerView = createPicturePlayerView();
        mIPicturePlayerView.bindPresent(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_player_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mIPicturePlayerView.bindView(mContext, view);

        initData();
        refreshIntent(mFragmentInstance.getActivity().getIntent());
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        unInitData();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
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


    @Override
    public void startDownLoad() {
            mIPicturePlayerView.showProgress(true);
    }

    @Override
    public void downLoadComplete(boolean isSuccess, String savePath) {
        onTransDelLoadResult(isSuccess, savePath);
    }

    @Override
    public void isScalBitmap(boolean flag) {
        mIPicturePlayerView.setScaleFlag(flag);
    }

    public void initData(){
        mDelCacheFileManager = new DelCacheFileManager();

        mControlCenter = new PictureControlCenter(mContext);
        mControlCenter.init();
        mControlCenter.setDownLoadCallback(this);

        mScreenWidth =  CommonUtil.getScreenWidth(mContext);
        mScreenHeight = CommonUtil.getScreenHeight(mContext);

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
        mIPicturePlayerView.showProgress(true);
    }

    public void togglePlayPause(){
        if (mIPicturePlayerView.isPlayShow()){
            mIPicturePlayerView.showPlayBtn(false);
        }else{
            mIPicturePlayerView.showPlayBtn(true);
        }
    }


    private void onTransDelLoadResult(final boolean isSuccess,final String savePath){

        final Bitmap bitmap = PictureUtil.decodeOptionsFile(savePath, mScreenWidth, mScreenHeight, this);

        mFragmentInstance.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mIPicturePlayerView.showProgress(false);

                if (!isSuccess){
                    mIPicturePlayerView.showLoadFailTip();
                    return ;
                }


                if (bitmap == null){
                    mIPicturePlayerView.showParseFailTip();
                    return ;
                }

                mIPicturePlayerView.setBitmap(bitmap);
            }
        });


    }


    private void unInitData(){
        mDelCacheFileManager.start(FileManager.getSaveRootDir());
        mControlCenter.unInit();
    }





    class DelCacheFileManager implements Runnable
    {
        private Thread mThread;
        private String mFilePath;

        public DelCacheFileManager()
        {

        }

        @Override
        public void run() {

            long time = System.currentTimeMillis();
            log.e("DelCacheFileManager run...");
            try {
                FileHelper.deleteDirectory(mFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long interval = System.currentTimeMillis() - time;
            log.e("DelCacheFileManager del over, cost time = " + interval);
        }

        public boolean start(String directory)
        {
            if (mThread != null)
            {
                if (mThread.isAlive())
                {
                    return false;
                }
            }
            mFilePath = directory;
            mThread = new Thread(this);
            mThread.start();

            return true;
        }

    }









}
