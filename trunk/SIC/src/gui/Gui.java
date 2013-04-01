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

import javax.swing.*;

public class Gui implements ActionListener, MouseListener, MouseMotionListener{

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
	protected FriendGui friend = new FriendGui();


	public Gui(){
		Dimension frameSize = new Dimension(600, 400);
		myFrame.setSize(frameSize);
		myFrame.setTitle("SIC");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		friend.homeButton.addActionListener(this);

		Box mainBox = Box.createHorizontalBox();
		Box col1 = Box.createVerticalBox();
		Box col2 = Box.createVerticalBox();
		Box col3 = Box.createVerticalBox();

		/**<---------------------Building Menu------------------------>*/
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);
	

		menu.add(menuItemFriends);
		menuItemFriends.addActionListener(this);
		updateButton.addActionListener(this);

		//auto or manual update radio buttons
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		autoUpdate = new JRadioButtonMenuItem("Auto Update");
		autoUpdate.addActionListener(this);
		autoUpdate.setSelected(true);
		updateButton.setEnabled(false);
		manualUpdate = new JRadioButtonMenuItem("Manual Update");
		manualUpdate.addActionListener(this);
		
		group.add(autoUpdate);
		group.add(manualUpdate);
		menu.add(autoUpdate);
		menu.add(manualUpdate);

		myFrame.setJMenuBar(menuBar);

		/**<-----------------------Column 2---------------------------> */

		col2.add(Box.createVerticalGlue());
		col2.add(updateButton);
		updateButton.setMaximumSize(new Dimension(200, 100));
		updateButton.setMinimumSize(new Dimension(200, 100));
		updateButton.setPreferredSize(new Dimension(200, 100));
		col2.add(Box.createVerticalGlue());

		/**<-----------------------Column 3---------------------------> */

		col3.add(Box.createVerticalGlue());
		col3.add(updateVersion);
		updateVersion.setText("Update Version: " + versionID);


		/**<-------------Add everything to main box-------------------> */
		mainBox.add(Box.createHorizontalGlue());
		mainBox.add(col2);
		mainBox.add(Box.createHorizontalGlue());
		mainBox.add(col3);

		/**<-------------Add everything to top frame-------------------> */

		myFrame.add(mainBox);
		myFrame.setVisible(true);
	}

	// inner class

	class FriendGui implements PropertyChangeListener{

		protected JButton setMulticastAddressButton = new JButton("Update Multicast Address");
		protected JFrame myFrame = new JFrame();
		protected JLabel updateVersion = new JLabel("Update Version: ");
		protected int versionID;
		protected JRadioButtonMenuItem manualUpdate = new JRadioButtonMenuItem("Manual");
		protected JRadioButtonMenuItem autoUpdate = new JRadioButtonMenuItem("Auto");
		protected ButtonGroup updateToggle = new ButtonGroup();
		protected JMenu menu = new JMenu("Menu");
		protected JMenuBar menuBar = new JMenuBar();
		protected JMenuItem menuItemFriends = new JMenuItem("Manage Friends");
		protected JButton homeButton = new JButton("Home");

		public FriendGui(){
			Dimension frameSize = new Dimension(600, 400);
			myFrame.setSize(frameSize);
			myFrame.setTitle("Manage Multicast Address");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Box mainBox = Box.createHorizontalBox();
			Box col1 = Box.createVerticalBox();
			Box col2 = Box.createVerticalBox();
			Box col3 = Box.createVerticalBox();

			/**<-----------------------Column 1---------------------------> */

			/**<-----------------------Column 2---------------------------> */

			JFormattedTextField address = new JFormattedTextField();
			address.setValue("255.255.255.110");
			address.setColumns(16);
			address.addPropertyChangeListener("value", this);

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
			myFrame.setVisible(true);

		}

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			// TODO Auto-generated method stub

		}

	}



	/** create and startup a SwingDemo */
	public static void main(String[] args)
	{
		Gui NiceGui = new Gui();
	}//main




	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == menuItemFriends){
			friend.myFrame.setVisible(true);
			myFrame.setVisible(false);

		}
		else if (arg0.getSource() == updateButton){
			versionID++;
			updateVersion.setText("Update Version: " + versionID);
		}
		else if (arg0.getSource() == friend.homeButton){
			myFrame.setVisible(true);
			friend.myFrame.setVisible(false);
		}
		else if (arg0.getSource() == autoUpdate){
			updateButton.setEnabled(false);
		}
		else if (arg0.getSource() == manualUpdate){
			updateButton.setEnabled(true);
		}

	}





}
