package com.nuszkowski.f5.ltm;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuszkowski.f5.utils.ImportedVirtualServerDef;


import iControl.CommonIPPortDefinition;
import iControl.CommonProtocolType;
import iControl.Interfaces;
import iControl.LocalLBProfileContextType;
import iControl.LocalLBVirtualServerVirtualServerProfile;
import iControl.LocalLBVirtualServerVirtualServerResource;
import iControl.LocalLBVirtualServerVirtualServerType;

public class LTMVirtualServer implements LTMInterface {
	static Logger log = Logger.getLogger("LTMVirtualServer");
	
	iControl.CommonVirtualServerDefinition[] vserver_def = new iControl.CommonVirtualServerDefinition[1];
	iControl.CommonProtocolType protocol_type = CommonProtocolType.PROTOCOL_TCP;
	String[] wildmasks = new String[1];
	iControl.LocalLBVirtualServerVirtualServerResource[] vs_resource = new  LocalLBVirtualServerVirtualServerResource[1];
	iControl.LocalLBVirtualServerVirtualServerType vs_type = LocalLBVirtualServerVirtualServerType.RESOURCE_TYPE_POOL;
	iControl.LocalLBVirtualServerVirtualServerProfile[][] vs_profile = new LocalLBVirtualServerVirtualServerProfile[1][1];
	LocalLBProfileContextType thingy = LocalLBProfileContextType.PROFILE_CONTEXT_TYPE_ALL;
	
	public LTMVirtualServer()
	{
		// Default blank constructor, so that helper/utility methods can be used
	}
	
	public LTMVirtualServer(String name, String address, long port, String default_pool)
	{
		iControl.CommonVirtualServerDefinition vs = new iControl.CommonVirtualServerDefinition(name, address, port, CommonProtocolType.PROTOCOL_TCP);
		vserver_def[0] = vs;
		wildmasks[0] = "255.255.255.255";
//		vserver_def[0].setName(name);
//		vserver_def[0].setAddress(address);
//		vserver_def[0].setPort(port);
//		vserver_def[0].setProtocol(protocol_type);
		
		iControl.LocalLBVirtualServerVirtualServerResource whatever;
		whatever = new iControl.LocalLBVirtualServerVirtualServerResource(LocalLBVirtualServerVirtualServerType.RESOURCE_TYPE_POOL, "");
		if (!default_pool.isEmpty())
		{
			whatever.setDefault_pool_name(default_pool);
		}
		vs_resource[0] = whatever;
		//vs_resource[0].setDefault_pool_name(default_pool);
		//vs_resource[0].setType(vs_type);
		
		iControl.LocalLBVirtualServerVirtualServerProfile whatever2 = new LocalLBVirtualServerVirtualServerProfile(thingy,"http");
		
		vs_profile[0][0] = whatever2;
		//vs_profile[0][0].setProfile_name("Testing");
		//vs_profile[0][0].setProfile_context(thingy);
	}
	
	public void add(Interfaces f5_interface) {
		try {
			if (this.exists(f5_interface))
			{
				throw new Exception("Virtual Server with server name " + vserver_def[0].getName() + " already exists.");
			}
			else
			{
				log.info("Adding virtual server " + vserver_def[0].getName());
				f5_interface.getLocalLBVirtualServer().create(vserver_def, wildmasks, vs_resource, vs_profile);
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}
	
	// Overloaded to add multiple nodes using the imported node definition
	public void add(Interfaces f5_interface, List<ImportedVirtualServerDef> vs_list)
	{
		for (ImportedVirtualServerDef vs: vs_list)
		{			
			LTMVirtualServer ltmvs = new LTMVirtualServer(vs.getVSName(), vs.getIPAddress(), (long)vs.getPort(), vs.getDefaultPool());
			ltmvs.add(f5_interface);
		}
	}

	public void remove(Interfaces f5_interface) {
		try {
			if (!this.exists(f5_interface))
			{
				throw new Exception("Virtual Server with server name " + vserver_def[0].getName() + " doesn't exist. I won't attempt to remove it.");
			}
			else
			{
				String temp[] = new String[1];
				temp[0] = vserver_def[0].getName();
				log.info("Removing virtual server" + vserver_def[0].getName());
				f5_interface.getLocalLBVirtualServer().delete_virtual_server(temp);
				
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	public boolean exists(Interfaces f5_interface) {
		try {
			for (String s : f5_interface.getLocalLBVirtualServer().get_list())
			{
				String[] tmp_s = s.split("/");
				int size = tmp_s.length;
				s = tmp_s[size - 1];
				if(vserver_def[0].getName().equals(s))
					return true;
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return false;
	}

	public void list(Interfaces f5_interface) {
		try {
			String[] vs_list = f5_interface.getLocalLBVirtualServer().get_list();
			CommonIPPortDefinition[] vs_destinations = f5_interface.getLocalLBVirtualServer().get_destination(vs_list);
			String[] vs_default_pools = f5_interface.getLocalLBVirtualServer().get_default_pool_name(vs_list);
			
			for (int i = 0; i < vs_list.length; i++)
			{
				log.info(vs_list[i] + "," + vs_destinations[i].getAddress() + "," + vs_destinations[i].getPort() + "," + vs_default_pools[i]);
			}
		} catch (RemoteException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}