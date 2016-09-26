/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniusgithub.mediaplayer.player.music.lrc;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.geniusgithub.common.util.AlwaysLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MusicUtils {
	
	private static final String TAG = MusicUtils.class.getSimpleName();
	public static int downprogress;

//	public static boolean isMusicLocal(String url) {
//		boolean result = true;
//		if (url != null && url.toLowerCase().startsWith("http://")) {
//			return false;
//		}
//		return result;
//	}
//
//	static protected Uri getContentURIForPath(String path) {
//		return Uri.fromFile(new File(path));
//	}
//
//	/*
//	 * Try to use String.format() as little as possible, because it creates a
//	 * new Formatter every time you call it, which is very inefficient. Reusing
//	 * an existing Formatter more than tripled the speed of makeTimeString().
//	 * This Formatter/StringBuilder are also used by makeAlbumSongsLabel()
//	 */
//	private static StringBuilder sFormatBuilder = new StringBuilder();
//	private static Formatter sFormatter = new Formatter(sFormatBuilder,
//			Locale.getDefault());
//	private static final Object[] sTimeArgs = new Object[5];
//
//	public static String makeTimeString(Context context, long secs) {
//		String durationformat = context
//				.getString(secs < 3600 ? R.string.durationformatshort
//						: R.string.durationformatlong);
//
//		/*
//		 * Provide multiple arguments so the format can be changed easily by
//		 * modifying the xml.
//		 */
//		sFormatBuilder.setLength(0);
//
//		final Object[] timeArgs = sTimeArgs;
//		timeArgs[0] = secs / 3600;
//		timeArgs[1] = secs / 60;
//		timeArgs[2] = (secs / 60) % 60;
//		timeArgs[3] = secs;
//		timeArgs[4] = secs % 60;
//
//		return sFormatter.format(durationformat, timeArgs).toString();
//	}
//
//
//	public static int[] shuffle(int[] input) {
//		if (input == null || input.length == 0)
//			throw new IllegalArgumentException(
//					"input can not be null or zero length");
//		int[] result = new int[input.length];
//		Random r = new Random();
//		int m = input.length;
//		int i = 0;
//		while (m > 0 && i < result.length) {
//			int pos = (r.nextInt() >>> 1) % m;
//			result[i] = input[pos];
//			input[pos] = input[--m];
//			i++;
//		}
//		return result;
//	}
//
//	public static int[] shuffle(int[] input, int position) {
//		int result[] = shuffle(input);
//		// find the position and swap it
//		for (int j = 0; j < result.length; j++) {
//			if (result[j] == position) {
//				int tmp = result[position];
//				result[position] = result[j];
//				result[j] = tmp;
//			}
//		}
//
//		return result;
//	}
//
//	public static int find(int[] source, int s) {
//		int pos = 0;
//		for (int i = 0; i < source.length; i++) {
//			if (source[i] == s) {
//				pos = i;
//				break;
//			}
//		}
//		// if we can not find the position,pos = 0;
//		return pos;
//	}
//
//	public static boolean isCanPlaying(String path, Context ctx) {
//		if (path.startsWith("http://")) {
//			return true;
//		}
//		MediaPlayer m = new MediaPlayer();
//		// m.reset();
//		try {
//			if (path.startsWith("content://")) {
//				m.setDataSource(ctx, Uri.parse(path));
//			} else {
//				m.setDataSource(path);
//			}
//			m.prepare();
//			return true;
//		} catch (IOException e) {
//			Log.v(TAG, "is can playing.");
//			return false;
//		} catch (IllegalArgumentException e) {
//			return false;
//		} catch (java.lang.RuntimeException e) {
//			return false;
//		} finally {
//			m.release();
//			m = null;
//		}
//	}

	private static final String LYRICS_DIR = "/geniusgithub/lyrics";

	public static String getLyricDir() {
		File file = Environment.getExternalStorageDirectory();
		if (file == null) {
			return null;
		}
		File f = new File(file.getAbsolutePath() + LYRICS_DIR);
		if (!f.exists()) {
			f.mkdirs();
		}

		return f.getAbsolutePath();
	}

	public static String createLyricName(String song, String artist) {
		StringBuilder sb = new StringBuilder();
		if (artist != null && artist.length() > 0
				&& !MediaStore.UNKNOWN_STRING.equals(artist)) {
			sb.append(artist);
			sb.append(" - ");
		}
		sb.append(song);
		sb.append(".lrc");
		return sb.toString();
	}

	public static String getLyricFile(String song, String artist) {
		String lyricDir = getLyricDir();
		AlwaysLog.i(TAG, "getLyricDir = " + getLyricDir());
		if (lyricDir == null) {
			return null;
		}
		return getLyricDir() + "/" + createLyricName(song, artist);
	}

	public static boolean saveFile(String filePath, InputStream inputStream)
			throws IOException {
		boolean result = false;
		if (filePath != null && inputStream != null) {
			Log.d(TAG, "filePath:" + filePath);
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			if (file.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(
						file.getAbsolutePath());
				byte[] buf = new byte[1024];
				int size = 0;
				while ((size = inputStream.read(buf, 0, 1024)) != -1) {
					fos.write(buf, 0, size);
				}

				fos.flush();
				fos.close();
				inputStream.close();
				result = true;
			}
		}
		return result;
	}

	public static boolean saveFile(String filePath, String str)
			throws IOException {
		boolean result = false;
		if (filePath != null && str != null) {
			Log.d(TAG, "filePath:" + filePath);
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			if (file.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(
						file.getAbsolutePath());
				fos.write(str.getBytes("gb18030"));
				fos.flush();
				fos.close();
				result = true;
			}
		}
		return result;
	}
//
//	public static String getExtensionName(String filename) {
//		if ((filename != null) && (filename.length() > 0)) {
//			int dot = filename.lastIndexOf('.');
//			if ((dot > -1) && (dot < (filename.length() - 1))) {
//				return filename.substring(dot + 1);
//			}
//		}
//		return filename;
//	}
//
//	public static String getFileNameNoEx(String filename) {
//		if ((filename != null) && (filename.length() > 0)) {
//			int dot = filename.lastIndexOf('.');
//			if ((dot > -1) && (dot < (filename.length()))) {
//				return filename.substring(0, dot);
//			}
//		}
//		return filename;
//	}
}
