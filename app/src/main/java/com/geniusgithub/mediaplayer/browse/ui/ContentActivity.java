package com.geniusgithub.mediaplayer.browse.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.activity.BaseActivity;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;

import org.cybergarage.upnp.Device;

public class ContentActivity extends BaseActivity {

	private static final String TAG_CONTENT_FRAGMENT = "tag_content_fragment";
	private Toolbar mToolbar;
	private MediaContentFragment mMediaContentFragment;
	private AllShareProxy mAllShareProxy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_activity_layout);

		initView(savedInstanceState);
		initData();
	}

	private void initView(Bundle savedInstanceState){
		if (savedInstanceState == null) {
			mMediaContentFragment  = new MediaContentFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.content_container, mMediaContentFragment, TAG_CONTENT_FRAGMENT)
					.commit();
		}


		initToolBar();
	}

	private void initToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("DLNA");
		mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
		setSupportActionBar(mToolbar);


		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);


	}

	private void initData(){
		mAllShareProxy = AllShareProxy.getInstance(getApplicationContext());
		Device device  = mAllShareProxy.getDMSSelectedDevice();
		if (device == null)
		{
			mToolbar.setTitle("no select device");
		}else{
			mToolbar.setTitle(device.getFriendlyName());
		}
	}



	@Override
	public void onBackPressed() {
		boolean back = mMediaContentFragment.back();
		if (!back){
			super.onBackPressed();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}


