package com.nuszkowski.f5.utils;

public class ImportedVirtualServerDef {
	private final String vs_name;
	private final String ip_address;
	private final int port;
	private final String default_pool;

	public ImportedVirtualServerDef(String vs_name, String ip_address, int port, String default_pool) {
		this.vs_name = vs_name;
		this.ip_address = ip_address;
		this.port = port;
		this.default_pool = default_pool;
	}
	
	public String getVSName() {
		return vs_name;
	}
	public String getIPAddress() {
		return ip_address;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getDefaultPool()
	{
		return default_pool;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s %s", vs_name, ip_address, port, default_pool);
	}

}