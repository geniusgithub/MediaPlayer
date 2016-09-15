package com.geniusgithub.mediaplayer.component;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

public class CacheManager {

    private static final CommonLog log = LogFactory.createLog();
    private static CacheManager mInstance;

    private Context mContext;

    private static final String THNUMNAIL_CACHE = "thnumnail_cache";


    public static CacheManager getInstance(){
        return mInstance;
    }

    public static CacheManager newInstance(Context context){
        if (mInstance == null){
            mInstance = new CacheManager(context);
        }

        return mInstance;
    }

    private CacheManager(Context context){
        mContext = context;
    }


    public void initConfigure(GlideBuilder build){
        log.i("CacheManager initConfigure");
        build.setDiskCache(new ExternalCacheDiskCacheFactory(mContext, THNUMNAIL_CACHE, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
    }

    private static boolean isclearThumnailCacheStart = false;
    public void clearCache(){
        clearMemoryCache();

        Thread mThead = new Thread(new Runnable() {
            @Override
            public void run() {
                log.i("clearCache thread start...");
                if (isclearThumnailCacheStart){
                    log.i("clearCache  start = true, so return now");
                    return ;
                }
                synchronized (CacheManager.this){
                    isclearThumnailCacheStart = true;
                    long time1 = System.currentTimeMillis();
                    Glide.get(mContext).clearDiskCache();
                    long time2 = System.currentTimeMillis();
                    log.i("clearDiskCache cost time:" + (time2 - time1));
                    isclearThumnailCacheStart = false;
                }
            }
        });
        mThead.start();
    }

    public void clearMemoryCache(){
        long time1 = System.currentTimeMillis();
        Glide.get(mContext).clearMemory();
        long time2 = System.currentTimeMillis();
        log.i("clearMemory cost time:" + (time2 - time1));
    }

}
