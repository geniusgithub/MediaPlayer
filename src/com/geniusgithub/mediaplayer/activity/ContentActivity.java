package com.geniusgithub.mediaplayer.activity;

import java.util.ArrayList;
import java.util.List;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.adapter.ContentAdapter;
import com.geniusgithub.mediaplayer.music.MusicPlayerActivity;
import com.geniusgithub.mediaplayer.picture.PicturePlayerActivity;
import com.geniusgithub.mediaplayer.proxy.AllShareProxy;
import com.geniusgithub.mediaplayer.proxy.BrowseDMSProxy;
import com.geniusgithub.mediaplayer.proxy.IDeviceChangeListener;
import com.geniusgithub.mediaplayer.proxy.BrowseDMSProxy.BrowseRequestCallback;
import com.geniusgithub.mediaplayer.proxy.MediaManager;
import com.geniusgithub.mediaplayer.upnp.DMSDeviceBrocastFactory;
import com.geniusgithub.mediaplayer.upnp.MediaItem;
import com.geniusgithub.mediaplayer.upnp.MediaItemFactory;
import com.geniusgithub.mediaplayer.upnp.UpnpUtil;
import com.geniusgithub.mediaplayer.util.CommonUtil;
import com.geniusgithub.mediaplayer.video.VideoPlayerActivity;

public class ContentActivity extends Activity implements OnItemClickListener, IDeviceChangeListener, 
												BrowseRequestCallback, OnClickListener{

	private static final CommonLog log = LogFactory.createLog();
	
	private TextView mTVSelDeV;
	private ListView mContentListView;
	private Button mBtnBack;
	
	
	private ContentAdapter mContentAdapter;
	private AllShareProxy mAllShareProxy;
	private ContentManager mContentManager;
	
	private List<MediaItem> mCurItems;	
	private DMSDeviceBrocastFactory mBrocastFactory;
	
	private Handler mHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        
        initView();   
        initData();
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	
	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		mContentManager.clear();
		mBrocastFactory.unRegisterListener();
		
		super.onDestroy();
	}

	
	

	private void initView()
    {
    	
    	mTVSelDeV = (TextView) findViewById(R.id.tv_selDev);
    	mContentListView = (ListView) findViewById(R.id.content_list);
    	mContentListView.setOnItemClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	
    	mProgressDialog = new ProgressDialog(this);   	
    	mProgressDialog.setMessage("Loading...");
    }

    private void initData()
    {
    	mAllShareProxy = AllShareProxy.getInstance(this);
    	mContentManager = ContentManager.getInstance();
    	
    	
    	mCurItems = new ArrayList<MediaItem>();
    	mContentAdapter = new ContentAdapter(this, mCurItems);
    	mContentListView.setAdapter(mContentAdapter);
    	
    	mBrocastFactory = new DMSDeviceBrocastFactory(this);
    	
    	updateSelDev();
    	
    	mHandler = new Handler();
    	mHandler.postDelayed(new RequestDirectoryRunnable(), 100);
    	
    	mBrocastFactory.registerListener(this);
    }
    
    
    
    private void requestDirectory()
    {
    	Device selDevice = mAllShareProxy.getDMSSelectedDevice();
    	if (selDevice == null){
    		CommonUtil.showToask(this, "当前未选中任何设备...");
    		finish();
    		return ;
    	}
    	
    	BrowseDMSProxy.syncGetDirectory(this, this);
    	showProgress(true);
    }
    
    class RequestDirectoryRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			requestDirectory();
		}
    	
    }
	

	
	private void setContentlist(List<MediaItem> list)
	{	
		mCurItems = list;
		if (list == null){
			mContentAdapter.clear();
		}else{
			mContentAdapter.refreshData(list);
		}
	}
	
	
	private ProgressDialog mProgressDialog;
	private void showProgress(boolean bShow)
	{
		mProgressDialog.dismiss();
		if (bShow){
			mProgressDialog.show();
		}
			
	}


	private void goMusicPlayerActivity(int index, MediaItem item){
		
		MediaManager.getInstance().setMusicList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this, MusicPlayerActivity.class);
		intent.putExtra(MusicPlayerActivity.PLAY_INDEX, index);
		MediaItemFactory.putItemToIntent(item, intent);
		ContentActivity.this.startActivity(intent);
	}
	
	private void goVideoPlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setVideoList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this, VideoPlayerActivity.class);
		intent.putExtra(VideoPlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		ContentActivity.this.startActivity(intent);
	}
	
	
	private void goPicturePlayerActivity(int position, MediaItem item){
		
	    MediaManager.getInstance().setPictureList(mCurItems);
		
		Intent intent = new Intent();
		intent.setClass(this, PicturePlayerActivity.class);
		intent.putExtra(PicturePlayerActivity.PLAY_INDEX, position);
		MediaItemFactory.putItemToIntent(item, intent);
		ContentActivity.this.startActivity(intent);
	}
	

	private void back(){
		mContentManager.popListItem();
		List<MediaItem> list = mContentManager.peekListItem();
		if (list == null){
			super.onBackPressed();
		}else{
			setContentlist(list);
		}	
		
	}

	@Override
	public void onBackPressed() {
		back();	
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		MediaItem item = (MediaItem) parent.getItemAtPosition(position);
		log.e("item = \n" + item.getShowString());		
		
		if (UpnpUtil.isAudioItem(item)) {
			goMusicPlayerActivity(position, item);
		}else if (UpnpUtil.isVideoItem(item)){
			goVideoPlayerActivity(position, item);
		}else if (UpnpUtil.isPictureItem(item)){
			goPicturePlayerActivity(position, item);
		}else{
			BrowseDMSProxy.syncGetItems(ContentActivity.this, item.getStringid(), ContentActivity.this);
		  	showProgress(true);
		}
		
	}

	@Override
	public void onDeviceChange(boolean isSelDeviceChange) {
		// TODO Auto-generated method stub
		if (isSelDeviceChange){
			CommonUtil.showToask(this, "当前设备已卸载设备...");
			finish();
		}
	}

	@Override
	public void onGetItems(final List<MediaItem> list) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showProgress(false);
				if (list == null){
					CommonUtil.showToask(ContentActivity.this, "无法获取目录...");
					return ;
				}		
				mContentManager.pushListItem(list);			
				setContentlist(list);
				
			}
		});
	}
	
	
	private void updateSelDev()
	{
		setSelDevUI(mAllShareProxy.getDMSSelectedDevice());
	}
	
	
	private void setSelDevUI(Device device)
	{
		if (device == null)
		{
			mTVSelDeV.setText("no select device");
		}else{
			mTVSelDeV.setText(device.getFriendlyName());
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_back:
			back();
			break;
		}
	}


}
