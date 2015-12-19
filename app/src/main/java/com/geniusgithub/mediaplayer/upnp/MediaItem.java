package com.geniusgithub.mediaplayer.upnp;

public class MediaItem {
	
	
	public static class ResInfo {
		public String protocolInfo = "";
		public String resolution = "";
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
	public String childCount = "";
	public long date = 0;
	public ResInfo resInfo = new ResInfo();
	
	
	public MediaItem() {
		
	}
	
	public MediaItem(String stringid, String title, String artist, String album, String objectClass) {
		setStringid(stringid);
		setTitle(title);
		setArtist(artist);
		setAlbum(album);
		setObjectClass(objectClass);
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
							"childCount = " + childCount + "\n" + 
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
	
	public String getchildCount() {
		return childCount;
	}
	public void setchildCount(String childCount) {
		this.childCount = (childCount != null ? childCount : "");
	}
	
	public long getSize() {
		return resInfo.size;
	}
	public void setSize(long size) {
		resInfo.size = size;
	}
	
	public String getresolution() {
		return resInfo.resolution;
	}
	public void setresolution(String resolution) {
		resInfo.resolution = resolution != null ? resolution : "";
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

}
