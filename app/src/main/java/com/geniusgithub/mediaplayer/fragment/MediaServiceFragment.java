package com.geniusgithub.mediaplayer.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.activity.ContentActivity;
import com.geniusgithub.mediaplayer.adapter.DeviceAdapter;
import com.geniusgithub.mediaplayer.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;


public class MediaServiceFragment extends Fragment implements View.OnClickListener,
                                                                IDeviceChangeListener,
                                                                AdapterView.OnItemClickListener {


    private static final CommonLog log = LogFactory.createLog();
    private Context mContext;
    private Button mBtnSearch;
    private Button mBtnReset;
    private Button mBtnExit;

    private TextView mTVSelDeV;
    private ListView mDevListView;

    private DeviceAdapter mDevAdapter;
    private AllShareProxy mAllShareProxy;

    private DMSDeviceBrocastFactory mBrocastFactory;


    public MediaServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dms_layout, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
    }

    private void initView(View view){
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnReset = (Button) view.findViewById(R.id.btn_reset);
        mBtnExit = (Button) view.findViewById(R.id.btn_exit);
        mBtnSearch.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);

        mDevListView = (ListView) view.findViewById(R.id.device_list);
        mDevListView.setOnItemClickListener(this);
    }

    private void initData(){
        mAllShareProxy = AllShareProxy.getInstance(mContext.getApplicationContext());

        mDevAdapter = new DeviceAdapter(mContext, new ArrayList<Device>());
        mDevListView.setAdapter(mDevAdapter);

        mBrocastFactory = new DMSDeviceBrocastFactory(mContext);
        mBrocastFactory.registerListener(this);

        AllShareApplication.getInstance().setStatus(true);
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {

        mBrocastFactory.unRegisterListener();


        super.onDestroy();
    }

    private void updateDeviceList(){
        List<Device> list = mAllShareProxy.getDMSDeviceList();
        mDevAdapter.refreshData(list);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_search:
                mAllShareProxy.startSearch();
                break;
            case R.id.btn_reset:
                mAllShareProxy.resetSearch();
                break;
            case R.id.btn_exit:
                mAllShareProxy.exitSearch();
                getActivity().finish();
                break;
        }
    }


    @Override
    public void onDeviceChange(boolean isSelDeviceChange) {
        updateDeviceList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Device device = (Device) parent.getItemAtPosition(position);
        mAllShareProxy.setDMSSelectedDevice(device);
        goContentActivity();
    }


    private void goContentActivity(){
        Intent intent = new Intent(mContext, ContentActivity.class);
        startActivity(intent);
    }
}
