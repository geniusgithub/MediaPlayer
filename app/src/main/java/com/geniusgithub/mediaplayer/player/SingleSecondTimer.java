package com.geniusgithub.mediaplayer.player;

import android.content.Context;

import com.geniusgithub.mediaplayer.player.common.AbstractTimer;

public class SingleSecondTimer extends AbstractTimer {

	public SingleSecondTimer(Context context) {
		super(context);
		setTimeInterval(1000);
	}

}
