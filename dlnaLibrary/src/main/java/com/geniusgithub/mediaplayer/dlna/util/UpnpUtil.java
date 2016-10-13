package com.geniusgithub.mediaplayer.dlna.util;

import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;

import org.cybergarage.upnp.Device;


public class UpnpUtil {


	public static boolean isMediaServerDevice(Device device){
		if ("urn:schemas-upnp-org:device:MediaServer:1".equalsIgnoreCase(device.getDeviceType())){
			return true;
		}
		return false;
	}
	
	
//	public static boolean isValidRenderDevice(Device device){
//		
//		if (UpnpUtil.isMediaRenderDevice(device) && !UpnpUtil.isLocalIpAddress(device)){
//			return true;
//		}
//		
//		return false;
//		
//	}
	
	
	public static boolean isMediaRenderDevice(Device device){
		if ("urn:schemas-upnp-org:device:MediaRenderer:1".equalsIgnoreCase(device.getDeviceType())){
			return true;
		}
		return false;
	}

	public static final String AUDIO_OBJECT = "object.item.audio";
	public static final String AUDIO_OBJECT1 = "object.item.music";
	public static boolean isAudioItem(MediaItem item){
		String objectClass = item.getObjectClass();
		return isAudioItem(objectClass);
	}
	public static boolean isAudioItem(String objectClass){
		if (isUPnPClassStartWith(objectClass, AUDIO_OBJECT) || isUPnPClassStartWith(objectClass, AUDIO_OBJECT1))
			return true;
		return false;
	}

	public static final String VIDEO_OBJECT = "object.item.movie";
	public static final String VIDEO_OBJECT1 = "object.item.video";
	public static boolean isVideoItem(MediaItem item){
		String objectClass = item.getObjectClass();
		return isVideoItem(objectClass);
	}
	public static boolean isVideoItem(String objectClass){
		if (isUPnPClassStartWith(objectClass, VIDEO_OBJECT) || isUPnPClassStartWith(objectClass, VIDEO_OBJECT1))
			return true;
		return false;
	}

	public static final String PHOTO_OBJECT = "object.item.image";
	public static final String PHOTO_OBJECT1 = "object.item.photo";
	public static boolean isPictureItem(MediaItem item){
		String objectClass = item.getObjectClass();
		return isPictureItem(objectClass);
	}
	public static boolean isPictureItem(String objectClass){
		if (isUPnPClassStartWith(objectClass, PHOTO_OBJECT) || isUPnPClassStartWith(objectClass, PHOTO_OBJECT1))
			return true;
		return false;
	}

	public static final String FOLDER_OBJECT = "object.container";
	public static boolean isFolderItem(MediaItem item){
		String objectClass = item.getObjectClass();
		return isFolderItem(objectClass);
	}
	public static boolean isFolderItem(String objectClass){
		if (isUPnPClassStartWith(objectClass, FOLDER_OBJECT))
			return true;
		return false;
	}

	public static boolean isUPnPClassStartWith(String objectClass, String targetClass)
	{
		if (targetClass == null)
			return false;
		if (objectClass == null)
			return false;
		return objectClass.startsWith(targetClass);
	}

	/*public static boolean isLocalIpAddress(Device device){
		try {
			String addrip = device.getLocation();
			addrip = addrip.substring("http://".length(),addrip.length());
			addrip = addrip.substring(0,addrip.indexOf(":"));
			boolean ret = isLocalIpAddress(addrip);
			ret = false;
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return false;
	}

	public static boolean isLocalIpAddress(String checkip) 
  	{  

  		boolean ret=false;
  		if(checkip != null)
  		{
  			try 
  			{  
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
                {  
                    NetworkInterface intf = en.nextElement();  
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
                    {
                        InetAddress inetAddress = enumIpAddr.nextElement();  
                        if (!inetAddress.isLoopbackAddress()) 
                        {
                      	  String ip = inetAddress.getHostAddress().toString();

                      	  if(ip == null)
                      	  {
                      		  continue;
                      	  }
                      	  if(checkip.equals(ip))
                      	  {
                      		  return true;
                      	  }
                        }
                    }
                }
            }
  			catch (SocketException ex) 
            {
          	  ex.printStackTrace();
            }
  		}
  		
  		return ret;
    } */
}
