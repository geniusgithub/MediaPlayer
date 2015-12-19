package com.geniusgithub.mediaplayer.activity;

import java.util.List;
import java.util.Stack;

import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

import com.geniusgithub.mediaplayer.upnp.MediaItem;


public class ContentManager {

	private static final CommonLog log = LogFactory.createLog();
	
	private static ContentManager mInstance = null;
	
	private Stack<List<MediaItem>> mStack;
	
	public synchronized static ContentManager getInstance(){
		if (mInstance == null){
			mInstance =  new ContentManager();
		}
		
		return mInstance;
	}
	
	private ContentManager()
	{
		mStack = new Stack<List<MediaItem>>();
	}
	
	public void pushListItem(List<MediaItem> dataList)
	{
		if (dataList != null){
	//		log.e("mStack.add data.size = " + dataList.size());
			mStack.add(dataList);
		}
	}
	
	public List<MediaItem> peekListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		return mStack.peek();
	}
	
	public List<MediaItem> popListItem()
	{
		if (mStack.empty()){
			return null;
		}
		
		return mStack.pop();
	}
	
	public void clear()
	{
		mStack.clear();
	}
	
	
}
