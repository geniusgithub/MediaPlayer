package com.geniusgithub.mediaplayer.dlna.control.rendercontrol;

import org.cybergarage.upnp.Device;

public interface IRemotePlayback {

    boolean play(Device device, String path);

    boolean play(Device device);

    boolean pause(Device device);

    boolean stop(Device device);

    boolean getProgress(Device device);

    boolean getDuration(Device device);

    boolean getTransportState(Device device);

    boolean seekTo(Device device, int progress);

    public void registerCallback(Callback callback);

    public void unregisterCallback();

    public static interface Callback{
        public void onRePlayComplete(boolean isSuccess);
        public void onPlayComplete(boolean isSuccess);
        public void onPauseComplete(boolean isSuccess);
        public void onStopComplete(boolean isSuccess);
        public void onGetProgressComplete(boolean isSuccess, int progress);
        public void onGetDurationComplete(boolean isSuccess, int duration);
        public void onGetTransportComplete(boolean isSuccess, int state);
        public void onSeekComplete(boolean isSuccess);
    }


}
