package com.nuszkowski.f5.ltm;

public interface LTMInterface {
	void add(iControl.Interfaces f5_interface);
	void remove(iControl.Interfaces f5_interface);
	boolean exists(iControl.Interfaces f5_interface);
	void list(iControl.Interfaces f5_interface);
}
