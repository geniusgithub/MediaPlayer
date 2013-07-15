package com.geniusgithub.mediaplayer.adapter;

import java.util.List;

import org.cybergarage.upnp.Device;

import com.geniusgithub.mediaplayer.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter{

	private  List<Device> devices;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public DeviceAdapter(Context context, List<Device>  devices) {
		mInflater = LayoutInflater.from(context);
		this.devices = devices;
		mContext = context;
	}
	
	public void refreshData(List<Device>  devices)
	{
		this.devices = devices;
		notifyDataSetChanged();
	}


	/**
	 * The number of items in the list is determined by the number of
	 * speeches in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return devices.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficent to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		return devices.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.device_list_item, null);
		}
		
		Device dataItem = (Device) getItem(position);
		TextView geckoView = (TextView)convertView.findViewById(R.id.ctrl_list_item_name);
		geckoView.setText(dataItem.getFriendlyName());
		TextView locationView = (TextView)convertView.findViewById(R.id.ctrl_list_item_location);
		locationView.setText(dataItem.getLocation());
		TextView uuidView = (TextView)convertView.findViewById(R.id.ctrl_list_item_uuid);
		uuidView.setText(dataItem.getUDN());
		TextView typeView = (TextView)convertView.findViewById(R.id.ctrl_list_item_type);
		typeView.setText(dataItem.getDeviceType());
		
		return convertView;
	}
	
	
}
