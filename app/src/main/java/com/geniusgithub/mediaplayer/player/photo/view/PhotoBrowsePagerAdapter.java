package com.geniusgithub.mediaplayer.player.photo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

class PhotoBrowsePagerAdapter extends PagerAdapter {

    public static final String TAG = PhotoBrowsePagerAdapter.class.getSimpleName();
    private Context mContext;
    private  List<MediaItem> mDataList;
    private LayoutInflater mLayoutInflate;


    public PhotoBrowsePagerAdapter(Context context){
        mContext = context;
        mLayoutInflate = LayoutInflater.from(context);
    }



    public void updateData( List<MediaItem> data){
        mDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        AlwaysLog.i(TAG, "instantiateItem position = " + position);

        View view = mLayoutInflate.inflate(R.layout.photo_browse_layout, null);
        bindView(view, position);

        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }

    private void bindView(View view, int position){
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        ImageView failView = (ImageView) view.findViewById(R.id.iv_fail);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mPhoneView = photoView;
        viewHolder.mProgress = progressBar;
        viewHolder.mLoadFialView = failView;

        MediaItem item = getItem(position);

        loadSource(mContext, item, viewHolder);

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

    private  void loadSource(Context context, MediaItem item, ViewHolder viewHolder){
        GlideImageView object = new GlideImageView(viewHolder.mPhoneView);
        object.bindViewHolder(viewHolder);
        Glide.with(context).load(item.getRes()).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(object);
    }

    public static class GlideImageView extends ImageViewTarget<GlideDrawable> {

        private ViewHolder mViewHolder;
        public GlideImageView(ImageView view) {
            super(view);
        }

        public void bindViewHolder(ViewHolder viewHolder){
            mViewHolder = viewHolder;
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setImageDrawable(resource);
            mViewHolder.mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
            mViewHolder.mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            mViewHolder.mProgress.setVisibility(View.GONE);
            mViewHolder.mLoadFialView.setVisibility(View.VISIBLE);

        }
    }

    public static class ViewHolder{
        public PhotoView mPhoneView;
        public ImageView mLoadFialView;
        public ProgressBar mProgress;
    }
}
