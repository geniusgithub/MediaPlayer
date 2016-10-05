package com.geniusgithub.mediaplayer.dlna.control.rendercontrol;

import org.cybergarage.upnp.Device;

public class RenderController implements IRenderController {


    private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
    private static final String SetAVTransportURI = "SetAVTransportURI";
    private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";
    private static final String Play = "Play";


    @Override
    public boolean play(Device device, String path) {
        return false;
    }

    @Override
    public boolean goon(Device device, String pausePosition) {
        return false;
    }

    @Override
    public String getTransportState(Device device) {
        return null;
    }

    @Override
    public int getMinVolumeValue(Device device) {
        return 0;
    }

    @Override
    public int getMaxVolumeValue(Device device) {
        return 0;
    }

    @Override
    public boolean seek(Device device, String targetPosition) {
        return false;
    }

    @Override
    public String getPositionInfo(Device device) {
        return null;
    }

    @Override
    public String getMediaDuration(Device device) {
        return null;
    }

    @Override
    public boolean setMute(Device device, String targetValue) {
        return false;
    }

    @Override
    public String getMute(Device device) {
        return null;
    }

    @Override
    public boolean setVoice(Device device, int value) {
        return false;
    }

    @Override
    public int getVoice(Device device) {
        return 0;
    }

    @Override
    public boolean stop(Device device) {
        return false;
    }

    @Override
    public boolean pause(Device device) {
        return false;
    }
}
