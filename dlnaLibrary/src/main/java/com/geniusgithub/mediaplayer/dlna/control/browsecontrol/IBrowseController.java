package com.geniusgithub.mediaplayer.dlna.control.browsecontrol;

import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.List;

public interface IBrowseController {
    public boolean browseItem(Device device, String id, List<MediaItem> list);
}
