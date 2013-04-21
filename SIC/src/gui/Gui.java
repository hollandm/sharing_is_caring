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

	protected JButton updateButton = new JButton("Update");
	protected JFrame myFrame = new JFrame();
	protected JLabel updateVersion = new JLabel("Update Version: ");
	protected int versionID = 0;
	protected JRadioButtonMenuItem manualUpdate = new JRadioButtonMenuItem("Manual");
	protected JRadioButtonMenuItem autoUpdate = new JRadioButtonMenuItem("Auto");
	protected ButtonGroup updateToggle = new ButtonGroup();
	protected JMenu menu = new JMenu("Menu");
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenuItem menuItemFriends = new JMenuItem("Manage Multicast Address");
	protected JMenuItem menuFolder = new JMenuItem("Manage Shared Folder");
	protected JMenuItem delayUpdate = new JMenuItem("Manage Delay Settings");
	protected FriendGui friend = new FriendGui();
	protected FolderGui folder = new FolderGui();
	protected DelayGui delayGui = new DelayGui();
	protected JLabel multicastAddress = new JLabel("Multicast Address: ");
	protected JLabel directoryAddress = new JLabel("Directory Address: ");
	protected Settings setting;
	protected JFormattedTextField delayTime = new JFormattedTextField();
	protected int delay = 15;
	protected JButton updateDelay = new JButton("Update Delay");
	protected JLabel delayTextBox = new JLabel("Delay");



	public Gui(){
		Dimension frameSize = new Dimension(600, 400);
		myFrame.setSize(frameSize);
		myFrame.setTitle("SIC");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		friend.homeButton.addActionListener(this);
		folder.homeButton.addActionListener(this);
		delayGui.homeButton.addActionListener(this);
		//setting.set_auto_updates_enabled(false);

		Box mainBox = Box.createVerticalBox();
		Box col1 = Box.createHorizontalBox();
		Box col2 = Box.createHorizontalBox();
		Box col3 = Box.createHorizontalBox();

		/**<---------------------Building Menu------------------------>*/
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);


		menu.add(menuItemFriends);
		menuItemFriends.addActionListener(this);
		updateButton.addActionListener(this);

		menu.addSeparator();
		menuFolder.addActionListener(this);
		menu.add(menuFolder);

		menu.addSeparator();
		delayUpdate.addActionListener(this);
		menu.add(delayUpdate);

		//auto or manual update radio buttons
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		autoUpdate = new JRadioButtonMenuItem("Auto Update");
		autoUpdate.addActionListener(this);
		updateButton.setEnabled(true);
		manualUpdate = new JRadioButtonMenuItem("Manual Update");
		manualUpdate.addActionListener(this);
		manualUpdate.setSelected(true);

		group.add(autoUpdate);
		group.add(manualUpdate);
		menu.add(autoUpdate);
		menu.add(manualUpdate);


		myFrame.setJMenuBar(menuBar);		

		/**<-----------------------Row 2---------------------------> */

		col2.add(Box.createHorizontalGlue());
		col2.add(updateButton);
		updateButton.setMaximumSize(new Dimension(200, 100));
		updateButton.setMinimumSize(new Dimension(200, 100));
		updateButton.setPreferredSize(new Dimension(200, 100));
		col2.add(Box.createHorizontalGlue());

		/**<-----------------------Row 3---------------------------> */

		Box info = Box.createVerticalBox();
		col3.add(Box.createHorizontalGlue());
		info.add(updateVersion);
		updateVersion.setText("Update Version: " + versionID);
		info.add(multicastAddress);
		multicastAddress.setText("Multicast address: " + friend.addressString);
		info.add(directoryAddress);
		directoryAddress.setText("Directory address: " + folder.addressString);
		info.add(delayTextBox);
		delayTextBox.setText("Delay: " + delay + " milliseconds");

		col3.add(info);

		/**<-------------Add everything to main box-------------------> */
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(col2);
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(col3);

		/**<-------------Add everything to top frame-------------------> */

		myFrame.add(mainBox);
		myFrame.setVisible(true);
	}

	// inner class

	class FriendGui implements ActionListener{

		protected JButton setMulticastAddressButton = new JButton("Update Multicast Address");
		protected Frame myFrame = new Frame();
		protected JButton homeButton = new JButton("Home");
		protected String addressString = "255.255.255.110";
		protected JFormattedTextField address = new JFormattedTextField();

		public FriendGui(){
			Dimension frameSize = new Dimension(600, 400);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Multicast Address");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


			Box mainBox = Box.createHorizontalBox();
			Box col1 = Box.createVerticalBox();
			Box col2 = Box.createVerticalBox();
			Box col3 = Box.createVerticalBox();


			/**<-----------------------Column 2---------------------------> */

			address.setValue(addressString);

			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setMulticastAddressButton);
			buttonBox.add(Box.createHorizontalGlue());

			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			addressBox.add(address);
			addressBox.add(Box.createHorizontalGlue());

			col2.add(addressBox);
			col2.add(buttonBox);

			setMulticastAddressButton.setMaximumSize(new Dimension(200, 100));
			setMulticastAddressButton.setMinimumSize(new Dimension(200, 100));
			setMulticastAddressButton.setPreferredSize(new Dimension(200, 100));
			setMulticastAddressButton.addActionListener(this);

			address.setPreferredSize(new Dimension(200, 50));
			address.setMinimumSize(new Dimension(200, 50));
			address.setMaximumSize(new Dimension(200, 50));

			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

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
		// if set is pressed, update addressString
		// set in settings
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == setMulticastAddressButton){
				String temp = addressString;// = address.getText();
				try{
					addressString = address.getText();
					setting.set_multicastGroup(InetAddress.getByName(addressString.trim()));
				}
				catch (Exception e){
					JOptionPane jop = new JOptionPane();
					jop.showMessageDialog(myFrame,"Invalid IP address: "+addressString,"AAAAAAAAAAAAAAAAAAAAAAAH!",0);
					addressString = temp;
					address.setText(addressString);
				}
			}			
		}

	}

	class Frame extends JFrame{
		Frame(){
			setVisible(false);
		}
	}

	class FolderGui implements ActionListener{

		protected JButton setFolderAddressButton = new JButton("Update Folder Directory");
		protected Frame myFrame = new Frame();
		protected ButtonGroup updateToggle = new ButtonGroup();
		protected JButton homeButton = new JButton("Home");
		protected String addressString = "P:\\Folder";
		protected JFormattedTextField address = new JFormattedTextField();

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

			address.setValue(addressString);

			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setFolderAddressButton);
			buttonBox.add(Box.createHorizontalGlue());

			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			addressBox.add(address);
			addressBox.add(Box.createHorizontalGlue());

			col2.add(addressBox);
			col2.add(buttonBox);

			setFolderAddressButton.setMaximumSize(new Dimension(200, 100));
			setFolderAddressButton.setMinimumSize(new Dimension(200, 100));
			setFolderAddressButton.setPreferredSize(new Dimension(200, 100));
			setFolderAddressButton.addActionListener(this);

			address.setPreferredSize(new Dimension(200, 50));
			address.setMinimumSize(new Dimension(200, 50));
			address.setMaximumSize(new Dimension(200, 50));

			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

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
		// set directory address
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == setFolderAddressButton){
				addressString = address.getText();
				setting.updateDirectory(addressString);
			}			
		}
	}

	class DelayGui implements ActionListener{

		protected JButton setDelay = new JButton("Update Delay");
		protected Frame myFrame = new Frame();
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

			col2.add(Box.createVerticalGlue());
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(setDelay);
			buttonBox.add(Box.createHorizontalGlue());

			Box addressBox = Box.createHorizontalBox();
			addressBox.add(Box.createHorizontalGlue());
			delayTime.setText(delay + "");
			addressBox.add(delayTime);
			addressBox.add(Box.createHorizontalGlue());

			col2.add(addressBox);
			col2.add(buttonBox);

			setDelay.setMaximumSize(new Dimension(200, 100));
			setDelay.setMinimumSize(new Dimension(200, 100));
			setDelay.setPreferredSize(new Dimension(200, 100));
			setDelay.addActionListener(this);

			delayTime.setPreferredSize(new Dimension(200, 50));
			delayTime.setMinimumSize(new Dimension(200, 50));
			delayTime.setMaximumSize(new Dimension(200, 50));

			col2.add(Box.createVerticalGlue());

			/**<-----------------------Column 3---------------------------> */

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
			if(arg0.getSource() == setDelay){
				delay = Integer.parseInt(delayTime.getText().trim());
				setting.updateDelay(delay);
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
		// if multicast menu item is selected, open up new window
		if(arg0.getSource() == menuItemFriends){
			friend.address.setValue(friend.addressString);
			friend.myFrame.setVisible(true);
			folder.myFrame.setVisible(false);
			myFrame.setVisible(false);
		}

		// update button updates version number
		else if (arg0.getSource() == updateButton){
			versionID++;
			updateVersion.setText("Update Version: " + versionID);
		}

		// in multicast gui, if pressed home, go back to main menu
		else if (arg0.getSource() == friend.homeButton){
			myFrame.setVisible(true);
			friend.myFrame.setVisible(false);
			multicastAddress.setText("Multicast address: " + friend.addressString);
		}

		// set toggle for auto update and manual update
		else if (arg0.getSource() == autoUpdate){
			updateButton.setEnabled(false);
			setting.set_auto_updates_enabled(true);
		}
		else if (arg0.getSource() == manualUpdate){
			updateButton.setEnabled(true);
			setting.set_auto_updates_enabled(false);
		}

		// if in directory management, and pressed home, go to home gui
		// also set text in home gui to reflect any changes
		else if(arg0.getSource() == folder.homeButton){
			myFrame.setVisible(true);
			folder.myFrame.setVisible(false);
			directoryAddress.setText("Directory address: " + folder.addressString);
		}

		// if clicked on directory management in menu, open new gui
		else if(arg0.getSource() == menuFolder){
			myFrame.setVisible(false);
			friend.myFrame.setVisible(false);
			folder.myFrame.setVisible(true);
		}

		else if(arg0.getSource() == this.delayUpdate){
			myFrame.setVisible(false);
			delayGui.myFrame.setVisible(true);
		}
		
		else if(arg0.getSource() == delayGui.homeButton){
			delayGui.myFrame.setVisible(false);
			delay = delayGui.delay;
			delayTextBox.setText("Delay: " + delay + " milliseconds");
			myFrame.setVisible(true);
		}
	}
}
