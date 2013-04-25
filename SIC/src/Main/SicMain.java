package Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;


import file.DirectoryMonitor;
import gui.Gui;

import network.NetworkManager;

import state.Directory;
import state.Settings;


public class SicMain {

	public final String settingsPath;
	
	public final boolean firstTimeStartup;
	
	private Settings settings;	
	
	private DirectoryMonitor dirMonitor;
	
	private Gui ui;
	
	private NetworkManager netManager;
	
	private Path path;
	
	public SicMain() {

		SicComponents components = new SicComponents();
		
		//Detect if this is the first time the program has been run
		settingsPath = System.getProperty("user.dir") + "\\SIC.settings";		
		
		//if first run then perform first time setup
		if (!(new File(settingsPath).exists())) {
			System.out.println("Settings file not detected, performing first time setup");
			firstTimeStartup = true;
			firstTimeSetup(settingsPath);
		} else {
			firstTimeStartup = false;
		}
		
		
		//load the settings file from the disk
		try {
			ObjectInputStream settingsReader = new ObjectInputStream(new FileInputStream(new File(settingsPath)));
	
			settings = (Settings) settingsReader.readObject();
			
			File dirFile = new File(settings.getDirectory());
			boolean settingsChanged = false;
			while (!dirFile.exists() || !dirFile.isDirectory()) {
				settingsChanged = true;
				String directory = JOptionPane.showInputDialog("Please enter a default directory path");
				directory.trim();
				dirFile = new File(directory);

				settings.getDirectoryList().clear();
				settings.getDirectoryList().add(directory);
			}
			
			components.dirList.add(settings.getDirectory());
			ui = new Gui(settings.getDirectory(), settings.get_multicastGroup(), settings.getDelay());	
			components.ui = ui;
			components.settings = settings;
			
			settingsReader.close();
		} 
		catch (ClassNotFoundException | FileNotFoundException e) {
			System.err.println("Setting file missing or corrupt");
			//TODO: notify user and prompt them to fix it
			e.printStackTrace();
			System.exit(0);
		} 
		catch (IOException e) {
			System.err.println("Failed to load settings file");
			e.printStackTrace();
			System.exit(0);
		} 
		
		components.settings = settings;

		//start the network manager
		NetworkManager netManager = new NetworkManager(components);
		components.netManager = netManager;
			
		//start the file monitor if in auto mode
		try {
			path = Paths.get(components.dirList.get(0));
			dirMonitor = new DirectoryMonitor(path, true);
			Thread t1 = new Thread(dirMonitor);
			t1.start();
			components.dirMonitor = dirMonitor;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ui.setComponents(components);
	}
	
	/**
	 * This method creates a settings object and writes it to a file
	 * @param settingsPath is the location were to write the file to
	 */
	public static void firstTimeSetup(String settingsPath) {
		
		ObjectOutputStream writer;
		
		Settings settings = new Settings();
		
		// set default multicast address
		try{
			settings.set_multicastGroup(InetAddress.getByName("230.0.0.10"));
		}
		catch (Exception e){
	
		}
		
		// set default delay
		settings.setDelay(15);
		
		
		// set default directory
		try {
			writer = new ObjectOutputStream(new FileOutputStream(new File(settingsPath)));
			
			File dirFile;
			String directory;
			do{
				directory = JOptionPane.showInputDialog("Please enter a default directory path");
				directory.trim();
				dirFile = new File(directory);
			} while(!dirFile.isDirectory());
			// if directory does not exist, keep prompting user
			settings.updateDirectory(directory);
			
			writer.writeObject(settings);
			writer.close();
		} catch (IOException e) {
			System.err.println("Failed to generate settings file");
			System.exit(0);
		}
	
	}
	

	public static void main(String[] args) {
		SicMain MatthewIsAwesome = new SicMain();
		
	}

}
