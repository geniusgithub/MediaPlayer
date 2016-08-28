package com.geniusgithub.common.util;

import java.io.File;

public class FileHelper {

	private static final String TAG = FileHelper.class.getSimpleName();

	private static final int FILE_BUFFER_SIZE = 51200;
	
	
	public static boolean fileIsExist(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			AlwaysLog.e(TAG, "param invalid, filePath: " + filePath);
			return false;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	public static boolean createDirectory(String filePath){
		if (null == filePath) {
			return false;
		}

		File file = new File(filePath);

		if (file.exists()){
			return true;
		}

		return file.mkdirs();

	}

	public static boolean deleteDirectory(String filePath) {
		if (null == filePath) {
			AlwaysLog.e(TAG, "Invalid param. filePath: " + filePath);
			return false;
		}

		File file = new File(filePath);

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			File[] list = file.listFiles();

			for (int i = 0; i < list.length; i++) {
				AlwaysLog.d(TAG, "delete filePath: " + list[i].getAbsolutePath());
				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}

		AlwaysLog.d(TAG, "delete filePath: " + file.getAbsolutePath());
		file.delete();
		return true;
	}

}