package com.nuszkowski.f5.ltm;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.axis.description.TypeDesc;
import org.apache.log4j.Logger;

import com.nuszkowski.f5.utils.ImportedNodeDef;


import iControl.CommonAddressPort;
import iControl.CommonIPPortDefinition;
import iControl.Interfaces;
import iControl.LocalLBPoolMemberMemberStatistics;

public class LTMPool implements LTMInterface {
	static Logger log = Logger.getLogger("LTMPool");
	private String[] pool_name = new String[1];
	private iControl.LocalLBLBMethod[] lb_method = new iControl.LocalLBLBMethod[1];

	public LTMPool()
	{
		lb_method[0] = iControl.LocalLBLBMethod.LB_METHOD_OBSERVED_MEMBER;
	}
	
	public LTMPool(String input_pool_name) {
		pool_name[0] = input_pool_name;
		lb_method[0] = iControl.LocalLBLBMethod.LB_METHOD_OBSERVED_MEMBER;
	}
	
	public void add(Interfaces f5_interface) {
		try {
			if (this.exists(f5_interface)) {
				throw new Exception("Pool " + pool_name[0] + " already exists.");
			}
			else
			{
				log.info("Adding pool " + pool_name[0]);
				f5_interface.getLocalLBPool().create_v2(pool_name, lb_method, null);
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}
	
	// Overloaded to add multiple nodes using the imported node definition
	public void add(Interfaces f5_interface,List<ImportedNodeDef> nodes)
	{
		List<String> dupeList = new ArrayList<String>();
		for (ImportedNodeDef ii:nodes)
		{
			dupeList.add(ii.getPoolName());
		}
		List<String> uniqueList = new ArrayList<String>(new HashSet<String>(dupeList)); 
		for (String s:uniqueList)
		{
			LTMPool pool = new LTMPool(s);
			pool.add(f5_interface);
		}
	}
	
	public void remove(Interfaces f5_interface) {
		try {
			if(!this.exists(f5_interface))
			{
				throw new Exception("Pool " + pool_name[0] + " doesn't exists, so I wont attempt to delete it.");
			}
			else
			{
				log.info("Removing pool " + pool_name[0]);
				f5_interface.getLocalLBPool().delete_pool(pool_name);
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	public boolean exists(Interfaces f5_interface) {
		try {
			String[] pool_list = f5_interface.getLocalLBPool().get_list();
			for (String s: pool_list)
			{
				String[] tmp_s = s.split("/");
				int size = tmp_s.length;
				s = tmp_s[size - 1];
				if (s.toUpperCase().equals(pool_name[0].toUpperCase()))
					return true;
			}
		} catch (RemoteException re) {
			log.error(re.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		
		return false;
	}

	public void addNodes(iControl.Interfaces f5_interface, List<ImportedNodeDef> nodes)
	{
		List<String> dupeList = new ArrayList<String>();
		for (ImportedNodeDef ii:nodes)
		{
			dupeList.add(ii.getPoolName());
		}
		List<String> uniqueList = new ArrayList<String>(new HashSet<String>(dupeList)); 
		for (String s:uniqueList)
		{
			log.info("Processing pool "+ s);
			int this_count = 0;
			for (ImportedNodeDef ii:nodes)
			{
				
				if (ii.getPoolName().equals(s))
				{
					this_count++;
				}
			}
			
			String[] pool_name = new String[1];
			pool_name[0] = s;
			int count = 0;
			
			// Inelegant way of returning # of eligible nodes to be added
			// in order to declare the second value in the CommonIPPortDefinition 
			// multi-dimension array
			
			int eligible_count = 0;
			for (ImportedNodeDef ii:nodes)
			{
				if (ii.getPoolName().equals(s))
				{
					try {
						if (!(new LTMPool().nodeExistsInPool(f5_interface, pool_name, ii)))
						{
							eligible_count++;
						}
					} catch (RemoteException e) {
						log.error(e.getMessage());
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
			
			CommonIPPortDefinition[][] hosts = new CommonIPPortDefinition[1][eligible_count];
			for (ImportedNodeDef ii:nodes)
			{
				if (ii.getPoolName().equals(s))
				{
					try {
						if (!(new LTMPool().nodeExistsInPool(f5_interface, pool_name, ii)))
						{
							log.info("Will add " + ii.getIPAddress() + ":" + ii.getPoolPort() + " to " + s);
							CommonIPPortDefinition cipp_def = new CommonIPPortDefinition();
							cipp_def.setAddress(ii.getIPAddress().toString());
							cipp_def.setPort((long)ii.getPoolPort());
							hosts[0][count] = cipp_def;
							count++;
						}
						else
						{
							log.info("Node " + ii.getIPAddress() + " already exists in " + pool_name[0] + " skipping.");
						}
					} catch (RemoteException e) {
						log.error(e.getMessage());
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
			if (count >0)
			{
				log.info("Adding " + count + " node(s) to pool " + s);
				try {
					f5_interface.getLocalLBPool().add_member(pool_name, hosts);
				} catch (RemoteException e) {
					log.error(e.getMessage());
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			else
			{
				log.info("No nodes to add to pools. ");
			}
		}
	}
	public boolean nodeExistsInPool(iControl.Interfaces f5_interface,String[] pool_name, ImportedNodeDef in) throws RemoteException, Exception {
		try {
			// Here we are going to back track and check to see if this node
			// was never added because of a dupe issue, but is picked up again
			// LTMNode ltm_node = new LTMNode(in.getServerName(), in.getIPAddress());
			// if (!ltm_node.exists(f5_interface)) {
			//	return true;
			// }
			
			CommonAddressPort[][] xq = f5_interface.getLocalLBPool().get_member_v2(pool_name);
			for (int i = 0; i < pool_name.length; i++)
			{
				for (int j = 0; j< xq[i].length; j++)
				{
					CommonAddressPort cp = xq[i][j];
					String node_address = cp.getAddress();
					String[] tmp_s = node_address.split("/");
					int size = tmp_s.length;
					node_address = tmp_s[size - 1].toUpperCase();
					long node_port = cp.getPort();
					if(node_address.equals(in.getServerName().toUpperCase()) && (node_port == (long)in.getPoolPort()))
						return true;
				}
			}
			return false;
		} catch (Exception e)
		{
			return false;
		}
	}

	public void list(Interfaces f5_interface) {
		try {
			for(String s: f5_interface.getLocalLBPool().get_list())
			{
				log.info(s);
			}
		} catch (RemoteException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public void listWithMembers(Interfaces f5_interface) {
		try {
			String[] pool_list = f5_interface.getLocalLBPool().get_list();
			CommonIPPortDefinition[][] member_def_2d = f5_interface.getLocalLBPool().get_member(pool_list);
			
			for(int i = 0; i < member_def_2d.length; i++)
			{
				CommonIPPortDefinition[] member_def = member_def_2d[i];
				for(int j = 0; j < member_def.length; j++)
				{
					CommonIPPortDefinition member = member_def_2d[i][j];
					String[] member_address_array = {member.getAddress()};
					String[] member_name_array = f5_interface.getLocalLBNodeAddress().get_screen_name(member_address_array);
					log.info(member_name_array[0].toUpperCase() + " -- " + member.getAddress() + " -- " + pool_list[i] + " -- " + member.getPort());
				}
			}
		} catch (RemoteException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
