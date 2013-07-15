package com.geniusgithub.mediaplayer.util;

public class TalentTimeUtil {

	public static String formateTime(long millis)
	{
		String str = "";
		
		int time = (int) (millis / 1000);
		int second = time % 60;
		int minute = time / 60;
		
		str = String.format("%02d:%02d", minute, second);
		
		return str;
		
	}
	
	public static int getDuration(String duration)
	{
		int durations = 0;
		try {
			String sArray[] = duration.split(":");
			int hour = Integer.valueOf(sArray[0]);
			int minute = Integer.valueOf(sArray[1]);
			int second = Integer.valueOf(sArray[2]);
			
			return ((hour * 60 + minute) * 60 + second) * 1000;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return durations;
		
	}
	
}
