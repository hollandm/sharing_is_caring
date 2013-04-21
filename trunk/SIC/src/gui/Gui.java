package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.InetAddress;


import javax.swing.*;

import state.Settings;

public class Gui implements ActionListener{

	protected Settings setting;

	protected JButton updateButton = new JButton("Update");
	protected JFrame myFrame = new JFrame();
	
	protected int versionID = 0;
	protected int delay = 15;

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
	protected JRadioButtonMenuItem manualUpdate = new JRadioButtonMenuItem("Manual");
	protected JRadioButtonMenuItem autoUpdate = new JRadioButtonMenuItem("Auto");
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenuItem menuMulticast = new JMenuItem("Manage Multicast Address");
	protected JMenuItem menuDirectory = new JMenuItem("Manage Shared Folder");
	protected JMenuItem menuDelayUpdate = new JMenuItem("Manage Delay Settings");
	
	/**
	 * Instance of other GUI's
	 */
	protected FriendGui friend = new FriendGui();
	protected FolderGui folder = new FolderGui();
	protected DelayGui delayGui = new DelayGui();
	
	public Gui(){
		Dimension frameSize = new Dimension(600, 400);
		myFrame.setSize(frameSize);
		myFrame.setTitle("SIC");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set listener for home button for each of the other GUIs
		// the home button brings you back to home page (this gui)
		friend.homeButton.addActionListener(this);
		folder.homeButton.addActionListener(this);
		delayGui.homeButton.addActionListener(this);

		// split home gui into 3 rows
		Box mainBox = Box.createVerticalBox();
		Box row1 = Box.createHorizontalBox();
		Box row2 = Box.createHorizontalBox();
		Box row3 = Box.createHorizontalBox();

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
		updateButton.setEnabled(true); // default to auto
		manualUpdate = new JRadioButtonMenuItem("Manual Update");
		manualUpdate.addActionListener(this);

		group.add(autoUpdate);
		group.add(manualUpdate);
		menu.add(autoUpdate);
		menu.add(manualUpdate);

		myFrame.setJMenuBar(menuBar);		

		/**<-----------------------Row 2---------------------------> */

		// add manual update button
		row2.add(Box.createHorizontalGlue());
		row2.add(updateButton);
		updateButton.addActionListener(this);
		updateButton.setMaximumSize(new Dimension(200, 100));
		updateButton.setMinimumSize(new Dimension(200, 100));
		updateButton.setPreferredSize(new Dimension(200, 100));
		row2.add(Box.createHorizontalGlue());

		/**<-----------------------Row 3---------------------------> */

		// add information box
		Box info = Box.createVerticalBox();
		row3.add(Box.createHorizontalGlue());
		info.add(updateVersion);
		updateVersion.setText("Update Version: " + versionID);
		info.add(multicastAddress);
		multicastAddress.setText("Multicast address: " + friend.addressString);
		info.add(directoryAddress);
		directoryAddress.setText("Directory address: " + folder.addressString);
		info.add(delayTextBox);
		delayTextBox.setText("Delay: " + delay + " milliseconds");

		row3.add(info);

		/**<-------------Add everything to main box-------------------> */
		
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(row2);
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(row3);

		/**<-------------Add everything to top frame-------------------> */

		myFrame.add(mainBox);
		myFrame.setVisible(true);
	}

	// inner class

	class FriendGui implements ActionListener{

		protected JButton setMulticastAddressButton = new JButton("Update Multicast Address");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected String addressString = "255.255.255.110";
		protected JFormattedTextField addressTextField = new JFormattedTextField();

