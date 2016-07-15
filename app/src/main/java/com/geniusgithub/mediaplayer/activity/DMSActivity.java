package com.geniusgithub.mediaplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.adapter.DeviceAdapter;
import com.geniusgithub.mediaplayer.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.upnp.DMSDeviceBrocastFactory;

import org.cybergarage.upnp.Device;
import com.geniusgithub.mediaplayer.util.CommonLog;
import com.geniusgithub.mediaplayer.util.LogFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author lance
 * @csdn  http://blog.csdn.net/geniuseoe2012
 * @github https://github.com/geniusgithub
 */
public class DMSActivity extends BaseActivity implements OnClickListener, 
															IDeviceChangeListener,
															OnItemClickListener{
    /** Called when the activity is first created. */
	private static final CommonLog log = LogFactory.createLog();
	
	private Button mBtnSearch;
	private Button mBtnReset;
	private Button mBtnExit;
	
	private TextView mTVSelDeV;
	private ListView mDevListView;
	
	private DeviceAdapter mDevAdapter;
	private AllShareProxy mAllShareProxy;
	
	private DMSDeviceBrocastFactory mBrocastFactory;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dms_layout);
        
        initView();
        
        initData();
        
    }

    @Override
	protected void onResume() {
		super.onResume();
		
		updateDeviceList();	
	
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	
	}

	@Override
	protected void onDestroy() {
		
		mBrocastFactory.unRegisterListener();
		
		
		super.onDestroy();
	}

	private void initView(){
		mBtnSearch = (Button) findViewById(R.id.btn_search);
		mBtnReset = (Button) findViewById(R.id.btn_reset);
		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnSearch.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnExit.setOnClickListener(this);	
		
		mDevListView = (ListView) findViewById(R.id.device_list);
		mDevListView.setOnItemClickListener(this);
    }
	
	private void initData(){
		mAllShareProxy = AllShareProxy.getInstance(this);
		
		mDevAdapter = new DeviceAdapter(this, new ArrayList<Device>());
    	mDevListView.setAdapter(mDevAdapter);
    	
    	mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	mBrocastFactory.registerListener(this);

		AllShareApplication.getInstance().setStatus(true);
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
				finish();
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
		Intent intent = new Intent(this, ContentActivity.class);
		startActivity(intent);
	}
	
//	private String test(){
//			ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//			if (networkInfo != null) {
//				if (networkInfo.getTypeName().toString().equalsIgnoreCase("ETHERNET")) {
//					if (networkInfo.isConnected() && networkInfo.isAvailable()) {
//						EthernetManager mEthManager = (EthernetManager) context
//								.getSystemService(Context.ETH_SERVICE);
//						EthernetDevInfo mEthInfo = mEthManager.getSavedEthConfig();
//						if (mEthInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
//							DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
//							return getAddress(dhcpInfo.ipAddress);
//						} else {
//							mEthInfo = mEthManager.getSavedEthConfig();
//							if (mEthInfo != null) {
//
//								return mEthInfo.getIpAddress();
//							}
//						}
//					}
//
//				} else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//
//					if (networkInfo.isConnected() && networkInfo.isAvailable()) {
//						WifiManager mWifiManager = (WifiManager) context
//								.getSystemService(Context.WIFI_SERVICE);
//						final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
//						int ipAddr = wifiInfo.getIpAddress();
//						StringBuffer ipBuf = new StringBuffer();
//						ipBuf.append(ipAddr & 0xff).append('.').append((ipAddr >>>= 8) & 0xff)
//								.append('.').append((ipAddr >>>= 8) & 0xff).append('.')
//								.append((ipAddr >>>= 8) & 0xff);
//
//						return ipBuf.toString();
//					}
//				}
//			}
//			return "0.0.0.0";
//	}
}