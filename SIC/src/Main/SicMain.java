package Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import file.DirectoryMonitor;
import gui.Gui;

import network.NetworkManager;

import state.Directory;
import state.Settings;


public class SicMain {

	public final String settingsPath;
	
	public final boolean firstTimeStartup;
	
	private Settings settings;
	private ArrayList<Directory> directoryList;
	
	private DirectoryMonitor dirMonitor;
	private Gui ui;
	private NetworkManager netManager;
	
	
	
	public SicMain() {

		SicComponents components = new SicComponents();
		
		//Detect if this is the first time the program has been run
		settingsPath = System.getProperty("user.dir") + "\\SIC.settings";
//		System.out.println(settingsPath);
		
		
		//if first run then preform first time setup
		if (!(new File(settingsPath).exists())) {
			System.out.println("Settings file not detected, preforming first time setup");
			firstTimeStartup = true;
			firstTimeSetup(settingsPath);
		} else {
			firstTimeStartup = false;
		}
		
		
		//load the settings file from the disk
		try {
			ObjectInputStream settingsReader = new ObjectInputStream(new FileInputStream(new File(settingsPath)));
		
			settings = (Settings) settingsReader.readObject();
			
			settingsReader.close();
		} catch (ClassNotFoundException | FileNotFoundException e) {
			System.err.println("Setting file missing or corrupt");
			//TODO: notify user and prompt them to fix it
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.err.println("Failed to load settings file");
			e.printStackTrace();
			System.exit(0);
		} 
		
		
		//load directories stored in settings file
		//ensure integrity
		ArrayList<String> dirList = settings.getDirectoryList();
		for (int i = 0; i < dirList.size(); ++i) {
			String dirStr = dirList.get(i);
			try {
				File dirFile = new File(dirStr);
				ObjectInputStream dirReader = new ObjectInputStream(new FileInputStream(dirFile));
				Directory dir = (Directory) dirReader.readObject();
				directoryList.add(dir);
			} catch (FileNotFoundException | ClassNotFoundException e) {
				System.err.println("Directory file missing or corrupt");
				//TODO: notify user and prompt them to fix it
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//start the network manager
		NetworkManager netManager = new NetworkManager(components);
		
		//start the file monitor if in auto mode
		try {
			dirMonitor = new DirectoryMonitor(components);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//display the gui
		ui = new Gui();
		
	}
	
	/**
	 * This method creates a settings object and writes it to a file
	 * @param settingsPath is the location were to write the file to
	 */
	public static void firstTimeSetup(String settingsPath) {
		
		ObjectOutputStream writer;
		
		try {
			writer = new ObjectOutputStream(new FileOutputStream(new File(settingsPath)));
			
			Settings settings = new Settings();
			
			writer.writeObject(settings);
			writer.close();
		} catch (IOException e) {
			System.err.println("Failed to generate settings file");
//			e.printStackTrace();
			System.exit(0);
		}
		
	}
	

	public static void main(String[] args) {
		SicMain MatthewIsAwesome = new SicMain();
	}

}
