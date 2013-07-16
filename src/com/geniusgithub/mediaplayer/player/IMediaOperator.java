package com.geniusgithub.mediaplayer.player;


public interface IMediaOperator {
	public void exit();
	public void replay();
	public void pause();
	public void stop();
	public void prev();
	public void next();
	public void skipTo(int time);
}
