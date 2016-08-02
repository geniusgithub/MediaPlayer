package com.geniusgithub.mediaplayer.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.geniusgithub.mediaplayer.AllShareApplication;

public class BaseActivity extends AppCompatActivity{

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
