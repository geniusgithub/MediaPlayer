package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;

import com.geniusgithub.mediaplayer.base.adapter.BaseListAdapter;

import org.cybergarage.upnp.Device;

import java.util.List;

public class DeviceAdapter  extends BaseListAdapter<Device, DeviceItemView> implements DeviceItemView.onDeviceDetialClickListener{

    private DeviceItemView.onDeviceDetialClickListener mDetailClick;

    public DeviceAdapter(Context context, List<Device> data) {
        super(context, data);
    }

    @Override
    public DeviceItemView createView(Context context, int viewType) {
        DeviceItemView itemView = new DeviceItemView(context);
        itemView.setOnDeviceDetialClickListener(this);
        return itemView;
    }

    public void setOnDeviceDetialClickListener(DeviceItemView.onDeviceDetialClickListener listener){
        mDetailClick = listener;
    }

    @Override
    public void onDetailViewClick(Device device) {
        if (mDetailClick != null){
            mDetailClick.onDetailViewClick(device);
        }
    }
}
