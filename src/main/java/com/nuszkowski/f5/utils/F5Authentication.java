package com.nuszkowski.f5.utils;

import org.apache.log4j.Logger;

public class F5Authentication {
	static Logger log = Logger.getLogger("F5Authentication");
	
	/* Here we just fetch some basic info about a system
	to verify authentication. In the past, initialization
	wouldn't indicate an authentication status until a method 
	was executed using those credentials. This might be fixed,
	but its better to keep it here, just to be safe. */
	public iControl.Interfaces processAuth(String device_name, String user_name, String user_password)
	{
		try{
			iControl.Interfaces f_five = new iControl.Interfaces();
			f_five.initialize(device_name, user_name, user_password);
			String device_version = f_five.getSystemSystemInfo().get_version();
			log.info("Authentication to " + device_name + " successful for user " + user_name + ". System Version: " + device_version);
			return f_five;
		}
		catch(Exception ex){
			log.error("Authentication to " + device_name + " failed for user " + user_name + ".");
			ex.printStackTrace();
			System.exit(0);
			return null;
		}
	}
}