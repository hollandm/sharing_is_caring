package gui;

import java.awt.Dimension;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.*;

import file.DirectoryMonitor;

import Main.SicComponents;

import state.Settings;

/**
 * Gui
 * 
 * This class creates the home gui, folder gui, and delay gui. It serves as the
 * listener for all of the buttons and menu items for each of the gui's.
 *
 */
public class Gui implements ActionListener{

	protected SicComponents components;

	protected JFrame myFrame = new JFrame();

	protected int versionID = 0;

	/**
	 * Instances of labels for information box. Displays current update version,
	 * the multicast address, the directory address, and the delay that is set.
	 */
	protected JLabel updateVersion = new JLabel("Update Version: ");
	protected JLabel multicastAddress = new JLabel("Multicast Address: ");
	protected JLabel directoryAddress = new JLabel("Directory Address: ");
	protected JLabel delayTextBox = new JLabel("Delay");

	/**
	 * Instance variables for menu
	 */
	protected JMenu menu = new JMenu("Menu");
	protected JRadioButtonMenuItem disableUpdate = new JRadioButtonMenuItem("Disable");
	protected JRadioButtonMenuItem autoUpdate = new JRadioButtonMenuItem("Auto");
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenuItem menuMulticast = new JMenuItem("Manage Multicast Address");
	protected JMenuItem menuDirectory = new JMenuItem("Manage Shared Folder");
	protected JMenuItem menuDelayUpdate = new JMenuItem("Manage Delay Settings");
	protected JMenuItem quit = new JMenuItem("Exit Program");

	/**
	 * Instance of other GUI's
	 */
	protected FriendGui friend = new FriendGui();
	protected FolderGui folder = new FolderGui();
	protected DelayGui delayGui = new DelayGui();

	/**
	 * Instance variables for minimizing to tray
	 */
	protected TrayIcon icon;
	final PopupMenu popup = new PopupMenu();
	MenuItem exitItem = new MenuItem("Exit Program");

