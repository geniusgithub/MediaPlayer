package com.geniusgithub.mediaplayer.player.picture.View;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;

public class PhotoBrowseActivity extends BaseActivity {

	public static final String TAG = PhotoBrowseActivity.class.getSimpleName();
	public static final String TAG_PICTURE_FRAGMENT = "tag_picture_fragment";
	private Toolbar mToolbar;
	private PhotoBrowseFragment mPhotoBrowseFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.picture_activity_layout);
		mPhotoBrowseFragment = new PhotoBrowseFragment();
		getFragmentManager().beginTransaction().add(R.id.content_container, mPhotoBrowseFragment, TAG_PICTURE_FRAGMENT).commit();


	}


}
