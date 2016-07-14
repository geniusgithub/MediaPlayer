/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002-2003
*
*	File: SSDPSearchResponseSocketList.java
*
*	Revision;
*
*	05/08/03
*		- first revision.
*	05/28/03
*		- Added post() to send a SSDPSearchRequest.
*
******************************************************************/

package org.cybergarage.upnp.ssdp;

import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.util.AlwaysLog;

import java.net.InetAddress;
import java.util.Vector;

public class SSDPSearchResponseSocketList extends Vector 
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////
	// add by geniusgithub begin
	private final static String TAG = SSDPSearchResponseSocketList.class.getSimpleName();
	// add by geniusgithub end
	private InetAddress[] binds = null;
	
	public SSDPSearchResponseSocketList() {
	}
	/**
	 * 
	 * @param binds The host to bind.Use <tt>null</tt> for the default behavior
	 */
	public SSDPSearchResponseSocketList(InetAddress[] binds) {
		this.binds = binds;
	}

	
	
	////////////////////////////////////////////////
	//	ControlPoint

	////////////////////////////////////////////////
	//	ControlPoint
	////////////////////////////////////////////////

	public void setControlPoint(ControlPoint ctrlPoint)
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
			sock.setControlPoint(ctrlPoint);
		}
	}

	////////////////////////////////////////////////
	//	get
	////////////////////////////////////////////////
	
	public SSDPSearchResponseSocket getSSDPSearchResponseSocket(int n)
	{
		return (SSDPSearchResponseSocket)get(n);
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

	public boolean open(int port){
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

		// add by geniusgithub begin
		boolean flag = false;
		for(int k = 0; k < bindAddresses.length; k++){
			AlwaysLog.i(TAG, "bindAddresses k = " + k + ", addr = " + bindAddresses[k]);
		}
		// add by geniusgithub end


		try {
			for (int j = 0; j < bindAddresses.length; j++) {
				// modify by geniusgithub begin
				//SSDPSearchResponseSocket socket = new SSDPSearchResponseSocket(bindAddresses[j], port);
				//add(socket);

				if (!isValidAddress(bindAddresses[j])){
					AlwaysLog.e(TAG, "ready to create SSDPSearchResponseSocket bindAddresses = " + bindAddresses[j]+ ", it's invalid so drop it!!!" );
					continue;
				}

				SSDPSearchResponseSocket socket = new SSDPSearchResponseSocket(bindAddresses[j], port);

				if (socket.getDatagramSocket() == null){
					AlwaysLog.e(TAG, "SSDPSearchResponseSocket.getSocket() == null!!!");
					continue;
				}
				AlwaysLog.i(TAG, "SSDPSearchResponseSocket create success!!!bindAddresses = " + bindAddresses[j]);

				add(socket);
				flag = true;
				continue;
				// modify by geniusgithub end

			}
		}catch (Exception e) {
			stop();
			close();
			clear();
			return false;
		}

		// modify by geniusgithub end
		//return true;
		return flag;
		// modify by geniusgithub end

	}

	public boolean open() 
	{
		return open(SSDP.PORT);
	}
		
	public void close()
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
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
			SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
			sock.start();
		}
	}

	public void stop()
	{
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
			sock.stop();
		}
	}

	////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////

	public boolean post(SSDPSearchRequest req)
	{
		boolean ret = true;
		int nSockets = size();
		for (int n=0; n<nSockets; n++) {
			SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
			String bindAddr = sock.getLocalAddress();
			req.setLocalAddress(bindAddr);
			String ssdpAddr = SSDP.ADDRESS;
			if (HostInterface.isIPv6Address(bindAddr) == true)
				ssdpAddr = SSDP.getIPv6Address();
			//sock.joinGroup(ssdpAddr, SSDP.PORT, bindAddr);
			if (sock.post(ssdpAddr, SSDP.PORT, req) == false)
				ret = false;
			//sock.leaveGroup(ssdpAddr, SSDP.PORT, bindAddr);
		}
		return ret;
	}
	
}

