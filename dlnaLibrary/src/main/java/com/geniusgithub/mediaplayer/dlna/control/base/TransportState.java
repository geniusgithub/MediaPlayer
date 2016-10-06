package com.geniusgithub.mediaplayer.dlna.control.base;

public  class TransportState {
    /**
     * All the state is "STOPPED" // "PLAYING" // "TRANSITIONING"//
     * "PAUSED_PLAYBACK"// "PAUSED_RECORDING"// "RECORDING" //
     * "NO_MEDIA_PRESENT//
     */
    public static final String STRING_TRANSITIONING= "PLAYING";
    public static final  String STRING_PLAYING = "PLAYING";
    public static final String STRING_PAUSED_PLAYBACK = "PAUSED_PLAYBACK";
    public static final String STRING_STOPPED = "STOPPED";

    public static int getState(String state){
        int result = TransportState.UNKNOW;

        if (state != null){
            if (state.equalsIgnoreCase(TransportState.STRING_TRANSITIONING)){
                result = TransportState.TRANSITIONING;
            }else if (state.equalsIgnoreCase(TransportState.STRING_PLAYING)){
                result = TransportState.PLAYING;
            }else if (state.equalsIgnoreCase(TransportState.STRING_PAUSED_PLAYBACK)){
                result = TransportState.PAUSED_PLAYBACK;
            }else if (state.equalsIgnoreCase(TransportState.STRING_STOPPED)){
                result = TransportState.STOPPED;
            }
        }

        return result;
    }

    public static final int UNKNOW = 0;
    public static final int TRANSITIONING = 1;
    public static final int PLAYING = 2;
    public static final int PAUSED_PLAYBACK = 3;
    public static final  int STOPPED = 4;
}
