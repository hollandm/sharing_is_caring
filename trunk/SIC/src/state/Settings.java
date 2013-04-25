package state;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Settings contains the data needed for the program to run
 * 
 */


public class Settings implements Serializable {

	private static final long serialVersionUID = 6206847912757795293L;
	private int delayTime = 15;
	
	
	private boolean _auto_updates_enabled;

	InetAddress _multicastGroup;
	private ArrayList<String> _directoriePaths;

	private int _revision;
	
	public Settings() {
		_auto_updates_enabled = true;
		_directoriePaths = new ArrayList<String>();
	}
	
	public boolean is_auto_updates_enabled() {
		return _auto_updates_enabled;
	}

	public void set_auto_updates_enabled(boolean _auto_updates_enabled) {
		this._auto_updates_enabled = _auto_updates_enabled;
	}

	public ArrayList<String> getDirectoryList() {
		return _directoriePaths;
	}

	public void updateDirectory(String directory)
	{
		if(_directoriePaths.isEmpty()) {
			_directoriePaths.add(directory);
		}
		else {
			_directoriePaths.set(0, directory);
		}
		System.err.println("UPdated path");
	}
	
	public InetAddress get_multicastGroup() {
		return _multicastGroup;
	}
	
	public void set_multicastGroup(InetAddress _multicastGroup) {
		System.err.println("UPdated address");
		this._multicastGroup = _multicastGroup;
	}

	
	public String toString() {
		return "auto updates: "+_auto_updates_enabled
				+", multicast group: "+_multicastGroup
				+", directories: "+_directoriePaths
				+", revision: "+_revision;
	}
	
	public int getDelay() {
		return delayTime;
	}
	
	public void updateDelay(int delay){
		delayTime = delay;
	}
}
