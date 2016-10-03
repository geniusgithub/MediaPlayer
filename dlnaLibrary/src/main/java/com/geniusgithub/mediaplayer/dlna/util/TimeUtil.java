package com.geniusgithub.mediaplayer.dlna.util;

public class TimeUtil {
    public static String formateTime(long millis)
    {
        String str = "";
        int hour = 0;
        int time = (int) (millis / 1000);
        int second = time % 60;
        int minute = time / 60;
        if (minute >= 60){
            hour = minute / 60;
            minute %= 60;
            str = String.format("%02d:%02d:%02d", hour, minute, second);
        }else{
            str = String.format("%02d:%02d", minute, second);
        }


        return str;

    }
}
