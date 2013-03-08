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
	
	private int _revision_number;
	private String _directory;
	private boolean _automatic_updates;
	
	private ArrayList<Freind> _freinds;
	
	
}
