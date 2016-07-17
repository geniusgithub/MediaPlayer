package com.geniusgithub.mediaplayer.player.music.lrc;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.provider.MediaStore;
import android.util.Log;


public class LyricHelper {
	private static final String TAG = "LyricHelper";
	private static final String DEFAULT_ENCODING = "gb18030";
	private static final String QQ_SEARCH_BASE_URL = "http://qqmusic.qq.com/fcgi-bin/qm_getLyricId.fcg";
	private static final String QQ_LYRIC_BASE_URL = "http://music.qq.com/miniportal/static/lyric/";
	private static final String BAIDU_SEARCH_BASE_URL = "http://box.zhangmen.baidu.com/x?op=12&count=1";
	private static final String BAIDU_LYRIC_BASE_URL = "http://box.zhangmen.baidu.com/bdlrc/";

	public static boolean searchLryics(String song, String artist) {
		boolean result = false;
		String lyricPath = MusicUtils.getLyricFile(song, artist);
		if (lyricPath != null) {
			String songId = searchLyricFromQQ(song, artist);
			if (songId != null) {
				String url = buildQQLyricUrl(songId);
				Log.d(TAG, "search url:" + url);
				try {
					HttpResponse response = HttpManager
							.execute(new HttpGet(url));
					if (response != null
							&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream is = response.getEntity().getContent();
						XmlPullParserFactory factory = XmlPullParserFactory
								.newInstance();
						factory.setNamespaceAware(true);
						XmlPullParser xpp = factory.newPullParser();
						xpp.setInput(is, DEFAULT_ENCODING);
						int eventType = xpp.getEventType();
						String tag = "";
						while (eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_TAG) {
								tag = xpp.getName();
								if (tag.equals("lyric")) {
									xpp.next();
									String lycis = xpp.getText();
									result = MusicUtils.saveFile(lyricPath,
											lycis);
									break;
								}
							}
							eventType = xpp.next();
						}
					}
				} catch (Exception e1) {
					Log.e(TAG, "Exception", e1);
				}
			}
			if (!result) { // Get lyric failed from QQ
				String lyricId = searchLyricFromBaidu(song, artist);
				if (lyricId != null) {
					String url = buildBaiduLyricUrl(lyricId);
					Log.d(TAG, "search url:" + url);
					try {
						HttpResponse response = HttpManager
								.execute(new HttpGet(url));
						if (response != null
								&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							InputStream is = response.getEntity().getContent();
							if (is != null) {
								result = MusicUtils.saveFile(lyricPath, is);
							}
						}
					} catch (Exception e1) {
						Log.e(TAG, "Exception", e1);
					}
				}
			}
		}
		return result;
	}

	private static String searchLyricFromQQ(String song, String artist) {
		String lyricID = null;
		String url = buildQQSearchUrl(song, artist);
		Log.d(TAG, "search url:" + url);
		try {
			HttpResponse response = HttpManager.execute(new HttpGet(url));
			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(is, DEFAULT_ENCODING);
				int eventType = xpp.getEventType();
				String tag = "";
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						tag = xpp.getName();
						if (tag.equals("songcount")) {
							xpp.next();
							int count = Integer.parseInt(xpp.getText());
							Log.i(TAG, "search count:" + count);
							if (count == 0) {
								break;
							}
						} else if (tag.equals("songinfo")) {
							final int attrSize = xpp.getAttributeCount();
							for (int i = 0; i < attrSize; i++) {
								String attrName = xpp.getAttributeName(i);
								if (attrName.equalsIgnoreCase("id")) {
									lyricID = xpp.getAttributeValue(i);
									break;
								}
							}
						}
					}
					eventType = xpp.next();
				}
			}
		} catch (Exception e1) {
			Log.e(TAG, "Exception", e1);
		}

		return lyricID;
	}

	private static String buildBaiduSearchUrl(String song, String artist) {
		if (artist != null && artist.length() > 0
				&& !artist.equals(MediaStore.UNKNOWN_STRING)) {
			StringBuilder sb = new StringBuilder();
			sb.append(BAIDU_SEARCH_BASE_URL);
			sb.append("&title=");
			try {
				sb.append(URLEncoder.encode(song, "gbk"));
				sb.append("$$");
				sb.append(URLEncoder.encode(artist, "gbk"));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "UnsupportedEncodingException", e);
			}
			sb.append("$$$$");
			return sb.toString();
		}
		return null;

	}

	private static String buildBaiduLyricUrl(String lyricId) {
		int id = -1;
		if (lyricId != null) {
			try {
				id = Integer.parseInt(lyricId);
			} catch (Exception e) {
			}
		}
		if (id > -1) {
			StringBuilder sb = new StringBuilder();
			sb.append(BAIDU_LYRIC_BASE_URL);
			int tmp = id / 100;
			sb.append(tmp);
			sb.append("/");
			sb.append(lyricId);
			sb.append(".lrc");

			return sb.toString();
		}
		return null;
	}

	private static String searchLyricFromBaidu(String song, String artist) {
		String lyricID = null;
		String url = buildBaiduSearchUrl(song, artist);
		Log.d(TAG, "search url:" + url);
		if (url != null) {
			try {
				HttpResponse response = HttpManager.execute(new HttpGet(url));
				if (response != null
						&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					InputStream is = response.getEntity().getContent();
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(is, DEFAULT_ENCODING);
					int eventType = xpp.getEventType();
					String tag = "";
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							tag = xpp.getName();
							if (tag.equals("count")) {
								xpp.next();
								int count = Integer.parseInt(xpp.getText());
								Log.i(TAG, "search count:" + count);
								if (count == 0) {
									break;
								}
							} else if (tag.equals("lrcid")) {
								xpp.next();
								lyricID = xpp.getText();
								break;
							}
						}
						eventType = xpp.next();
					}
				}
			} catch (Exception e1) {
				Log.e(TAG, "Exception", e1);
			}
		}
		return lyricID;
	}

	private static String buildQQSearchUrl(String song, String artist) {
		StringBuilder sb = new StringBuilder();
		sb.append(QQ_SEARCH_BASE_URL);
		sb.append("?name=");
		try {
			sb.append(URLEncoder.encode(song, "gb18030"));
			if (artist != null && artist.length() > 0
					&& !artist.equals(MediaStore.UNKNOWN_STRING)) {
				sb.append("&singer=");
				sb.append(URLEncoder.encode(artist, "gb18030"));
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException", e);
		}
		sb.append("&from=qqplayer");
		return sb.toString();
	}

	private static String buildQQLyricUrl(String songId) {
		StringBuilder sb = new StringBuilder();
		sb.append(QQ_LYRIC_BASE_URL);
		int length = songId.length();
		String tmp = songId.substring(length - 2);
		sb.append(tmp);
		sb.append("/");
		sb.append(songId);
		sb.append(".xml");

		return sb.toString();
	}
}
