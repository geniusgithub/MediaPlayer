package com.geniusgithub.mediaplayer.browse.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.browse.IBrowsePresenter;
import com.geniusgithub.mediaplayer.browse.adapter.DeviceAdapter;
import com.geniusgithub.mediaplayer.browse.adapter.DeviceItemViewHolder;
import com.geniusgithub.mediaplayer.browse.adapter.ContentAdapter;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class BrowserView implements BrowsePresenter.IBrowseView{

    private Context mContext;

    private RecyclerView mDevListView;
    private DeviceAdapter mDevAdapter;
    private  OnDeviceItemClick mOnDeviceItemClick;
    private LinearLayoutManager mLayoutManager;

    private ListView mContentListView;
    private ContentAdapter mContentAdapter;
    private OnContentItemClick mOnContentItemClick;


    private ProgressDialog mProgressDialog;

    private IBrowsePresenter mIBrowsePresenter;

    @Override
    public void bindView(Context context, View container) {
        mContext = context;
        initView(container);
    }

    @Override
    public void bindPresent(IBrowsePresenter presenter) {
        mIBrowsePresenter = presenter;
    }

    private void initView(View view){

        mDevListView = (RecyclerView) view.findViewById(R.id.device_list);
        mDevListView.setHasFixedSize(true);
        mDevListView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(mContext);
        mDevListView.setLayoutManager(mLayoutManager);

        mDevAdapter = new DeviceAdapter(mContext, new ArrayList<Device>());
        mOnDeviceItemClick = new OnDeviceItemClick();
        mDevAdapter.setOnItemClickListener(mOnDeviceItemClick);
        mDevListView.setAdapter(mDevAdapter);





        mContentListView = (ListView) view.findViewById(R.id.content_list);
        mOnContentItemClick = new OnContentItemClick();
        mContentListView.setOnItemClickListener(mOnContentItemClick);
        mContentAdapter = new ContentAdapter(mContext,  new ArrayList<MediaItem>());
        mContentListView.setAdapter(mContentAdapter);


        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading...");


    }

    @Override
    public void showProgress(boolean bShow)
    {
        mProgressDialog.dismiss();
        if (bShow){
            mProgressDialog.show();
        }

    }

    private class OnDeviceItemClick  implements DeviceItemViewHolder.onItemClickListener{
        @Override
        public void onItemClick(Device device) {
            mIBrowsePresenter.enterDevice(device);
        }

        @Override
        public void onDetailClick(Device device) {
            mIBrowsePresenter.showDeviceDetail(device);
        }
    }


    private class OnContentItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem item = (MediaItem) parent.getItemAtPosition(position);
            mIBrowsePresenter.browseItem(position, item);

        }
    }

    @Override
    public void updateDeviceList(List<Device> devices)
    {
        mDevAdapter.refreshDevices(devices);
    }

    @Override
    public void updateItemList(List<MediaItem>  contentItem)
    {
        mContentAdapter.refreshData(contentItem);
    }

    @Override
    public void showDeviceList(boolean bShow){
        mDevListView.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDeviceDetail(Device device) {

    }

    @Override
    public void showItemList(boolean bShow){
        mContentListView.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }
}
