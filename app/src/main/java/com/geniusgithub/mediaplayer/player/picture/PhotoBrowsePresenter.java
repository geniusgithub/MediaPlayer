package com.geniusgithub.mediaplayer.player.picture;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.component.CacheManager;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;
import com.geniusgithub.mediaplayer.dlna.model.MediaItemFactory;
import com.geniusgithub.mediaplayer.dlna.model.MediaManager;
import com.geniusgithub.mediaplayer.player.SingleSecondTimer;
import com.geniusgithub.mediaplayer.player.common.AbstractTimer;

import java.util.ArrayList;
import java.util.List;

public class PhotoBrowsePresenter implements PhotoBrowseContact.IPresenter {

    public static final String TAG = PhotoBrowsePresenter.class.getSimpleName();

    private Context mContext;
    private PhotoBrowseContact.IView mView;


    public static final String PLAY_INDEX = "player_index";
    private final static int AUTO_PLAY_INTERVAL = 3000;
    private final static int PLAY_NEXT = 0x0001;
    private Handler mHandler;
    private boolean isPlayAuto = false;
    private AbstractTimer mAutoPlayerTimer;


    private int mCurIndex = -1;
    private MediaItem mMediaInfo = new MediaItem();
    private List<MediaItem> mCurItemsList;

    public PhotoBrowsePresenter(Context contect){
        mContext = contect;
    }


    ///////////////////////////////////////     presenter callback begin
    @Override
    public void bindView(PhotoBrowseContact.IView view) {
        mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }
    ///////////////////////////////////////     presenter callback end


    private static final String[] URLS = {
            "http://ww3.sinaimg.cn/thumbnail/6cc0b934jw1e0bnwu709kj.jpg",
            "http://ww2.sinaimg.cn/thumbnail/519e330bjw1e0bw1icjokj.jpg",
            "http://ww3.sinaimg.cn/thumbnail/a163c684jw1e0c0gbftdpj.jpg",
            "http://ww4.sinaimg.cn/thumbnail/a163c684jw1e0c0g9tgmhj.jpg",
            "http://ww3.sinaimg.cn/thumbnail/a163c684jw1e0c0g9fxcqj.jpg",
            "http://ww4.sinaimg.cn/thumbnail/a81bce36jw1e0brpwol6yj.jpg",
            "http://ww4.sinaimg.cn/thumbnail/a81bce36jw1e0brqequ1oj.jpg",
            "http://ww1.sinaimg.cn/thumbnail/a81bce36jw1e0brpwnn0bj.jpg",
            "http://ww2.sinaimg.cn/thumbnail/721f7167jw1e0aeqrqtlfj.jpg",
            "http://ww1.sinaimg.cn/thumbnail/60dd4473jw1e0aj52otyaj.jpg"};

    private void unitTest(){
        mCurItemsList = new ArrayList<MediaItem>();
        for (String value:URLS) {
            MediaItem item = new MediaItem();
            item.setRes(value);
            item.setTitle(value);
            mCurItemsList.add(item);
        }
        mCurIndex = 3;
    }

    ///////////////////////////////////////     lifecycle or ui operator begin
    public void onUiCreate(Context context, Intent intent) {
        mContext = context;
        if (intent != null){
            mCurIndex = intent.getIntExtra(PLAY_INDEX, 0);
            mMediaInfo = MediaItemFactory.getItemFromIntent(intent);
        }


        mCurItemsList = MediaManager.getInstance().getPictureList();
        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case PLAY_NEXT:
                    {
                        AlwaysLog.i(TAG, "PLAY_NEXT ... ");
                        mView.onPlayNext();
                    }
                    break;
                }
            }

        };

      //  unitTest();
        mAutoPlayerTimer = new SingleSecondTimer(context);
        mAutoPlayerTimer.setTimeInterval(AUTO_PLAY_INTERVAL);
        mAutoPlayerTimer.setHandler(mHandler, PLAY_NEXT);

        mView.initBrowseData(mCurItemsList, mCurIndex);

    }



    public void onUiDestroy() {
        mAutoPlayerTimer.stopTimer();

        CacheManager.getInstance().clearDiskCache();
    }
    ///////////////////////////////////////     lifecycle or ui operator end

    public void startAutoPlay(boolean flag){
        if (flag == isPlayAuto){
            AlwaysLog.e(TAG, "isPlayAuto equal flag = " + flag);
            return ;
        }
        AlwaysLog.i(TAG, "startAutoPlay flag = " + flag);
        if (flag){
            mAutoPlayerTimer.startTimer();
            isPlayAuto = true;
        }else{
            mAutoPlayerTimer.stopTimer();
            isPlayAuto = false;
        }
    }



    public boolean isPlayAuto(){
        return isPlayAuto;
    }

}