	public Gui(SicComponents comp){
		components = comp;
		
		// receive necessary components from settings
		folder.addressString = components.settings.getDirectory();
		friend.addressString = components.settings.get_multicastGroup().toString().substring(1);
		delayGui.delay = components.settings.getDelay();

		// add listener for each button in the other gui's
		folder.setFolderAddressButton.addActionListener(this);
		friend.setMulticastAddressButton.addActionListener(this);
		delayGui.setDelay.addActionListener(this);

		// create system tray icon
		ImageIcon image = new ImageIcon();
		try {
			image = new ImageIcon(new URL("http://i.imgur.com/D8IxL.gif"));
		}
		catch(Exception e) {

		}
		SystemTray st = SystemTray.getSystemTray();
		icon = new TrayIcon(image.getImage());
		icon.setImageAutoSize(true);
		popup.add(exitItem);
		exitItem.addActionListener(this);

		// create popup menu so user can quit program
		icon.addActionListener(this);
		icon.setPopupMenu(popup);
		try{
			st.add(icon);
		}
		catch(Exception e){
			System.err.println("System tray unsupported");
		}

		// set gui frame
		Dimension frameSize = new Dimension(450, 250);
		myFrame.setSize(frameSize);
		myFrame.setTitle("SIC");
		myFrame.setResizable(false);

		// set listener for home button for each of the other GUIs
		// the home button brings you back to home page (this gui)
		friend.homeButton.addActionListener(this);
		folder.homeButton.addActionListener(this);
		delayGui.homeButton.addActionListener(this);

		// split home gui into 3 rows
		Box mainBox = Box.createVerticalBox();
		Box row1 = Box.createHorizontalBox();

		/**<---------------------Building Menu------------------------>*/

		menuBar.add(menu);

		// add menu item for setting multicast address
		menu.add(menuMulticast);
		menuMulticast.addActionListener(this);
		menu.addSeparator();

		// add menu item for setting directory address
		menu.add(menuDirectory);
		menuDirectory.addActionListener(this);
		menu.addSeparator();

		// add menu item for updating delay time
		menu.add(menuDelayUpdate);
		menuDelayUpdate.addActionListener(this);

		//auto or manual update radio buttons
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		autoUpdate = new JRadioButtonMenuItem("Auto Update");
		autoUpdate.addActionListener(this);
		autoUpdate.setSelected(true);
		disableUpdate = new JRadioButtonMenuItem("Disable Update");
		disableUpdate.addActionListener(this);

		group.add(autoUpdate);
		group.add(disableUpdate);
		menu.add(autoUpdate);
		menu.add(disableUpdate);

		menu.add(quit);
		quit.addActionListener(this);

		myFrame.setJMenuBar(menuBar);	


		/**<-----------------------Row 1---------------------------> */

		// add information box
		Box info = Box.createVerticalBox();
		info.add(updateVersion);
		updateVersion.setText("Update Version: " + versionID);
		info.add(multicastAddress);
		multicastAddress.setText("Multicast address: " + friend.addressString);
		info.add(directoryAddress);
		directoryAddress.setText("Directory address: " + folder.addressString);
		info.add(delayTextBox);
		delayTextBox.setText("Delay: " + delayGui.delay + " milliseconds");

		row1.add(info);

		/**<-------------Add everything to main box-------------------> */

		mainBox.add(Box.createVerticalGlue());
		mainBox.add(row1);
		mainBox.add(Box.createVerticalGlue());

		/**<-------------Add everything to top frame-------------------> */

		myFrame.add(mainBox);
		myFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		/**<----------------------Multicast Gui--------------------------->*/
		// if multicast menu item is selected, open up multicast gui
		if(arg0.getSource() == menuMulticast){
			openMulticastGui();
		}

		// set multicast address only if it is a valid one
		else if(arg0.getSource() == friend.setMulticastAddressButton){
			setMulticastAddress();
		}	

		// in multicast gui, if pressed home, go back to main menu
		else if (arg0.getSource() == friend.homeButton){
			leaveMulticastGui();
		}

		/**<--------------------Directory Gui---------------------------->*/

		// if clicked on directory management in menu, open new gui
		else if(arg0.getSource() == menuDirectory){
			openDirectoryGui();
		}

		// set directory address
		else if(arg0.getSource() == folder.setFolderAddressButton){
			setDirectoryAddress();
		}	

		// if in directory management, and pressed home, go to home gui
		// also set text in home gui to reflect any changes
		else if(arg0.getSource() == folder.homeButton){
			leaveDirectoryGui();
		}

		/**<-------------------------Delay Gui-------------------------->*/

		// open delay gui when delay menu is pressed
		else if(arg0.getSource() == menuDelayUpdate){
			openDelayGui();
		}

		// set delay
		else if(arg0.getSource() == delayGui.setDelay){
			setDelay();
		}	

		// update delay string on home page once delay time has been updated
		else if(arg0.getSource() == delayGui.homeButton){
			leaveDelayGui();
		}

		/**<-----------------Auto/manual update------------------------>*/
		
		// set toggle for auto update
		else if (arg0.getSource() == autoUpdate){
			enableAutoUpdate();
		}
		
		// set toggle to disable update
		else if (arg0.getSource() == disableUpdate){
			disableAutoUpdate();
		}

		/**<------------------Exit program----------------------------->*/

		// exits when user chooses to exit program
		else if (arg0.getSource() == quit){
			closeProgram();
		}

		/**<-----------------TrayIcon Stuff---------------------------->*/
		
		// when icon is pressed, brings up the gui again
		else if (arg0.getSource() == icon){
			unminimizeGui();
		}

		// right click on icon, chose to quit
		else if (arg0.getSource() == exitItem){
			closeProgram();
		}
	}

	/**
	 * helper method for opening multicast gui
	 */
	private void openMulticastGui(){
		friend.addressTextField.setValue(friend.addressString);
		friend.myFrame.setLocation(myFrame.getLocation());
		friend.myFrame.setVisible(true);
		myFrame.setVisible(false);
	}

