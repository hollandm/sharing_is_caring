package Main;

import java.util.ArrayList;

import state.Settings;

import file.DirectoryMonitor;
import gui.Gui;
import network.NetworkManager;

/**
 * A container for all of the information that our program needs
 * Makes it easier for us to pass information to and from all the difference classes
 * without having to pass in a ton of parameters
 *
 */
public class SicComponents {

	public Settings settings;
	public ArrayList<String> dirList = new ArrayList<String>();
	
	public Gui ui;
	public NetworkManager netManager;
	public DirectoryMonitor dirMonitor;
	
}
