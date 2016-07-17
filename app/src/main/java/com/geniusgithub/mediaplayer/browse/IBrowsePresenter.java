package com.geniusgithub.mediaplayer.browse;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

public interface IBrowsePresenter {
    public void onSearch();
    public void onReset();
    public void onExit();
    public void enterDevice(Device device);
    public void browseItem(int index, MediaItem item);
}
