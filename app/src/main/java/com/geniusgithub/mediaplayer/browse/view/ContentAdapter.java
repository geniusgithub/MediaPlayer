package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.common.cache.ImageLoaderEx;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> implements ContentItemViewHolder.onItemClickListener {


    private final static String TAG = ContentAdapter.class.getSimpleName();
    private Context mContext;
    private  List<MediaItem> contentItem;
    private LayoutInflater mInflater;

    private boolean mBusy = false;
    private ImageLoaderEx mImageLoader;

    public ContentAdapter(Context context, List<MediaItem>  mediaItemList){
		super();
		mContext = context;
        contentItem = mediaItemList;
        mInflater = LayoutInflater.from(context);

        Resources res = context.getResources();
        ContentItemViewHolder.loadDefaultDrawable(res);


        mImageLoader = new ImageLoaderEx(context);
	}


    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
    }


    public void refreshData(List<MediaItem>  contentItem)
    {
        this.contentItem = contentItem;
        notifyDataSetChanged();
    }

    public void clear()
    {
        if (contentItem != null){
            contentItem.clear();
            notifyDataSetChanged();
        }
    }

    public ContentItemViewHolder.onItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(ContentItemViewHolder.onItemClickListener listener){
        mOnItemClickListener = listener;
    }


   @Override
   public int getItemCount() {
       int count = 0;
       if (contentItem != null){
           count = contentItem.size();
       }

      return count;
   }
	
    public Object getItem(int position) {
        if (contentItem == null) {
            return null;
        }
        return contentItem.get(position);
    }
    
    
	
	private final int NORMAL_CONTENT_TYPE = 0;
	
    @Override
    public int getItemViewType(int position) {
    	return NORMAL_CONTENT_TYPE;
    }

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case NORMAL_CONTENT_TYPE:
                bindContentViewHolder(viewHolder, position);
                break;
            default:
                bindContentViewHolder(viewHolder, position);
                break;
        }

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

		switch (viewType) {
		case NORMAL_CONTENT_TYPE:
			return createContentItemEntryViewHolder(viewGroup);

		default:
			break;
		}

		return createContentItemEntryViewHolder(viewGroup);
	}
	
	
	
    private ViewHolder createContentItemEntryViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.content_list_item, parent, false);
        ContentItemViewHolder viewHolder = new ContentItemViewHolder(view);
        viewHolder.setOnItemClickListener(this);
        return viewHolder;
    }
    
    private void bindContentViewHolder(ViewHolder viewHolder, int position) {
        MediaItem item = (MediaItem) getItem(position);
        if (item == null) {
            return;
        }

        ContentItemViewHolder contentViewHolder = (ContentItemViewHolder) viewHolder;
        contentViewHolder.bindInfo(position, item,  mImageLoader, mBusy);
    }


    @Override
    public void onItemClick(int pos, MediaItem item) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(pos, item);
        }
    }
}
