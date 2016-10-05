package com.geniusgithub.mediaplayer.dlna.control.model;


import com.geniusgithub.mediaplayer.dlna.util.DlnaUtil;

import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.upnp.std.av.server.object.item.ResourceNode;

public class MediaItem implements MediaEntry {


	@Override
	public String getDataSource() {
		return getRes();
	}

	public static class ResInfo {
		public String protocolInfo = "";
		public long size = 0;
		public String res = "";
		public int duration = 0;
	}
	
	public String stringid = "";
	public String title = "";
	public String artist = "";
	public String album = "";
	public String objectClass = "";
	public String albumarturi = "";
	public long date = 0;
	public ResInfo resInfo = new ResInfo();
	
	
	public MediaItem() {
		
	}
	
	public String getShowString(){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("stringid = " + stringid+ "\n" + 
							"title = " + title+ "\n" + 
							"artist = " + artist + "\n" + 
							"album = " + album + "\n" + 						
							"objectClass = " + objectClass + "\n" + 
							"res = " + resInfo.res + "\n" + 
							"duration = " + resInfo.duration + "\n" + 
							"albumUri = " + albumarturi + "\n" +
							"date = " + date + "\n" + 
							"size = " + resInfo.size);
		
		return stringBuffer.toString();
	}
	
	public String getStringid() {
		return stringid;
	}
	public void setStringid(String stringid) {
		this.stringid = (stringid != null ? stringid : "");
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = (title != null ? title : "");
	}
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = (artist != null ? artist : "");
	}
	
	public void setAlbum(String album) {
		this.album = (album != null ? album : "");
	}
	public String getAlbum() {
		return album;
	}
	
	public void setObjectClass(String objectClass) {
		this.objectClass = (objectClass != null ? objectClass : "");
	}
	public String getObjectClass() {
		return objectClass;
	}

	public void setRes(String res) {
		resInfo.res = (res != null ? res : "");
	}
	public String getRes() {
		return resInfo.res;
	}
	
	public void setDuration(int duration) {
		resInfo.duration = duration;
	}
	public int getDuration() {
		return resInfo.duration;
	}
	

	public String getAlbumUri(){
		return albumarturi;
	}
	public void setAlbumUri(String albumUri){
		this.albumarturi = (albumUri != null ? albumUri : "");
	}

	
	public long getSize() {
		return resInfo.size;
	}
	public void setSize(long size) {
		resInfo.size = size;
	}

	public String getprotocolInfo() {
		return resInfo.protocolInfo;
	}
	public void setprotocolInfo(String protocolInfo) {
		resInfo.protocolInfo = protocolInfo != null ? protocolInfo : "";
	}
	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}


	public static class Builder{

		public static MediaItem create(ContentNode node){
			MediaItem item = null;

			if (node instanceof ContainerNode){
				item = create((ContainerNode) node);
			}else if (node instanceof ItemNode){
				item = create((ItemNode) node);
			}

			return item;
		}



		public static MediaItem create(ContainerNode node){
			MediaItem item = new MediaItem();

			item.setStringid(node.getID());
			item.setTitle(node.getTitle());
			item.setObjectClass(node.getUPnPClass());

			return item;
		}


		 public static MediaItem create(ItemNode node){
			 MediaItem item = new MediaItem();

			 item.setStringid(node.getID());
			 item.setTitle(node.getTitle());
			 item.setObjectClass(node.getUPnPClass());
			 item.setDate(node.getDateTime());

			 item.setAlbum(node.getAlbum());
			 item.setAlbumUri(node.getAlbumArtURI());
			 item.setArtist(node.getArtist());

			 ResourceNode resourceNode = node.getFirstResource();
			 if (resourceNode != null){
				 item.setRes(resourceNode.getURL());
				 item.setprotocolInfo(resourceNode.getProtocolInfo());
				 item.setDuration(DlnaUtil.formatDurationString(resourceNode.getDuration()));
				 item.setSize(DlnaUtil.formatSizeString(resourceNode.getSize()));
			 }

			return item;
		 }
	}
}
