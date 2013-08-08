package com.geniusgithub.mediaplayer.picture;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.cybergarage.util.CommonLog;

import com.geniusgithub.mediaplayer.util.FileHelper;
import com.geniusgithub.mediaplayer.util.LogFactory;


public class FileDownTask implements Runnable{

	private static final CommonLog log = LogFactory.createLog();
	private final static int MAX_REQUEST_COUNT = 3;
	private final static int CONNECT_TIME_OUT = 5000;
	
	public String requesetMethod = "GET";	
	public String requestUrl; 							
	public String saveUri; 		
	public int responsCode = 0;
	public boolean isDownloadSuccess = false;
	public DownLoadHelper.IDownLoadCallback callback;
	
	public FileDownTask(String requestUrl, String saveUri, DownLoadHelper.IDownLoadCallback callback){
		this.requestUrl = requestUrl;
		this.saveUri = saveUri;
		this.callback = callback;
	}
	
	@Override
	public void run() {

		boolean isParamValid = isParamValid();
		if(isParamValid){
			boolean ret = false;
			int count = 0;
			while(true){
				ret = request();
				if (ret || count > 2){			
					break;
				}
				count++;
				log.e("request fail,cur count = " + count);
			}			
		}else{
			log.e("isParamValid = false!!!");
		}
	
		if (callback != null){
			callback.downLoadComplete(isDownloadSuccess, saveUri);
		}
	}
	
	private boolean request(){
		
		InputStream inputStream = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod(requesetMethod);
			conn.setRequestProperty("Connection", "Keep-Alive"); 	
			conn.setConnectTimeout(CONNECT_TIME_OUT); 
			responsCode = conn.getResponseCode();
			if (responsCode != 200){
				log.e("responsCode = " + responsCode + ", so Fail!!!");
				return false;
			}

		    inputStream = conn.getInputStream();
			isDownloadSuccess  = FileHelper.writeFile(saveUri, inputStream);

			inputStream.close();
			return isDownloadSuccess;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.e("catch MalformedURLException e = " + e.getMessage());
		}catch (IOException e) {
			e.printStackTrace();
			log.e("catch IOException e = " + e.getMessage() + ", inputStream = " + inputStream);
		}
	
		
		return false;
	}

	public boolean isParamValid(){
		if (requestUrl == null || saveUri == null){
			return false;
		}
		
		return true;
	}
}
