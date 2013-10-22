package com.nuszkowski.f5.ltm;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuszkowski.f5.utils.ImportedNodeDef;

import iControl.Interfaces;

public class LTMNode implements LTMInterface {
	static Logger log = Logger.getLogger("LTMNode");
	private String[] server_name = new String[1];
	private String[] ip_address = new String[1];
	private long[] conn_limit = new long[1];
	
	public LTMNode()
	{
		// Default blank constructor, so that helper/utility methods can be used
	}
	
	public LTMNode(String server_name, String ip_address)
	{
		this.server_name[0] = server_name.toUpperCase();
		this.ip_address[0] = ip_address;
		this.conn_limit[0] = 0;
	}
	
	public void setServerName(String server_name)
	{
		this.server_name[0] = server_name;
	}
	
	public void setIPAddress(String ip_address)
	{
		this.ip_address[0] = ip_address;
	}
	
	public void add(Interfaces f5_interface) {
		try {
			if(this.exists(f5_interface))
			{
				throw new Exception("Node with server name " + server_name[0] + " or IP address " + ip_address[0] + " already exists.");
			}
			else
			{
				log.info("Adding node " + server_name[0] + " - "  + ip_address[0]);
				f5_interface.getLocalLBNodeAddressV2().create(server_name, ip_address, conn_limit);
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}

	}
	
	// Overloaded to add multiple nodes using the imported node definition
	public void add(Interfaces f5_interface, List<ImportedNodeDef> nodes)
	{
		for (ImportedNodeDef ii:nodes)
		{
			LTMNode node = new LTMNode(ii.getServerName(), ii.getIPAddress());
			node.add(f5_interface);
		}
	}

	public void remove(Interfaces f5_interface) {
		try {
			if(!this.exists(f5_interface))
			{
				throw new Exception("Node with server name " + server_name[0] + " or IP address " + ip_address[0] + " doesn't exists, so I wont attempt to delete it.");
			}
			else
			{
				log.info("Removing node " + server_name[0] + " - "  + ip_address[0]);
				f5_interface.getLocalLBNodeAddressV2().delete_node_address(ip_address);
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}

	}

	public boolean exists(Interfaces f5_interface) {
		try {
			
			String[] node_name_list = f5_interface.getLocalLBNodeAddressV2().get_list(); 
			String[] node_ip_list = f5_interface.getLocalLBNodeAddressV2().get_address(node_name_list);
			
			for (String s: node_ip_list)
			{
				if (s.equals(ip_address[0]))
					return true;
			}
			
			for (String s2: node_name_list)
			{
				String[] tmp_s = s2.split("/");
				int size = tmp_s.length;
				s2 = tmp_s[size - 1];
				if (s2.toUpperCase().equals(server_name[0].toUpperCase()))
					return true;
			}
			
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return false;
	}

	// List all of the nodes on the current device.
	public void list(Interfaces f5_interface) {
		try {
			
			// There are some compatibility issues here. Your mileage may vary
			//String[] node_list = f5_interface.getLocalLBNodeAddressV2().get_list();
			String[] node_list_ip = f5_interface.getLocalLBNodeAddress().get_list();
			String[] node_list_screenname = f5_interface.getLocalLBNodeAddress().get_screen_name(node_list_ip);
			
			if (node_list_ip.length == node_list_screenname.length)
			{
				log.info("Node IP array size of " + node_list_ip.length + " is equal to Node Name array size of " + node_list_screenname.length + ", proceeding.");
				for (int i=0; i < node_list_ip.length; i++)
				{
					log.info(node_list_ip[i] + "," + node_list_screenname[i].toUpperCase());
				}
			}
			else
			{
				log.error("Node IP array size of " + node_list_ip.length + " is NOT equal to Node Name array size of " + node_list_screenname.length + ". I won't do anything.");
			}
			
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

}