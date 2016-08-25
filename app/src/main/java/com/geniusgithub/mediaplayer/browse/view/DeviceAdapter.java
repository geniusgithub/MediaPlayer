package com.geniusgithub.mediaplayer.browse.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniusgithub.mediaplayer.R;

import org.cybergarage.upnp.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<ViewHolder> implements DeviceItemViewHolder.onItemClickListener {


    private final static String TAG = DeviceAdapter.class.getSimpleName();
	private Context mContext;
    private  List<Device> devices;


	public DeviceAdapter(Context context, List<Device>  devices){
		super();
		mContext = context;
        this.devices = devices;
	}

    public void refreshDevices(List<Device> devices)
    {

        this.devices = devices;
        notifyDataSetChanged();
    }

    public DeviceItemViewHolder.onItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(DeviceItemViewHolder.onItemClickListener listener){
        mOnItemClickListener = listener;
    }


   @Override
   public int getItemCount() {
       int count = 0;
       if (devices != null){
           count = devices.size();
       }

      return count;
   }
	
    public Object getItem(int position) {
        if (devices == null) {
            return null;
        }
        return devices.get(position);
    }
    
    
	
	private final int NORMAL_DEVICE_TYPE = 0;
	
    @Override
    public int getItemViewType(int position) {
    	return NORMAL_DEVICE_TYPE;
    }

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case NORMAL_DEVICE_TYPE:
                bindDeviceViewHolder(viewHolder, position);
                break;
            default:
                bindDeviceViewHolder(viewHolder, position);
                break;
        }

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

		switch (viewType) {
		case NORMAL_DEVICE_TYPE:
			return createDeviceEntryViewHolder(viewGroup);

		default:
			break;
		}

		return createDeviceEntryViewHolder(viewGroup);
	}
	
	
	
    private ViewHolder createDeviceEntryViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.device_list_item, parent, false);
        DeviceItemViewHolder viewHolder = new DeviceItemViewHolder(view);
        viewHolder.setOnItemClickListener(this);
        return viewHolder;
    }
    
    private void bindDeviceViewHolder(ViewHolder viewHolder, int position) {
        Device device = (Device) getItem(position);
        if (device == null) {
            return;
        }

        DeviceItemViewHolder callLogListItemViewHolder = (DeviceItemViewHolder) viewHolder;
        callLogListItemViewHolder.bindInfo(device);
    }


    @Override
    public void onItemClick(Device device) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(device);
        }
    }

    @Override
    public void onDetailClick(Device device)   {
          if (mOnItemClickListener != null) {
              mOnItemClickListener.onDetailClick(device);
          }
    }
}