		public FriendGui(){
			Dimension frameSize = new Dimension(600, 400);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Multicast Address");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			setMulticastAddressButton.addActionListener(this);

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


		@Override
		// set multicast address only if it is a valid one
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == setMulticastAddressButton){
				String temp = addressString;// = address.getText();
				try{
					addressString = addressTextField.getText();
					setting.set_multicastGroup(InetAddress.getByName(addressString.trim()));
				}
				catch (Exception e){
					JOptionPane jop = new JOptionPane();
					jop.showMessageDialog(myFrame,"Invalid IP address: "+addressString,"AAAAAAAAAAAAAAAAAAAAAAAH!",0);
					addressString = temp;
					addressTextField.setText(addressString);
				}
			}			
		}

	}

	class FolderGui implements ActionListener{

		protected JButton setFolderAddressButton = new JButton("Update Folder Directory");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected String addressString = "P:\\Folder";
		protected JFormattedTextField directoryAddress = new JFormattedTextField();

		public FolderGui(){
			Dimension frameSize = new Dimension(600, 400);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Shared Directory");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			setFolderAddressButton.addActionListener(this);

			
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

		@Override
		// set directory address
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == setFolderAddressButton){
				addressString = directoryAddress.getText();
				setting.updateDirectory(addressString);
			}			
		}
	}

	class DelayGui implements ActionListener{

		protected JButton setDelay = new JButton("Update Delay");
		protected JFrame myFrame = new JFrame();
		protected JButton homeButton = new JButton("Home");
		protected JFormattedTextField delayTime = new JFormattedTextField();
		protected int delay = 15;

		public DelayGui(){
			Dimension frameSize = new Dimension(600, 400);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Delay Settings");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			setDelay.addActionListener(this);

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

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// set delay
			if(arg0.getSource() == setDelay){
				delay = Integer.parseInt(delayTime.getText().trim());
				//setting.updateDelay(delay);
				System.err.println("delay updated");
			}	
		}
	}

	/** create and startup a SwingDemo */
	public static void main(String[] args)
	{
		Gui NiceGui = new Gui();
	}//main

	public void setSettings(Settings newSetting)
	{
		setting = newSetting;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		/**<----------------Update button------------------------>*/
		// update button updates version number
		if (arg0.getSource() == updateButton){
			versionID++;
			updateVersion.setText("Update Version: " + versionID);
		}
		
		/**<----------------Multicast Gui------------------------>*/
		// if multicast menu item is selected, open up multicast gui
		else if(arg0.getSource() == menuMulticast){
			friend.addressTextField.setValue(friend.addressString);
			friend.myFrame.setVisible(true);
			folder.myFrame.setVisible(false);
			myFrame.setVisible(false);
		}
		
		// in multicast gui, if pressed home, go back to main menu
		else if (arg0.getSource() == friend.homeButton){
			myFrame.setVisible(true);
			friend.myFrame.setVisible(false);
			multicastAddress.setText("Multicast address: " + friend.addressString);
		}

		/**<----------------Directory Gui------------------------>*/

		// if clicked on directory management in menu, open new gui
		else if(arg0.getSource() == menuDirectory){
			myFrame.setVisible(false);
			friend.myFrame.setVisible(false);
			folder.myFrame.setVisible(true);
		}
		
		// if in directory management, and pressed home, go to home gui
		// also set text in home gui to reflect any changes
		else if(arg0.getSource() == folder.homeButton){
			myFrame.setVisible(true);
			folder.myFrame.setVisible(false);
			directoryAddress.setText("Directory address: " + folder.addressString);
		}

		/**<----------------Delay Gui------------------------>*/

		// open delay setting gui
		else if(arg0.getSource() == menuDelayUpdate){
			myFrame.setVisible(false);
			delayGui.delayTime.setText(delayGui.delay + "");
			delay = delayGui.delay;
			delayGui.myFrame.setVisible(true);
		}

		// update delay string on home page once delay time has been updated
		else if(arg0.getSource() == delayGui.homeButton){
			delayGui.myFrame.setVisible(false);
			delay = delayGui.delay;
			delayTextBox.setText("Delay: " + delay + " milliseconds");
			myFrame.setVisible(true);
		}
		
		/**<----------------Auto/manual update------------------------>*/

		// set toggle for auto update and manual update
		else if (arg0.getSource() == autoUpdate){
			updateButton.setEnabled(false);
			setting.set_auto_updates_enabled(true);
		}
		else if (arg0.getSource() == manualUpdate){
			updateButton.setEnabled(true);
			setting.set_auto_updates_enabled(false);
		}
	}
}
