package state;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Settings contains the data needed for the program to run
 * 
 */


public class Settings implements Serializable {

	private static final long serialVersionUID = 6206847912757795293L;
	
	
	private boolean _auto_updates_enabled;

	InetAddress _multicastGroup;
	private ArrayList<String> _directoriePaths;

	private int _revision;
	
	public Settings() {
		_auto_updates_enabled = false;
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
		_directoriePaths.add(0, directory);
	}
	
	public InetAddress get_multicastGroup() {
		return _multicastGroup;
	}
	
	public void set_multicastGroup(InetAddress _multicastGroup) {
		this._multicastGroup = _multicastGroup;
	}
	
}