	/**
	 * helper method for setting multicast address in multicast gui
	 */
	private void setMulticastAddress(){
		String temp = friend.addressString;// = address.getText();
		try{
			friend.addressString = friend.addressTextField.getText();
			components.settings.set_multicastGroup(
					InetAddress.getByName(friend.addressString.trim()));
			components.netManager.changeGroup();
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(myFrame,"Invalid IP address: "
					+ friend.addressString,"AAAAAAAAAAAAAAAAAAAAAAAH!",0);
			friend.addressString = temp;
			friend.addressTextField.setText(friend.addressString);
		}
	}

	/**
	 * helper method to close multicast gui and return user to home
	 */
	private void leaveMulticastGui(){
		myFrame.setLocation(friend.myFrame.getLocation());
		myFrame.setVisible(true);
		friend.myFrame.setVisible(false);
		multicastAddress.setText("Multicast address: " + friend.addressString);
	}

	/**
	 * helper method for opening directory gui
	 */
	private void openDirectoryGui(){
		myFrame.setVisible(false);
		folder.directoryAddress.setText(folder.addressString);
		folder.myFrame.setLocation(myFrame.getLocation());
		folder.myFrame.setVisible(true);
	}

	/**
	 * helper method for setting address of directory 
	 * 
	 * save the new directory address and updates directory address in settings
	 */
	private void setDirectoryAddress(){
		folder.addressString = folder.directoryAddress.getText();
		components.settings.updateDirectory(folder.addressString);


		if (components.dirMonitor == null) {
			System.out.println("Error 1");
		}

		components.dirMonitor.test();
		components.dirMonitor.clearVectors();
		components.dirMonitor.changeRoot(folder.addressString);
	}
	
	/**
	 * helper method for closing directory gui and returns user to home
	 */
	private void leaveDirectoryGui(){
		myFrame.setLocation(folder.myFrame.getLocation());
		myFrame.setVisible(true);
		folder.myFrame.setVisible(false);
		directoryAddress.setText("Directory address:" + folder.addressString);
	}
	
	/** 
	 * helper method to open delay gui
	 */
	private void openDelayGui(){
		myFrame.setVisible(false);
		delayGui.myFrame.setLocation(myFrame.getLocation());
		delayGui.delayTime.setText(delayGui.delay + "");
		delayGui.myFrame.setVisible(true);
		components.settings.setDelay(delayGui.delay);
	}
	
	/**
	 * helper method that saves the delay and updates the delay in settings
	 */
	private void setDelay(){
		delayGui.delay = Integer.parseInt(delayGui.delayTime.getText());
		components.settings.setDelay(delayGui.delay);
	}
	
	/**
	 * helper method that leaves the delay gui and returns user to home
	 */
	private void leaveDelayGui(){
		delayGui.myFrame.setVisible(false);
		delayTextBox.setText("Delay: " + delayGui.delay + " milliseconds");
		myFrame.setLocation(delayGui.myFrame.getLocation());
		myFrame.setVisible(true);
	}
	
	/**
	 * helper method that enables auto updates in settings
	 */
	private void enableAutoUpdate(){
		components.settings.set_auto_updates_enabled(true);
	}
	
	/**
	 * helper method that disables auto updates in settings
	 */
	private void disableAutoUpdate(){
		components.settings.set_auto_updates_enabled(false);
	}
	
	/**
	 * helper method that brings back the gui once the tray icon is pressed
	 */
	private void unminimizeGui(){
		myFrame.setVisible(true);
		myFrame.setState(Frame.NORMAL);
	}
	
	/**
	 * helper method for closing program
	 */
	private void closeProgram(){
		System.exit(0);
	}
	
	/** 
	 * getter method for SicComponents 
	 */
	public void setComponents(SicComponents comp){
		components = comp;
	}
}
