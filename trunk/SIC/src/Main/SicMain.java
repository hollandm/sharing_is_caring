package Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;


import file.DirectoryMonitor;
import gui.Gui;

import network.NetworkManager;

import state.Settings;


public class SicMain {

	public final String settingsPath;
	
	public final boolean firstTimeStartup;
	
	private Settings settings;	
	
	private DirectoryMonitor dirMonitor;
	
	private Gui ui;
	
	@SuppressWarnings("unused")
	private NetworkManager netManager;
	
	private Path path;
	
	public SicMain() {

		SicComponents components = new SicComponents();
		
		//Detect if this is the first time the program has been run
		settingsPath = System.getProperty("user.dir") + "//SIC.settings";		
		
		//if first run then perform first time setup
		if (!(new File(settingsPath).exists())) {
			System.out.println("Settings file not detected, performing first time setup");
			Settings settings = Settings.createDefaultSettings();
			settings.saveChanges();
			firstTimeStartup = true;
		} else {
			firstTimeStartup = false;
		}
		
		
		//load the settings file from the disk
		try {
			ObjectInputStream settingsReader = new ObjectInputStream(new FileInputStream(new File(settingsPath)));
	
			settings = (Settings) settingsReader.readObject();
			
			File dirFile = new File(settings.getDirectory());
			String directory = settings.getDirectory().toString();
			
			boolean settingsChanged = false;
			while (!dirFile.exists() || !dirFile.isDirectory()) {
				settingsChanged = true;
				directory = JOptionPane.showInputDialog("Please enter a default directory path");
				directory.trim();
				dirFile = new File(directory);
			}
			if (settingsChanged) {
				settings.updateDirectory(directory);
			}
			
			components.settings = settings;
//			components.dirList.add(settings.getDirectory());
			ui = new Gui(components);	
			components.ui = ui;
			
			settingsReader.close();
		} 
		catch (ClassNotFoundException | FileNotFoundException e) {
			System.err.println("Setting file missing or corrupt");
			e.printStackTrace();
			System.exit(0);
		} 
		catch (IOException e) {
			System.err.println("Failed to load settings file");
			e.printStackTrace();
			System.exit(0);
		} 
		
		components.settings = settings;
		
		
		System.err.println("HIIIIIEIEIEIEIEI");

		//start the file monitor if in auto mode
		try {
			path = Paths.get(components.settings.getDirectory());
			dirMonitor = new DirectoryMonitor(path, true);
			Thread t1 = new Thread(dirMonitor);
			t1.start();
			components.dirMonitor = dirMonitor;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("HIIIIIEIEIEIEIEI");

				
		ui.setComponents(components);
		
		//start the network manager
		NetworkManager netManager = new NetworkManager(components);
		components.netManager = netManager;
		
		
		netManager.begin();
		
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SicMain MatthewIsAwesome = new SicMain();
		
	}

}
