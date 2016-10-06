package com.geniusgithub.mediaplayer.dlna.control.rendercontrol;

import org.cybergarage.upnp.Device;

public interface IRenderController {

    public boolean setAVTransportURI(Device dev,String path);


    /**
     * Play the video with the video path.
     *
     * @param device
     *            The device be controlled.
     *
     * @return If is success to play the video.
     */
    boolean play(Device device);


    /**
     * Pause the playing video.
     *
     * @param device
     *            The device to controlled.
     * @return If if success to pause the video.
     */
    boolean pause(Device device);


    /**
     * Stop to play.
     *
     * @param device
     *            The device to controlled.
     * @return If if success to stop the video.
     */
    boolean stop(Device device);



    /**
     * Get the current playing position of the video.
     *
     * @param device
     *            The device be controlled.
     * @return Current playing position is 00:00:00
     */
    String getPositionInfo(Device device);

    /**
     * Get the duration of the video playing.
     *
     * @param device
     *            The device be controlled.
     * @return The media duration like 00:00:00,if get failed it will return
     *         null.
     */
    String getMediaDuration(Device device);


    /**
     * All the state is "STOPPED" // "PLAYING" // "TRANSITIONING"//
     * "PAUSED_PLAYBACK"// "PAUSED_RECORDING"// "RECORDING" //
     * "NO_MEDIA_PRESENT//
     */
    String getTransportState(Device device);

    /**
     * Seek the playing video to a target position.
     *
     * @param device
     *            The device be controlled.
     * @param targetPosition
     *            Target position we want to set.
     * @return
     */
    boolean seek(Device device, String targetPosition);




/*    *//**
     * Go on playing the video from the position.
     *
     * @param device
     *            The device be controlled.
     * @param pausePosition
     *            The format must be 00:00:00.
     *//*
    boolean goon(Device device, String pausePosition);*/

}
