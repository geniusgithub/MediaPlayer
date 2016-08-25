package com.geniusgithub.mediaplayer.browse.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geniusgithub.mediaplayer.DialogFactory;
import com.geniusgithub.mediaplayer.base.IToolBar;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseFragment;
import com.geniusgithub.mediaplayer.browse.BrowseContract;
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;


public class BrowserMediaFragment extends BaseFragment{

    public static final String TAG = BrowserMediaFragment.class.getSimpleName();

    private View mRootView;
    private BrowsePresenter mBrowsePresenter;
    private BrowseContract.IView mBrowseView;


    private IToolBar mExternToolbar;
    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dms_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        onUIReady(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        DialogFactory.closeAllDialog();
    }

    @Override
    public void onDestroy(){
        mBrowsePresenter.onUiDestroy();
        super.onDestroy();

    }


    public boolean onBackPressed(){
        return mBrowsePresenter.onBackPressed();
    }

    private void onUIReady(View view){
        mRootView = view.findViewById(R.id.ll_root);

        mBrowsePresenter = new BrowsePresenter();
        mBrowseView = new BrowserView(getActivity());
        mBrowseView.setupView(mRootView);
        mBrowsePresenter.bindView(mBrowseView);
        mBrowsePresenter.onUiCreate(getActivity());
    }


    public void updateToolTitle(String title){
        if (mExternToolbar != null){
            mExternToolbar.updateToolTitle(title);
        }
    }









    public class BrowserView implements BrowseContract.IView{

        private Context mContext;

        private RecyclerView mDevListView;
        private DeviceAdapter mDevAdapter;
        private  OnDeviceItemClick mOnDeviceItemClick;
        private LinearLayoutManager mLayoutManager;

        private ListView mContentListView;
        private ContentAdapter mContentAdapter;
        private OnContentItemClick mOnContentItemClick;


        private ProgressDialog mProgressDialog;

        private BrowseContract.IPresenter mIBrowsePresenter;

        public BrowserView(Context context){
            mContext = context;
        }


        @Override
        public void bindPresenter(BrowseContract.IPresenter presenter) {
            mIBrowsePresenter = presenter;
        }

        @Override
        public void setupView(View rootView) {
            initView(rootView);
        }


        @Override
        public void showProgress(boolean bShow)
        {
            mProgressDialog.dismiss();
            if (bShow){
                mProgressDialog.show();
            }

        }

        @Override
        public void showDeviceList(boolean bShow){
            mDevListView.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }


        @Override
        public void showItemList(boolean bShow){
            mContentListView.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }

        @Override
        public void showDeviceDetail(Device device) {
            DialogFactory.popupDeviceDetailDialog(getActivity(), device);
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
        public void updateToolTitle(String title) {
            BrowserMediaFragment.this.updateToolTitle(title);
        }

        private class OnDeviceItemClick  implements DeviceItemViewHolder.onItemClickListener{
            @Override
            public void onItemClick(Device device) {
                mIBrowsePresenter.enterDevice(device);
            }

            @Override
            public void onDetailClick(Device device) {
                showDeviceDetail(device);
            }
        }


        private class OnContentItemClick implements AdapterView.OnItemClickListener{
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem item = (MediaItem) parent.getItemAtPosition(position);
                mIBrowsePresenter.browseItem(position, item);

            }
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

    }

}
