package com.geniusgithub.mediaplayer.dlna.control.browsecontrol;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;

public interface IBrowseController {
    public boolean browseItem(Device device, String id, ContainerNode rootNode);
}
