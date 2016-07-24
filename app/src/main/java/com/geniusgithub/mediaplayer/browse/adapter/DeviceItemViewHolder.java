package com.geniusgithub.mediaplayer.browse.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;

import org.cybergarage.upnp.Device;

public class DeviceItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


	public static interface onItemClickListener{
		public void onItemClick(Device device);
		public void onDetailClick(Device device);
	}

	public TextView mNameView;
	public TextView mIPView;
	public View mDetailView;
    public CardView mCardView;
	public Device mDevice;

	public onItemClickListener mOnItemClickListener;

	
	public DeviceItemViewHolder(View itemView) {
		super(itemView);

		mNameView = (TextView)itemView.findViewById(R.id.ctrl_list_item_name);
		mIPView = (TextView)itemView.findViewById(R.id.ctrl_list_item_ip);
		mDetailView = itemView.findViewById(R.id.fl_detail);
		mCardView = (CardView) itemView.findViewById(R.id.cardview);

		mDetailView.setOnClickListener(this);
		mCardView.setOnClickListener(this);
	}

	public void bindInfo(Device dataItem){
		mDevice = dataItem;

		mNameView.setText(dataItem.getFriendlyName());
		mIPView.setText(dataItem.getInterfaceAddress());
	}

	public void setOnItemClickListener(onItemClickListener listener){
		mOnItemClickListener = listener;
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.cardview:
				if (mOnItemClickListener != null){
					mOnItemClickListener.onItemClick(mDevice);
				}
				break;
			case R.id.fl_detail:
				if (mOnItemClickListener != null){
					mOnItemClickListener.onDetailClick(mDevice);
				}
				break;
		}

	}
}
