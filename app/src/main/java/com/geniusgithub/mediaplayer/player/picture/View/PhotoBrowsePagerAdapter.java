package com.geniusgithub.mediaplayer.player.picture.View;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

class PhotoBrowsePagerAdapter extends PagerAdapter {

    public static final String TAG = PhotoBrowsePagerAdapter.class.getSimpleName();
    private Context mContext;
    private  List<MediaItem> mDataList;
    public PhotoBrowsePagerAdapter(Context context){
        mContext = context;
    }

    public void updateData( List<MediaItem> data){
        mDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        AlwaysLog.i(TAG, "instantiateItem position = " + position);
        PhotoView photoView = new PhotoView(container.getContext());
        MediaItem item = getItem(position);
        loadSource(mContext, item.getRes(), photoView);

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mDataList != null){
            count = Integer.MAX_VALUE;
        }
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public MediaItem getItem(int position){

        int offsetPos = position % mDataList.size();
        return mDataList.get(offsetPos);
    }

    public int getMiddlePos(){
        int total =  getCount() / mDataList.size() / 2;
        return total * mDataList.size();
    }

    public int getDataSize(){
        if (mDataList != null){
           return mDataList.size();
        }
        return 0;
    }

    private  void loadSource(Context context, String url, ImageView imageView){
        Glide.with(context).load(url).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

}
