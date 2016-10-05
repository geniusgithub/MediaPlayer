package com.geniusgithub.mediaplayer.component;

import android.content.Intent;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

public class MediaItemFactory {

	public  static void putItemToIntent(MediaItem item, Intent intent){
		intent.putExtra("title", item.getTitle());
		intent.putExtra("artist", item.getArtist());
		intent.putExtra("album", item.getAlbum());
		intent.putExtra("stringid", item.getStringid());
		intent.putExtra("objectClass", item.getObjectClass());
		intent.putExtra("res", item.getRes());
		intent.putExtra("duration", item.getDuration());
		intent.putExtra("date", item.getDate());
		intent.putExtra("size", item.getSize());
		intent.putExtra("albumarturi", item.getAlbumUri());
		intent.putExtra("protocolInfo", item.getprotocolInfo());
	}
	
	public static MediaItem getItemFromIntent( Intent intent){
		MediaItem item = new MediaItem();
		item.setTitle(intent.getStringExtra("title"));
		item.setArtist(intent.getStringExtra("artist"));
		item.setAlbum(intent.getStringExtra("album"));
		item.setStringid(intent.getStringExtra("stringid"));
		item.setObjectClass(intent.getStringExtra("objectClass"));
		item.setRes(intent.getStringExtra("res"));
		item.setDuration(intent.getIntExtra("duration", 0));
		item.setDate(intent.getLongExtra("date", 0));
		item.setSize(intent.getLongExtra("size", 0));
		item.setAlbumUri(intent.getStringExtra("albumarturi"));
		item.setprotocolInfo(intent.getStringExtra("protocolInfo"));
		return item;
	}
	
}
