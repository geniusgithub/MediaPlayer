package com.geniusgithub.mediaplayer.player;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractTimer {

	private final static int TIMER_INTERVAL = 1000;
	
	protected Context mContext;
	private Timer mTimer;	
	protected MyTimeTask mTimeTask;
	protected int mTimeInterval = TIMER_INTERVAL;
	protected Handler mHandler;
	protected int msgID;
	
	public AbstractTimer(Context context)
	{
		mContext = context;	
		mTimer = new Timer();	
	}
	
	public void setHandler( Handler handler, int msgID){
		mHandler = handler;
		this.msgID = msgID;
	}
	
	public void setTimeInterval(int interval){
		mTimeInterval = interval;
	}
	
	public void startTimer()
	{
		if (mTimeTask == null)
		{
			mTimeTask = new MyTimeTask();
			mTimer.schedule(mTimeTask, 0, mTimeInterval);
		}
	}
	
	public void stopTimer()
	{
		if (mTimeTask != null)
		{
			mTimeTask.cancel();
			mTimeTask = null;
		}
	}

	
	class MyTimeTask extends TimerTask
	{

		@Override
		public void run() {
			if (mHandler != null){
				Message msg = mHandler.obtainMessage(msgID);
				msg.sendToTarget();
			}
		}
		
	}
	
}
