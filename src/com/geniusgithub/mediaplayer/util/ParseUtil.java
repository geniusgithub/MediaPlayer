package com.geniusgithub.mediaplayer.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cybergarage.upnp.Argument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.geniusgithub.mediaplayer.upnp.MediaItem;
import com.geniusgithub.mediaplayer.upnp.UpnpUtil;


import android.R.integer;
import android.util.Log;

public class ParseUtil {

	public static List<MediaItem> parseResult(Argument result) throws Exception{

		List<MediaItem> list = new ArrayList<MediaItem>();

		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = dfactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(result.getValue()
					.getBytes("UTF-8"));

			Document doc = documentBuilder.parse(is);

			NodeList containers = doc.getElementsByTagName("container");
			for (int j = 0; j < containers.getLength(); ++j) {
				Node container = containers.item(j);

				String childCountString = "";
				Node childcountNodes =  container.getAttributes().getNamedItem("childCount");
				if (childcountNodes != null){
					childCountString = childcountNodes.getNodeValue();
				}
				String title = "";
				String objectClass = "";
				int id = 0;
				String stringid = "";
				String date = "";
				NodeList childNodes = container.getChildNodes();
				for (int l = 0; l < childNodes.getLength(); ++l) {
					Node childNode = childNodes.item(l);

					if (childNode.getNodeName().equals("dc:title")) {
						title = childNode.getFirstChild().getNodeValue();
						// id = Integer.parseInt(container.getAttributes()
						// .getNamedItem("id").getNodeValue());
						stringid = container.getAttributes().getNamedItem("id")
								.getNodeValue();						
					
					} else if (childNode.getNodeName().equals("upnp:class")) {
						objectClass = childNode.getFirstChild().getNodeValue();
					}
					if (childNode.getNodeName().equals("dc:date")) {
						Node dateNode = childNode.getFirstChild();
						if (dateNode != null) {
							date = dateNode.getNodeValue();
						}
					}

				}
				// Item i = new Item(id, title, null, null, objectClass);
				MediaItem i = new MediaItem(stringid, title, null, null, objectClass);
				i.setchildCount(childCountString);
				i.setDate(formatTimeString(date));
				list.add(i);
			}

		
			
			NodeList items = doc.getElementsByTagName("item");
			for (int j = 0; j < items.getLength(); ++j) {
				Node item = items.item(j);
				// int id = 0;
				String stringid;
				String title = null;
				String artist = null;
				String album = null;
				String objectClass = null;
				String res = null;
				String duration = null;
				String albumUri = null;
				String size = null;
				String date = null;

				boolean testFlag = false;
				
				List<MediaItem.ResInfo> resLists = new ArrayList<MediaItem.ResInfo>();
//				boolean resRead = false;
				// id =
				// Integer.parseInt(item.getAttributes().getNamedItem("id").getNodeValue());
				stringid = item.getAttributes().getNamedItem("id")
						.getNodeValue();
				NodeList childNodes = item.getChildNodes();
				for (int l = 0; l < childNodes.getLength(); ++l) {
					Node childNode = childNodes.item(l);

					if (childNode.getNodeName().equals("dc:title")) {
						title = childNode.getFirstChild().getNodeValue();

						if (title.equalsIgnoreCase("Return of the Jedi.2")){
							testFlag = true;
						}
					} else if (childNode.getNodeName().equals("dc:date")) {
						Node dateNode = childNode.getFirstChild();
						if (dateNode != null) {
							date = dateNode.getNodeValue();
						}
					} else if (childNode.getNodeName().equals("upnp:artist")) {
						artist = childNode.getFirstChild().getNodeValue();
					} else if (childNode.getNodeName().equals("upnp:album")) {
						album = childNode.getFirstChild().getNodeValue();
					} else if (childNode.getNodeName().equals("upnp:class")) {
						objectClass = childNode.getFirstChild().getNodeValue();
					} else if (childNode.getNodeName().equals("res")) {
						
//						if (!resRead){
//							res = childNode.getFirstChild().getNodeValue();
//							if (childNode.getAttributes().getNamedItem(
//									"duration") != null) {
//								duration = childNode.getAttributes()
//										.getNamedItem("duration")
//										.getNodeValue();
//							}
//							Node SizeNode = childNode.getAttributes().getNamedItem(
//									"size");
//							if (SizeNode != null) {
//								size = SizeNode.getNodeValue();							
//							}
//							resRead = true;
//						}
						
				
						if (testFlag){
							Log.e("", "Return of the Jedi.2 is true, ready!!!");
						}
						MediaItem.ResInfo resInfo = getResInfo(childNode);
						if (resInfo != null){
							resLists.add(resInfo);
						}
						
						
						
					} else if (childNode.getNodeName().equals(
							"upnp:albumArtURI")
							|| childNode.getNodeName().equals("upnp:icon")) {
						albumUri = childNode.getFirstChild().getNodeValue();
					}

				}
				
				
				MediaItem i = new MediaItem(stringid, title, artist, album, objectClass);
				
				
					
				MediaItem.ResInfo resInfo = getBestResInfo(objectClass, resLists);
				if (resInfo != null){
					i.resInfo = resInfo;
				}
				
				if (testFlag){
					Log.e("", "Return of the Jedi.2 = \n" + i.getShowString());
				}
					
		
			//	i.setRes(res);
			//	i.setDuration(formatDurationString(duration));
				i.setAlbumUri(albumUri);
			//	i.setSize(formatSizeString(size));
				i.setDate(formatTimeString(date));

				// if ("object.item.audioItem.musicTrack".equals(objectClass) ||
				// "object.item.videoItem".equals(objectClass) ||
				// "object.item.imageItem.photo".equals(objectClass)) {
				// i.setRes(res);
				// i.setDuration(duration);
				// }
				list.add(i);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	
	public static MediaItem.ResInfo getResInfo(Node node){
		if (node == null || !node.getNodeName().equals("res")){
			return null;
		}
		
		MediaItem.ResInfo resInfo = new MediaItem.ResInfo();
		
		String res = node.getFirstChild().getNodeValue();
		if (res != null){
			resInfo.res = res;
		}
		
		resInfo.duration = formatDurationString(getAttributeByname(node, "duration"));
		resInfo.size = formatSizeString(getAttributeByname(node, "size"));	
		resInfo.protocolInfo = getAttributeByname(node, "protocolInfo");
		resInfo.resolution =  getAttributeByname(node, "resolution");
		
		return resInfo;
	}
	
	public static String getAttributeByname(Node node, String name){
		String value = "";
		Node attribute = node.getAttributes().getNamedItem(name);
		if (attribute != null){
			value = attribute.getNodeValue();
			if (value == null){
				value = "";
			}
		}
				
		return value;
	}
	
	public static MediaItem.ResInfo getBestResInfo(String objectClass, List<MediaItem.ResInfo> list){
		
		if (objectClass == null || list == null || list.size() == 0){
			return null;
		}
		
		if (!objectClass.contains(UpnpUtil.DLNA_OBJECTCLASS_PHOTOID)){
			return list.get(0);
		}
		
		int maxIndex = 0;
		int size = list.size();
		for(int i = 1; i < size; i++){
			boolean ret = compareBetweenResolution(list.get(maxIndex).resolution, list.get(i).resolution);
			if (!ret){
				maxIndex = i;
			}
		}

		return list.get(maxIndex);
	}
	
	public static boolean compareBetweenResolution(String resolution1, String resolution2){
	
		int resolutionInt1 = formatResolution(resolution1);
		int resolutionInt2 = formatResolution(resolution2);
		
		return resolutionInt1 >= resolutionInt2 ? true : false;
	}
	
	
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

	// 2000-2-3
	public static long formatTimeString(String timeString) {
		long time = 0;
		if (timeString == null || timeString.length() == 0){
			return time;
		}

		try {
			String[] array = timeString.split(":");
			int year = Integer.valueOf(array[0]);
			int month = Integer.valueOf(array[1]);
			int day = Integer.valueOf(array[2]);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			time = calendar.getTimeInMillis();
		} catch (Exception e) {
			time = 0;
		}


		return time;
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

}

