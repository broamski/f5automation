package com.nuszkowski.f5.utils;

public class ImportedNodeDef {
	private final String server_name;
	private final String ip_address;
	private final String pool_name;
	private final int pool_port;

	public ImportedNodeDef(String server_name, String ip_address,String pool_name, int pool_port) {
		this.ip_address = ip_address;
		this.server_name = server_name;
		this.pool_name = pool_name;
		this.pool_port = pool_port;
	}

	public String getIPAddress() {
		return ip_address;
	}
	
	public String getServerName() {
		return server_name;
	}
	
	public String getPoolName() {
		return pool_name;
	}
	
	public int getPoolPort()
	{
		return pool_port;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s %s", server_name, ip_address, pool_name, pool_port);
	}

}