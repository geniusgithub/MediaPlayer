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

	public static boolean isAudioItem(MediaItem item){
		String objectClass = item.getObjectClass();
		if (isUPnPClassStartWith(objectClass, "object.item.audio") || isUPnPClassStartWith(objectClass, "object.item.music"))
			return true;
		return false;
	}



	public static boolean isVideoItem(MediaItem item){
		String objectClass = item.getObjectClass();
		if (isUPnPClassStartWith(objectClass, "object.item.movie") || isUPnPClassStartWith(objectClass, "object.item.video"))
			return true;
		return false;
	}
	
	public static boolean isPictureItem(MediaItem item){
		String objectClass = item.getObjectClass();
		if (isUPnPClassStartWith(objectClass, "object.item.image") || isUPnPClassStartWith(objectClass, "object.item.photo"))
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
