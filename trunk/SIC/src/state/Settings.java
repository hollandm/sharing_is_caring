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
	
	/**
	 * Returns the revision number
	 * @return
	 */
	public int getRevision() {
		return _revision;
	}

	/**
	 * Increments the revision number.
	 * @param _revision
	 */
	public void bumpRevision(int _revision) {
		this._revision++;
	}
	
	
	/**
	 * For now the program will always auto update. We left this in here
	 * in case we want the program to allow for a manual update button.
	 * @return
	 */
	public boolean is_auto_updates_enabled() {
		return _auto_updates_enabled;
	}

	/**
	 * Enables or disables the auto updating feature.
	 * @param _auto_updates_enabled
	 */
	public void set_auto_updates_enabled(boolean _auto_updates_enabled) {
		this._auto_updates_enabled = _auto_updates_enabled;
	}

	/**
	 * Returns a vector of directories. This allows us to add
	 * a feature to have more than one directory in sync.
	 * @return
	 */
	public ArrayList<String> getDirectoryList() {
		return _directoriePaths;
	}
	
	/**
	 * Returns the first directory in the list of directories
	 */
	public String getDirectory(){
		return _directoriePaths.get(0);
	}
	
	/**
	 * Changes the root folder.
	 * @param directory
	 */
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
	
	/**
	 * Changes the multicast ip address.
	 * @param _multicastGroup
	 */
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
	
	
	/**
	 * Modifies the delay time to avoid congestion.
	 * @param delay
	 */
	public void setDelay(int delay){
		delayTime = delay;
	}
}
