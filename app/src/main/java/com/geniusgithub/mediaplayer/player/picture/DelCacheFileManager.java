package com.geniusgithub.mediaplayer.player.picture;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.common.util.FileManager;
import com.geniusgithub.mediaplayer.util.FileHelper;

public class DelCacheFileManager implements Runnable
{
    private final static String TAG = DelCacheFileManager.class.getSimpleName();
    private Thread mThread;
    private String mFilePath;
    private boolean mOnlyDeleteContent;

    public DelCacheFileManager()
    {

    }

    public void clearThumbnailCache(){
       start(FileManager.getThumbnailCacheRootDir(), true);
    }

    public void clearBrowseCache(){
        start(FileManager.getBrowseCacheRootDir(), false);
    }

    @Override
    public void run() {

        long time = System.currentTimeMillis();
        AlwaysLog.i(TAG, "DelCacheFileManager run...");
        try {
            FileHelper.deleteDirectory(mFilePath, mOnlyDeleteContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long interval = System.currentTimeMillis() - time;
        AlwaysLog.i(TAG, "DelCacheFileManager del over, cost time = " + interval);
    }

    public boolean start(String directory, boolean onlyDeleteContent)
    {
        if (mThread != null)
        {
            if (mThread.isAlive())
            {
                return false;
            }
        }
        mFilePath = directory;
        mOnlyDeleteContent = onlyDeleteContent;
        mThread = new Thread(this);
        mThread.start();

        return true;
    }

}