package Main;

import java.util.ArrayList;

import state.Directory;
import state.Settings;

import file.DirectoryMonitor;
import gui.Gui;
import network.NetworkManager;

public class SicComponents {

	public Settings settings;
	public ArrayList<String> dirList = new ArrayList<String>();
	
	public Gui ui;
	public NetworkManager netManager;
	public DirectoryMonitor dirMonitor;
	
}
