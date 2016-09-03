package com.geniusgithub.mediaplayer.player.picture.View;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;
import com.geniusgithub.mediaplayer.base.IToolBar;
import com.geniusgithub.mediaplayer.base.ToolEntry;

public class PicturePlayerActivity extends BaseActivity implements IToolBar {

	public static final String TAG = PicturePlayerActivity.class.getSimpleName();
	public static final String TAG_PICTURE_FRAGMENT = "tag_picture_fragment";
	private Toolbar mToolbar;
	private PicturePlayerFragment mPicturePlayerFragment;
	private MenuItem mOptionItem;


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		mPicturePlayerFragment.onNewIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return  super.onPrepareOptionsMenu(menu);

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.picture_activity_layout);
		mPicturePlayerFragment = new PicturePlayerFragment();
		getFragmentManager().beginTransaction().add(R.id.content_container, mPicturePlayerFragment, TAG_PICTURE_FRAGMENT).commit();
		mPicturePlayerFragment.bindToolbar(this);

		initToolBar();

	}

	@Override
	public void updateToolTitle(ToolEntry entry) {
		if (mToolbar != null){
			mToolbar.setTitle(entry.title);
			mToolbar.setSubtitle(entry.subTitle);
		}
	}
	private void initToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("PICTURE");
		mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
		mToolbar.setBackgroundColor(Color.parseColor("#00ffffff"));
		setSupportActionBar(mToolbar);


		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);


	}



}
