package state;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Settings contains the data needed for the program to run
 * 
 *
 */


public class Settings implements Serializable {

	private static final long serialVersionUID = 6206847912757795293L;
	
	
	private boolean _auto_updates_enabled;
	
	private ArrayList<Friend> _freinds;
	private ArrayList<Directory> _directories;
	
	
	
	public Settings() {
		
		_freinds = new ArrayList<Friend>();
		_directories = new ArrayList<Directory>();
	}
	
	public boolean is_auto_updates_enabled() {
		return _auto_updates_enabled;
	}

	public void set_auto_updates_enabled(boolean _auto_updates_enabled) {
		this._auto_updates_enabled = _auto_updates_enabled;
	}

	public ArrayList<Friend> get_freinds() {
		return _freinds;
	}

	public ArrayList<Directory> get_directories() {
		return _directories;
	}

	public static void main(String[] args) {
		
		Settings test = new Settings();
		
	}

	
}
