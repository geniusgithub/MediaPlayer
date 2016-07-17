package com.geniusgithub.mediaplayer.player.music.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geniusgithub.mediaplayer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;



public class LyricView extends View {
	private final static String TAG = "LyricView";
	private TreeMap<Integer, LyricObject> lrc_map = new TreeMap<Integer, LyricObject>();
	private float mX; // 屏幕X轴的中点，此值固定，保持歌词在X中间显示
	private float offsetY; // 歌词在Y轴上的偏移量，此值会根据歌词的滚动变小
	private boolean blLrc = false;
	private float touchY; // 当触摸歌词View时，保存为当前触点的Y轴坐标
	private int lrcIndex = 0; // 保存歌词TreeMap的下标
	private int SIZEWORD = 22;// 显示歌词文字的大小值
	private int SIZEWORDHL = 27;// 显示歌词文字的大小值
	public static final int INTERVAL = 15;// 歌词每行的间隔
	Paint paint = new Paint();// 画笔，用于画不是高亮的歌词
	Paint paintHL = new Paint(); // 画笔，用于画高亮的歌词，即当前唱到这句歌词
	private String title = "";
	private String artist = "";

	// private Typeface mTypeface;

	public LyricView(Context context) {
		super(context);
		init();
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (blLrc) {
			LyricObject temp = lrc_map.get(lrcIndex);
			if (temp.lrc != null) {
				canvas.drawText(temp.lrc, mX, offsetY + (SIZEWORD + INTERVAL)
						* lrcIndex, paintHL);
			}

			// 画当前歌词之前的歌词
			for (int i = lrcIndex - 1; i >= 0; i--) {
				temp = lrc_map.get(i);
				if (offsetY + (SIZEWORD + INTERVAL) * i < 0) {
					break;
				}
				if (temp.lrc != null) {
					canvas.drawText(temp.lrc, mX, offsetY
							+ (SIZEWORD + INTERVAL) * i, paint);
				}
			}
			// 画当前歌词之后的歌词
			for (int i = lrcIndex + 1; i < lrc_map.size(); i++) {
				temp = lrc_map.get(i);
				if (offsetY + (SIZEWORD + INTERVAL) * i > 600) {
					break;
				}
				if (temp.lrc != null) {
					canvas.drawText(temp.lrc, mX, offsetY
							+ (SIZEWORD + INTERVAL) * i, paint);
				}
			}
		} else {
			paint.setTextSize(SIZEWORD);
			canvas.drawText(title, mX, 220, paint);
			String ar = "";
			if (artist != null && artist.length() > 0
					&& !MediaStore.UNKNOWN_STRING.equals(artist)) {
				ar = artist;
			} else {
				ar = getResources().getString(R.string.mp_unknown_artist);
			}
			canvas.drawText(ar, mX, 260, paint);
			canvas.drawText(
					getResources().getString(R.string.mp_cant_find_lyrics), mX,
					310, paint);
		}
		super.onDraw(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float tt = event.getY();
//		if (!blLrc) {
//			return super.onTouchEvent(event);
//		}
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			break;
//		case MotionEvent.ACTION_MOVE:
//			touchY = tt - touchY;
//			offsetY = offsetY + touchY;
//			break;
//		case MotionEvent.ACTION_UP:
//			break;
//		}
//		touchY = tt;
//		return true;
//	}

	public void init() {
		offsetY = 320;
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(getResources().getColor(R.color.lyrics));
		paint.setTextSize(SIZEWORD);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setAlpha(180);

		paintHL = new Paint();
		paintHL.setTextAlign(Paint.Align.CENTER);

		paintHL.setColor(getResources().getColor(R.color.lyrics_hl));
		paintHL.setTextSize(SIZEWORDHL);
		paintHL.setAntiAlias(true);
		paintHL.setAlpha(255);
	}

	/**
	 * 根据歌词里面最长的那句来确定歌词字体的大小
	 */

	public void SetTextSize() {
		// if (!blLrc) {
		// return;
		// }
		// int max = lrc_map.get(0).lrc.length();
		// for (int i = 1; i < lrc_map.size(); i++) {
		// LyricObject lrcStrLength = lrc_map.get(i);
		// if (max < lrcStrLength.lrc.length()) {
		// max = lrcStrLength.lrc.length();
		// }
		// }
		// SIZEWORD = 320 / max;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mX = w * 0.5f;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 歌词滚动的速度
	 * 
	 * @return 返回歌词滚动的速度
	 */
	public Float speedLrc() {
		float speed = 0;
		if (offsetY + (SIZEWORD + INTERVAL) * lrcIndex > 220) {
			speed = ((offsetY + (SIZEWORD + INTERVAL) * lrcIndex - 220) / 20);
		} else if (offsetY + (SIZEWORD + INTERVAL) * lrcIndex < 120) {
			Log.i("speed", "speed is too fast!!!");
			speed = 0;
		}
		// if(speed<0.2){
		// speed=0.2f;
		// }
		return speed;
	}

	/**
	 * 按当前的歌曲的播放时间，从歌词里面获得那一句
	 * 
	 * @param time
	 *            当前歌曲的播放时间
	 * @return 返回当前歌词的索引值
	 */
	public int selectIndex(int time) {
		if (!blLrc) {
			return 0;
		}
		int index = 0;
		for (int i = 0; i < lrc_map.size(); i++) {
			LyricObject temp = lrc_map.get(i);
			if (temp.begintime < time) {
				++index;
			}
		}
		lrcIndex = index - 1;
		if (lrcIndex < 0) {
			lrcIndex = 0;
		}
		return lrcIndex;

	}

	/**
	 * 读取歌词文件
	 * 
	 * @param file
	 *            歌词的路径
	 * 
	 */
	public void read(String song, String artist) {
		this.title = song;
		this.artist = artist;
		TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();
		String data = "";
		String lyricFile = MusicUtils.getLyricFile(song, artist);
		if (lyricFile != null) {
			File saveFile = new File(lyricFile);
			if (saveFile.isFile()) {
				blLrc = true;
				try {
					FileInputStream stream = new FileInputStream(saveFile);// context.openFileInput(file);

					BufferedReader br = new BufferedReader(
							new InputStreamReader(stream, "gb18030"));
					Pattern pattern = Pattern.compile("\\d{2}");
					while ((data = br.readLine()) != null) {
						if (data.startsWith("[ti")) {
							String title = data.substring(4, data.length() - 1);
							LyricObject item1 = new LyricObject();
							item1.begintime = 0;
							item1.lrc = title;
							lrc_read.put(0, item1);
							continue;
						} else if (data.startsWith("[ar")) {
							String ar = data.substring(4, data.length() - 1);
							LyricObject item1 = new LyricObject();
							item1.begintime = 1;
							item1.lrc = ar;
							lrc_read.put(1, item1);
							continue;
						} else if (data.startsWith("[al")) {
							String album = data.substring(4, data.length() - 1);
							LyricObject item1 = new LyricObject();
							item1.begintime = 2;
							item1.lrc = album;
							lrc_read.put(2, item1);

							LyricObject tips = new LyricObject();
							tips.begintime = 3;
							tips.lrc = getContext().getString(
									R.string.mp_lyrics_tips);
							lrc_read.put(3, tips);
							continue;
						}
						data = data.replace("[", "");// 将前面的替换成后面的
						data = data.replace("]", "@");
						String splitdata[] = data.split("@");// 分隔
						if (data.endsWith("@")) {
							for (int k = 0; k < splitdata.length; k++) {
								String str = splitdata[k];
								str = str.replace(":", ".");
								str = str.replace(".", "@");
								String timedata[] = str.split("@");
								Matcher matcher = pattern.matcher(timedata[0]);
								if (timedata.length == 3 && matcher.matches()) {
									int m = Integer.parseInt(timedata[0]); // 分
									int s = Integer.parseInt(timedata[1]); // 秒
									int ms = Integer.parseInt(timedata[2]); // 毫秒
									int currTime = (m * 60 + s) * 1000 + ms
											* 10;
									if (currTime == 0) {
										currTime = 10;
									}
									LyricObject item1 = new LyricObject();
									item1.begintime = currTime;
									item1.lrc = "";
									lrc_read.put(currTime, item1);
								}
							}
						} else {
							String lrcContenet = splitdata[splitdata.length - 1];
							for (int j = 0; j < splitdata.length - 1; j++) {
								String tmpstr = splitdata[j];
								tmpstr = tmpstr.replace(":", ".");
								tmpstr = tmpstr.replace(".", "@");
								String timedata[] = tmpstr.split("@");
								Matcher matcher = pattern.matcher(timedata[0]);
								if (timedata.length == 3 && matcher.matches()) {
									int m = Integer.parseInt(timedata[0]); // 分
									int s = Integer.parseInt(timedata[1]); // 秒
									int ms = Integer.parseInt(timedata[2]); // 毫秒
									int currTime = (m * 60 + s) * 1000 + ms
											* 10;
									if (currTime == 0) {
										currTime = 20;
									}
									LyricObject item1 = new LyricObject();
									item1.begintime = currTime;
									item1.lrc = lrcContenet;
									lrc_read.put(currTime, item1);
								}
							}
						}
					}
					stream.close();
				} catch (IOException e) {
					Log.e(TAG, "Lyric IOException", e);
				}
				/*
				 * 遍历hashmap 计算每句歌词所需要的时间
				 */
				lrc_map.clear();
				data = "";
				Iterator<Integer> iterator = lrc_read.keySet().iterator();
				LyricObject oldval = null;
				int i = 0;
				while (iterator.hasNext()) {
					Object ob = iterator.next();

					LyricObject val = lrc_read.get(ob);

					if (oldval == null) {
						oldval = val;
					} else {
						LyricObject item1 = new LyricObject();
						item1 = oldval;
						item1.timeline = val.begintime - oldval.begintime;
						lrc_map.put(Integer.valueOf(i), item1);
						i++;
						oldval = val;
					}
					if (!iterator.hasNext()) {
						lrc_map.put(Integer.valueOf(i), val);
					}
				}
			} else {
				blLrc = false;
			}
		} else {
			blLrc = false;
		}

	}

	/**
	 * @return the blLrc
	 */
	public boolean isBlLrc() {
		return blLrc;
	}

	/**
	 * @return the offsetY
	 */
	public float getOffsetY() {
		return offsetY;
	}

	/**
	 * @param offsetY
	 *            the offsetY to set
	 */
	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * @return 返回歌词文字的大小
	 */
	public int getSIZEWORD() {
		return SIZEWORD;
	}

	/**
	 * 设置歌词文字的大小
	 * 
	 * @param sIZEWORD
	 *            the sIZEWORD to set
	 */
	public void setSIZEWORD(int sIZEWORD) {
		SIZEWORD = sIZEWORD;
	}
}
