package com.geniusgithub.mediaplayer.dlna.util;

import android.util.Log;

public class DlnaUtil {


    private final static String TAG = DlnaUtil.class.getSimpleName();

    public static long formatSizeString(String sizeString) {
        long size = 0;
        if (sizeString == null || sizeString.length() < 1){
            return size;
        }
        try {
            size = Long.parseLong(sizeString);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "sizeString = " + sizeString);
        }
        return size;
    }



    public static int formatDurationString(String durationString) {
        int duration = 0;
        if(durationString == null || durationString.length() == 0){
            return duration;
        }
        try {
            String sArray[] = durationString.split(":");
            double hour = Double.valueOf(sArray[0]);
            double minute = Double.valueOf(sArray[1]);
            double second = Double.valueOf(sArray[2]);
            return (int) ((hour * 60 + minute) * 60 + second) * 1000;
        } catch (Exception e) {

        }

        return duration;
    }


    private final static String DURATION_FORMAT = "hh:MM:ss";
    public static String  formatDurationString(int duration) {

        String str = "";
        int hour = 0;
        int time = (int) (duration / 1000);
        int second = time % 60;
        int minute = time / 60;
        if (minute >= 60){
            hour = minute / 60;
            minute %= 60;
        }
        str = String.format("%02d:%02d:%02d", hour, minute, second);
        return str;
    }

    public static int formatResolution(String resolutionString){
        int value = 0;
        if(resolutionString == null || resolutionString.length() == 0){
            return value;
        }

        try {
            String array[] = resolutionString.split("x");
            int v1 = Integer.valueOf(array[0]);
            int v2 = Integer.valueOf(array[1]);

            value = v1 * v2;
        } catch (Exception e) {

        }

        return value;
    }

    public static boolean compareBetweenResolution(String resolution1, String resolution2){

        int resolutionInt1 = formatResolution(resolution1);
        int resolutionInt2 = formatResolution(resolution2);

        return resolutionInt1 >= resolutionInt2 ? true : false;
    }


}
