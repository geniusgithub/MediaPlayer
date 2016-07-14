/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002-2003
*
*	File: HTTPServerList.java
*
*	Revision;
*
*	05/11/03
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.ssdp;

import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.AlwaysLog;

import java.net.InetAddress;
import java.util.Vector;

public class SSDPNotifySocketList extends Vector 
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////

	// add by geniusgithub begin
	private final static String TAG = SSDPNotifySocketList.class.getSimpleName();
	// add by geniusgithub end

	private InetAddress[] binds = null;

	public SSDPNotifySocketList() {
	}
	
	/**
	 * 
	 * @param binds The host to bind the service <tt>null</tt> means to bind to default.
	 * @since 1.8
	 */
	public SSDPNotifySocketList(InetAddress[] binds){
		this.binds=binds;
	}

	////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////
	
	public SSDPNotifySocket getSSDPNotifySocket(int n)
	{
		return (SSDPNotifySocket)get(n);
	}

	////////////////////////////////////////////////
	//	ControlPoint
	////////////////////////////////////////////////

	public void setControlPoint(ControlPoint ctrlPoint)
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPNotifySocket sock = getSSDPNotifySocket(n);
			sock.setControlPoint(ctrlPoint);
		}
	}

	////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////

	// add by geniusgithub begin
	public boolean isValidAddress(String address){
		if (address == null || address.length() < 1){
			return false;
		}
		
		int pos = address.indexOf(':');
		if (pos == -1){
			return true;
		}
		
		return false;
	}
	// add by geniusgithub end
	
	public boolean open(){
		InetAddress[] binds=this.binds ;
		String[] bindAddresses;
		if(binds!=null){			
			bindAddresses = new String[binds.length];
			for (int i = 0; i < binds.length; i++) {
				bindAddresses[i] = binds[i].getHostAddress();
			}
		}else{
			int nHostAddrs = HostInterface.getNHostAddresses();
			bindAddresses = new String[nHostAddrs]; 
			for (int n=0; n<nHostAddrs; n++) {
				bindAddresses[n] = HostInterface.getHostAddress(n);
			}
		}
		// add by geiusgithub begin
		boolean flag = false;
		// add by geiusgithub begin
		for (int i = 0; i < bindAddresses.length; i++) {

			// modify by geiusgithub begin
/*			if(bindAddresses[i]!=null) {
				SSDPNotifySocket ssdpNotifySocket = new SSDPNotifySocket(bindAddresses[i]);
				add(ssdpNotifySocket);
			}*/

			if (!isValidAddress(bindAddresses[i])){
				AlwaysLog.e(TAG, "ready to create SSDPNotifySocket bindAddresses = " + bindAddresses[i]+ ", it's invalid so drop it!!!" );
				continue;
			}

			if(bindAddresses[i]!=null) {
				SSDPNotifySocket ssdpNotifySocket = new SSDPNotifySocket(bindAddresses[i]);
				if (ssdpNotifySocket.getSocket() == null){
					AlwaysLog.e(TAG, "ssdpNotifySocket.getSocket() == null!!!");
					continue;
				}
				AlwaysLog.i(TAG, "ssdpNotifySocket create success!!!bindAddresses = " + bindAddresses[i]);
				add(ssdpNotifySocket);
				flag = true;
			}
			// modify by geiusgithub end
		}
		return true;
	}
	
	public void close()
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPNotifySocket sock = getSSDPNotifySocket(n);
			sock.close();
		}
		clear();
	}
	
	////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////
	
	public void start()
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPNotifySocket sock = getSSDPNotifySocket(n);
			sock.start();
		}
	}

	public void stop()
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPNotifySocket sock = getSSDPNotifySocket(n);
			sock.stop();
		}
	}
	
}

