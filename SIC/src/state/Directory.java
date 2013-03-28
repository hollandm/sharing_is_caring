package state;

import java.io.Serializable;
import java.util.ArrayList;

public class Directory implements Serializable{

	private static final long serialVersionUID = 312784493590177413L;

	private String _id;
	private String _path;
	private int _revision;
	
	private Log _log;
	
	private ArrayList<Friend> _friends;

	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_path() {
		return _path;
	}

	public void set_path(String _path) {
		this._path = _path;
	}

	public Log get_log() {
		return _log;
	}

	public ArrayList<Friend> get_friends() {
		return _friends;
	}

	public void set_friends(ArrayList<Friend> _friends) {
		this._friends = _friends;
	}
	
}
