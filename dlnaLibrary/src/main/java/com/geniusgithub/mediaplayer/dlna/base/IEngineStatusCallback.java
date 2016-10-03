package com.geniusgithub.mediaplayer.dlna.base;

public interface IEngineStatusCallback {
    public void onEngineCreate();
    public void onEngineDestory();
    public void onEngineRestart();
}
