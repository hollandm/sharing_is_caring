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
	
	
	private boolean _auto_updates_enabled;

	InetAddress _multicastGroup;
	private ArrayList<Directory> _directories;

	private int _revision;
	
	public Settings() {
		_auto_updates_enabled = false;
		_directories = new ArrayList<Directory>();
	}
	
	public boolean is_auto_updates_enabled() {
		return _auto_updates_enabled;
	}

	public void set_auto_updates_enabled(boolean _auto_updates_enabled) {
		this._auto_updates_enabled = _auto_updates_enabled;
	}

	public ArrayList<Directory> getDirectoryList() {
		return _directories;
	}

	
	public InetAddress get_multicastGroup() {
		return _multicastGroup;
	}
	
	public void set_multicastGroup(InetAddress _multicastGroup) {
		this._multicastGroup = _multicastGroup;
	}
	
}
