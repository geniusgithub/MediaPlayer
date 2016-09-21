package com.geniusgithub.mediaplayer.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public  abstract class BaseListAdapter<T, V extends IBaseAdapterView> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String TAG = BaseListAdapter.class.getSimpleName();

    private Context mContext;
    private List<T> mData;

    private OnItemClickListener<T> mItemClickListener;
    private int mLastItemClickPosition = RecyclerView.NO_POSITION;

    public BaseListAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
    }

    public abstract V createView(Context context, int viewType);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = (View) createView(mContext, viewType);
        final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(itemView) {};
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mLastItemClickPosition = position;
                    if (mItemClickListener != null){
                        mItemClickListener.onItemClick(getItem(position), position);
                    }
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IBaseAdapterView itemView = (V) holder.itemView;
        itemView.bindView(getItem(position), position);
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
    }


    public int getLastItemClickPosition() {
        return mLastItemClickPosition;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public OnItemClickListener getItemObserver(){
        return mItemClickListener;
    }


}
