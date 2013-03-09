package state;

import java.util.ArrayList;

public class Log {
	
	private int _revision;
	
	private ArrayList<String> string;
	
	
	public int get_revision() {
		return _revision;
	}

	public void bump_revision(int _revision) {
		this._revision++;
	}
	
}
