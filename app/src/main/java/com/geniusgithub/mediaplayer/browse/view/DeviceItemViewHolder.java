package com.geniusgithub.mediaplayer.browse.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;

import org.cybergarage.upnp.Device;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


	public static interface onItemClickListener{
		public void onItemClick(Device device);
		public void onDetailClick(Device device);
	}

	@BindView(R.id.ctrl_list_item_name)
	public TextView mNameView;

	@BindView(R.id.ctrl_list_item_ip)
	public TextView mIPView;

	@BindView(R.id.fl_detail)
	public View mDetailView;

	@BindView(R.id.cardview)
    public CardView mCardView;

	public Device mDevice;

	public onItemClickListener mOnItemClickListener;

	
	public DeviceItemViewHolder(View itemView) {
		super(itemView);

		ButterKnife.bind(this, itemView);
		mDetailView.setOnClickListener(this);
		mCardView.setOnClickListener(this);
	}

	public void bindInfo(Device dataItem){
		mDevice = dataItem;

		mNameView.setText(dataItem.getFriendlyName());
		mIPView.setText(dataItem.getHost());
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
