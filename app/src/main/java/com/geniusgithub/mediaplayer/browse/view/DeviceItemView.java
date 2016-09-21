package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.adapter.IBaseAdapterView;
import com.geniusgithub.mediaplayer.base.adapter.OnItemClickListener;

import org.cybergarage.upnp.Device;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceItemView extends LinearLayout implements IBaseAdapterView<Device>, View.OnClickListener{

    public static interface onDeviceItemClickListener extends OnItemClickListener<Device> {
    }

    public static interface onDeviceDetialClickListener {
        public void onDetailViewClick(Device device);
    }

    @BindView(R.id.ctrl_list_item_name)
    public TextView mNameView;

    @BindView(R.id.ctrl_list_item_ip)
    public TextView mIPView;

    @BindView(R.id.fl_detail)
    public View mDetailView;


    private onDeviceDetialClickListener mDetailClick;

    public Device mDevice;

    public DeviceItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.device_list_item, this);
        ButterKnife.bind(this);

        mDetailView.setOnClickListener(this);
    }

    @Override
    public void bindView(Device data, int position) {
        mDevice = data;

        mNameView.setText(data.getFriendlyName());
        mIPView.setText(data.getHost());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fl_detail:
                if (mDetailClick != null){
                    mDetailClick.onDetailViewClick(mDevice);
                }
                break;
        }
    }

    public void setOnDeviceDetialClickListener(onDeviceDetialClickListener listener){
        mDetailClick = listener;
    }
}
