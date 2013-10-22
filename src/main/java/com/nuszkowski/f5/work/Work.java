package com.nuszkowski.f5.work;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import iControl.CommonAddressPort;
import iControl.CommonEnabledState;
import iControl.CommonIPPortDefinition;
import iControl.CommonProtocolType;
import iControl.GlobalLBPoolPoolMemberDefinition;
import iControl.LocalLBClassStringClass;
import iControl.LocalLBPoolMemberMemberObjectStatus;
import iControl.ManagementDBVariableVariableNameValue;
import iControl.XTrustProvider;

import org.apache.log4j.Logger;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.nuszkowski.f5.ltm.LTMNode;
import com.nuszkowski.f5.ltm.LTMPool;
import com.nuszkowski.f5.ltm.LTMVirtualServer;
import com.nuszkowski.f5.utils.DefinitionReader;
import com.nuszkowski.f5.utils.F5Authentication;
import com.nuszkowski.f5.utils.ImportedNodeDef;
import com.nuszkowski.f5.utils.ImportedVirtualServerDef;

public class Work {
	static Logger log = Logger.getLogger("Work");
	
	// !! This eventually needs to be externalized to a config singleton.
	public static final String credentials_username = "Username";
	public static final String credentials_password = "Password";

		
	public static void main(String[] args) throws Exception {
		F5Authentication auth = new F5Authentication();
		iControl.Interfaces f_five;
		f_five = auth.processAuth("servername.dominan.com", credentials_username, credentials_password);

		DefinitionReader node_reader = new DefinitionReader();
		List<ImportedNodeDef> nodes = node_reader.buildNodeInfo("/home/user/node_definition.csv");
		
		DefinitionReader vs_reader = new DefinitionReader();
		List<ImportedVirtualServerDef> virtuals = vs_reader.buildVSInfo("/home/user/vs_definition.csv");

		//Reads and adds all nodes v2
		LTMNode ltm_node = new LTMNode();
		ltm_node.add(f_five, nodes);
		
		// Read and build a list of unique pool names
		LTMPool ltm_pool = new LTMPool();	
		ltm_pool.add(f_five, nodes);
		// Adds nodes to necessary pools
		ltm_pool.addNodes(f_five, nodes);
		
		// Creates the virtual servers and assigns pools to them.
		LTMVirtualServer ltmvs = new LTMVirtualServer();
		ltmvs.add(f_five, virtuals);
	}

}
