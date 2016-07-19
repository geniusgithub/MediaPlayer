package com.geniusgithub.mediaplayer.browse.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.browse.IBrowsePresenter;
import com.geniusgithub.mediaplayer.browse.adapter.ContentAdapter;
import com.geniusgithub.mediaplayer.browse.adapter.DeviceAdapter;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class BroswerView implements BrowsePresenter.IBrowseView,
                                    View.OnClickListener{

    private Context mContext;
    private Button mBtnSearch;
    private Button mBtnReset;
    private Button mBtnExit;

    private ListView mDevListView;
    private ListView mContentListView;
    private DeviceAdapter mDevAdapter;
    private ContentAdapter mContentAdapter;

    private  OnContentItemClick mOnContentItemClick;
    private  OnDeviceItemClick mOnDeviceItemClick;

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
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnReset = (Button) view.findViewById(R.id.btn_reset);
        mBtnExit = (Button) view.findViewById(R.id.btn_exit);
        mBtnSearch.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);

        mDevListView = (ListView) view.findViewById(R.id.device_list);
        mOnDeviceItemClick = new OnDeviceItemClick();
        mDevListView.setOnItemClickListener(mOnDeviceItemClick);

        mContentListView = (ListView) view.findViewById(R.id.content_list);
        mOnContentItemClick = new OnContentItemClick();
        mContentListView.setOnItemClickListener(mOnContentItemClick);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading...");

        mDevAdapter = new DeviceAdapter(mContext, new ArrayList<Device>());
        mDevListView.setAdapter(mDevAdapter);

        mContentAdapter = new ContentAdapter(mContext,  new ArrayList<MediaItem>());
        mContentListView.setAdapter(mContentAdapter);
    }

    @Override
    public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_search:
                    mIBrowsePresenter.onSearch();
                    break;
                case R.id.btn_reset:
                    mIBrowsePresenter.onReset();
                     break;
                case R.id.btn_exit:
                    mIBrowsePresenter.onExit();
                      break;
            }
     }

    @Override
    public void showProgress(boolean bShow)
    {
        mProgressDialog.dismiss();
        if (bShow){
            mProgressDialog.show();
        }

    }

    private class OnDeviceItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Device device = (Device) parent.getItemAtPosition(position);
            mIBrowsePresenter.enterDevice(device);
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
        mDevAdapter.refreshData(devices);
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
    public void showItemList(boolean bShow){
        mContentListView.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }
}
