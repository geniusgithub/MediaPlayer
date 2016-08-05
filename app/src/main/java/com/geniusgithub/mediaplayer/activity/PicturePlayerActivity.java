package com.geniusgithub.mediaplayer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;
import com.geniusgithub.mediaplayer.player.picture.PictureFragment;

public class PicturePlayerActivity extends BaseActivity {

	public static final String TAG_PICTURE_FRAGMENT = "tag_picture_fragment";
	private Toolbar mToolbar;
	private PictureFragment mPicturePlayerFragment;
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
		mPicturePlayerFragment = new PictureFragment();
		getFragmentManager().beginTransaction().add(R.id.content_container, mPicturePlayerFragment, TAG_PICTURE_FRAGMENT).commit();

		initToolBar();


	}

	private void initToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("PICTURE");
		mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
		setSupportActionBar(mToolbar);


		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);


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


		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
}
