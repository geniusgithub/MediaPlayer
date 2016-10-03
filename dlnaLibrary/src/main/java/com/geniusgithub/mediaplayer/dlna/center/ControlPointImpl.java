package com.geniusgithub.mediaplayer.dlna.center;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.ssdp.SSDPNotifySocket;
import org.cybergarage.upnp.ssdp.SSDPNotifySocketList;
import org.cybergarage.upnp.ssdp.SSDPSearchResponseSocket;
import org.cybergarage.upnp.ssdp.SSDPSearchResponseSocketList;
import org.cybergarage.util.AlwaysLog;

import java.net.DatagramSocket;
import java.net.MulticastSocket;

public class ControlPointImpl extends ControlPoint {

    private final static String TAG = ControlPointImpl.class.getSimpleName();

    private String localAddress = "";
    private DatagramSocket searchSocket;
    private MulticastSocket notifySocket;

    public ControlPointImpl(){
        super();
    }

    public String getLocalAddress(){
        return localAddress;
    }

    public boolean flushLocalAddress(){
        flushSSDPNotifySocket();
        flushSearchResponseSocket();
        printSocketInfo();
        if (searchSocket != null){
            localAddress = searchSocket.getLocalAddress().toString();
        }else{
            localAddress = "";
        }
        return  true;
    }

    private boolean flushSearchResponseSocket(){

        SSDPSearchResponseSocketList searchResponseSocketList = getSSDPSearchResponseSocketList();
        int size = searchResponseSocketList.size();
        AlwaysLog.i(TAG, "flushSearchResponseSocket size = " + size);
        if (size >= 1){
            SSDPSearchResponseSocket socket = (SSDPSearchResponseSocket) searchResponseSocketList.get(0);
            searchSocket = socket.getDatagramSocket();
        }else{
            searchSocket = null;
        }

        return true;
    }


    private boolean flushSSDPNotifySocket(){

        SSDPNotifySocketList ssdpNotifySocketList = getSSDPNotifySocketList();
        int size = ssdpNotifySocketList.size();
        AlwaysLog.i(TAG, "flushSSDPNotifySocket size = " + size);
        if (size >= 1){
            SSDPNotifySocket socket = (SSDPNotifySocket) ssdpNotifySocketList.get(0);
            notifySocket = socket.getSocket();
        }else{
            notifySocket = null;
        }

        return true;
    }


    private void printSocketInfo(){
        if (searchSocket != null){
            AlwaysLog.i(TAG, "searchSocket  address = " + searchSocket.getLocalSocketAddress().toString());
        }else{
            AlwaysLog.i(TAG, "searchSocket = null");
        }

        if (notifySocket != null){
            AlwaysLog.i(TAG, "notifySocket  address  = " +  notifySocket.getLocalSocketAddress().toString());
        }else{
            AlwaysLog.i(TAG, "notifySocket = null");
        }
    }
}
