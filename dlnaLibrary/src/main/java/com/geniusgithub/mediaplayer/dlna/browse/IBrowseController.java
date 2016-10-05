package com.geniusgithub.mediaplayer.dlna.browse;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.List;

public interface IBrowseController {
    public boolean browseItem(Device device, String id, List<MediaItem> list);
}
