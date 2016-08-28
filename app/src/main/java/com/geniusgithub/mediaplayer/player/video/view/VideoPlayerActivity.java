package com.geniusgithub.mediaplayer.player.video.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.geniusgithub.mediaplayer.R;

public class VideoPlayerActivity extends Activity {

	public static final String TAG_VIDEO_FRAGMENT = "tag_video_fragment";
	private VideoPlayerFragment mVideoPlayerFragment;


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		mVideoPlayerFragment.onNewIntent(intent);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.video_activity_layout);
		mVideoPlayerFragment = new VideoPlayerFragment();
		getFragmentManager().beginTransaction().add(R.id.content_container, mVideoPlayerFragment, TAG_VIDEO_FRAGMENT).commit();
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		boolean flag = mVideoPlayerFragment.dispatchTouchEvent(ev);
		if (flag){
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
}
