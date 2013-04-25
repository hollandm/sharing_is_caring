package state;


/**
 * A running log for the revision numbers.
 * @author VietPhan
 *
 */
public class Log {
	
	private int _revision;
	
	/**
	 * Returns the revision number
	 * @return
	 */
	public int get_revision() {
		return _revision;
	}

	/**
	 * Increments the revision number.
	 * @param _revision
	 */
	public void bump_revision(int _revision) {
		this._revision++;
	}
	
}
