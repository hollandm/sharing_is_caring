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
import java.net.InetAddress;
import java.net.InetAddress;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import Main.SicComponents;

import state.Settings;

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
		folder.addressString = components.settings.getDirectory();
		friend.addressString = components.settings.get_multicastGroup().toString().substring(1);
		delayGui.delay = components.settings.getDelay();
		
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

	// inner class

	class FriendGui{

		protected JButton setMulticastAddressButton = new JButton("Update Multicast Address");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected String addressString;// = components.settings.get_multicastGroup().toString();
		protected JFormattedTextField addressTextField = new JFormattedTextField();

		public FriendGui(){
			Dimension frameSize = new Dimension(450, 250);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Multicast Address");
			//myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			myFrame.setResizable(false);

			// split friend gui into 3 columns
			Box mainBox = Box.createHorizontalBox();
			Box col1 = Box.createVerticalBox();
			Box col2 = Box.createVerticalBox();
			Box col3 = Box.createVerticalBox();


			/**<-----------------------Column 2---------------------------> */

			addressTextField.setValue(addressString);

			// add update multicast button
			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setMulticastAddressButton);
			buttonBox.add(Box.createHorizontalGlue());
			setMulticastAddressButton.setMaximumSize(new Dimension(200, 100));
			setMulticastAddressButton.setMinimumSize(new Dimension(200, 100));
			setMulticastAddressButton.setPreferredSize(new Dimension(200, 100));

			// add multicast text field
			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			addressBox.add(addressTextField);
			addressBox.add(Box.createHorizontalGlue());
			addressTextField.setPreferredSize(new Dimension(200, 50));
			addressTextField.setMinimumSize(new Dimension(200, 50));
			addressTextField.setMaximumSize(new Dimension(200, 50));
			
			col2.add(addressBox);
			col2.add(buttonBox);
			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

			// add home button
			col3.add(Box.createVerticalGlue());
			homeButton.setMaximumSize(new Dimension(100, 50));
			homeButton.setMinimumSize(new Dimension(100, 50));
			homeButton.setPreferredSize(new Dimension(100, 50));
			col3.add(homeButton);

			/**<-------------Add everything to main box-------------------> */
			
			mainBox.add(col1);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col2);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col3);

			/**<-------------Add everything to top frame-------------------> */
			myFrame.add(mainBox);
			myFrame.setVisible(false);
		}

	}

	class FolderGui{

		protected JButton setFolderAddressButton = new JButton("Update Folder Directory");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected String addressString;// = components.settings.getDirectory();
		protected JFormattedTextField directoryAddress = new JFormattedTextField();

		public FolderGui(){
			Dimension frameSize = new Dimension(450, 250);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Shared Directory");
			myFrame.setResizable(false);


			Box mainBox = Box.createHorizontalBox();
			Box col1 = Box.createVerticalBox();
			Box col2 = Box.createVerticalBox();
			Box col3 = Box.createVerticalBox();

			/**<-----------------------Column 1---------------------------> */

			/**<-----------------------Column 2---------------------------> */

			directoryAddress.setValue(addressString);

			// add button to set folder address
			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setFolderAddressButton);
			buttonBox.add(Box.createHorizontalGlue());
			setFolderAddressButton.setMaximumSize(new Dimension(200, 100));
			setFolderAddressButton.setMinimumSize(new Dimension(200, 100));
			setFolderAddressButton.setPreferredSize(new Dimension(200, 100));

			
			// add directory address text field
			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			addressBox.add(directoryAddress);
			addressBox.add(Box.createHorizontalGlue());
			directoryAddress.setPreferredSize(new Dimension(200, 50));
			directoryAddress.setMinimumSize(new Dimension(200, 50));
			directoryAddress.setMaximumSize(new Dimension(200, 50));

			col2.add(addressBox);
			col2.add(buttonBox);
			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

			// add home button
			col3.add(Box.createVerticalGlue());
			homeButton.setMaximumSize(new Dimension(100, 50));
			homeButton.setMinimumSize(new Dimension(100, 50));
			homeButton.setPreferredSize(new Dimension(100, 50));
			col3.add(homeButton);

			/**<-------------Add everything to main box-------------------> */
			
			mainBox.add(col1);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col2);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col3);

			/**<-------------Add everything to top frame-------------------> */

			myFrame.add(mainBox);
			myFrame.setVisible(false);
		}
	}

	class DelayGui{

		protected JButton setDelay = new JButton("Update Delay");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected JFormattedTextField delayTime = new JFormattedTextField();
		protected int delay;

		public DelayGui(){
			Dimension frameSize = new Dimension(450, 250);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Delay Settings");
			//myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			myFrame.setResizable(false);


			Box mainBox = Box.createHorizontalBox();
			Box col1 = Box.createVerticalBox();
			Box col2 = Box.createVerticalBox();
			Box col3 = Box.createVerticalBox();

			/**<-----------------------Column 1---------------------------> */

			/**<-----------------------Column 2---------------------------> */

			// add update delay button
			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setDelay);
			buttonBox.add(Box.createHorizontalGlue());
			setDelay.setMaximumSize(new Dimension(200, 100));
			setDelay.setMinimumSize(new Dimension(200, 100));
			setDelay.setPreferredSize(new Dimension(200, 100));

			// add delay text field
			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			delayTime.setText(delay + "");
			addressBox.add(delayTime);
			addressBox.add(Box.createHorizontalGlue());
			delayTime.setPreferredSize(new Dimension(200, 50));
			delayTime.setMinimumSize(new Dimension(200, 50));
			delayTime.setMaximumSize(new Dimension(200, 50));
			
			col2.add(addressBox);
			col2.add(buttonBox);
			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

			// add home button
			col3.add(Box.createVerticalGlue());
			homeButton.setMaximumSize(new Dimension(100, 50));
			homeButton.setMinimumSize(new Dimension(100, 50));
			homeButton.setPreferredSize(new Dimension(100, 50));
			col3.add(homeButton);

			/**<-------------Add everything to main box-------------------> */
			
			mainBox.add(col1);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col2);
			mainBox.add(Box.createHorizontalGlue());
			mainBox.add(col3);

			/**<-------------Add everything to top frame-------------------> */

			myFrame.add(mainBox);
			myFrame.setVisible(false);
			myFrame.setVisible(false);
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		/**<----------------------Multicast Gui--------------------------->*/
		// if multicast menu item is selected, open up multicast gui
		if(arg0.getSource() == menuMulticast){
			friend.addressTextField.setValue(friend.addressString);
			friend.myFrame.setLocation(myFrame.getLocation());
			friend.myFrame.setVisible(true);
			myFrame.setVisible(false);
		}
		
		// set multicast address only if it is a valid one
		else if(arg0.getSource() == friend.setMulticastAddressButton){
			String temp = friend.addressString;// = address.getText();
			try{
				friend.addressString = friend.addressTextField.getText();
				components.settings.set_multicastGroup(
						InetAddress.getByName(friend.addressString.trim()));
			}
			catch (Exception e){
				JOptionPane.showMessageDialog(myFrame,"Invalid IP address: "
						+ friend.addressString,"AAAAAAAAAAAAAAAAAAAAAAAH!",0);
				friend.addressString = temp;
				friend.addressTextField.setText(friend.addressString);
			}
		}	
		
		// in multicast gui, if pressed home, go back to main menu
		else if (arg0.getSource() == friend.homeButton){
			myFrame.setLocation(friend.myFrame.getLocation());
			myFrame.setVisible(true);
			friend.myFrame.setVisible(false);
			multicastAddress.setText("Multicast address: " + friend.addressString);
		}
		
		/**<--------------------Directory Gui---------------------------->*/

		// if clicked on directory management in menu, open new gui
		else if(arg0.getSource() == menuDirectory){
			myFrame.setVisible(false);
			folder.directoryAddress.setText(folder.addressString);
			folder.myFrame.setLocation(myFrame.getLocation());
			folder.myFrame.setVisible(true);
		}
		
		// set directory address
		else if(arg0.getSource() == folder.setFolderAddressButton){
			folder.addressString = folder.directoryAddress.getText();
			components.settings.updateDirectory(folder.addressString);
			components.dirMonitor.setPath(folder.addressString);
		}	
		
		// if in directory management, and pressed home, go to home gui
		// also set text in home gui to reflect any changes
		else if(arg0.getSource() == folder.homeButton){
			myFrame.setLocation(folder.myFrame.getLocation());
			myFrame.setVisible(true);
			folder.myFrame.setVisible(false);
			directoryAddress.setText("Directory address:" + folder.addressString);
		}

		/**<-------------------------Delay Gui-------------------------->*/

		// open delay gui when delay menu is pressed
		else if(arg0.getSource() == menuDelayUpdate){
			myFrame.setVisible(false);
			delayGui.myFrame.setLocation(myFrame.getLocation());
			delayGui.delayTime.setText(delayGui.delay + "");
			delayGui.myFrame.setVisible(true);
			System.err.println("success! delay updated");
			components.settings.setDelay(delayGui.delay);
		}

		// set delay
		else if(arg0.getSource() == delayGui.setDelay){
			delayGui.delay = Integer.parseInt(delayGui.delayTime.getText());
			components.settings.setDelay(delayGui.delay);
		}	
		
		// update delay string on home page once delay time has been updated
		else if(arg0.getSource() == delayGui.homeButton){
			delayGui.myFrame.setVisible(false);
			delayTextBox.setText("Delay: " + delayGui.delay + " milliseconds");
			myFrame.setLocation(delayGui.myFrame.getLocation());
			myFrame.setVisible(true);
		}
		
		/**<-----------------Auto/manual update------------------------>*/

		// set toggle for auto update and manual update
		else if (arg0.getSource() == autoUpdate){
			components.settings.set_auto_updates_enabled(true);
		}
		else if (arg0.getSource() == disableUpdate){
			components.settings.set_auto_updates_enabled(false);
		}
		
		/**<------------------Exit program----------------------------->*/
		
		// exits when user chooses to exit program
		else if (arg0.getSource() == quit){
			System.exit(0);
		}
		
		/**<-----------------TrayIcon Stuff---------------------------->*/
		// when icon is pressed, brings up the gui again
		else if (arg0.getSource() == icon){
			myFrame.setVisible(true);
			myFrame.setState(Frame.NORMAL);
		}
		
		// right click on icon, chose to quit
		else if (arg0.getSource() == exitItem){
			System.exit(0);
		}
	}
	
	/** create and startup a SwingDemo */
	public static void main(String[] args)
	{
		//Gui NiceGui = new Gui("c:/desktop", );
	}//main

	/** getter method for SicComponents */
	public void setComponents(SicComponents comp){
		components = comp;
	}
}
