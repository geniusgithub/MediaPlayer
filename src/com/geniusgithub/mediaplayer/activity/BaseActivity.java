package com.geniusgithub.mediaplayer.activity;


import com.geniusgithub.mediaplayer.AllShareApplication;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AllShareApplication.onCatchError(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		AllShareApplication.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		AllShareApplication.onResume(this);
	}

}
