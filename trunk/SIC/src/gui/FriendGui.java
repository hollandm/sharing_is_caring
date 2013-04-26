package gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

public class FriendGui {
	protected JButton setMulticastAddressButton = new JButton("Update Multicast Address");
	protected JFrame myFrame = new JFrame();
	protected JButton homeButton = new JButton("Home");
	protected String addressString;
	protected JFormattedTextField addressTextField = new JFormattedTextField();
	
	public FriendGui(){
		Dimension frameSize = new Dimension(450, 250);
		myFrame.setSize(frameSize);
		myFrame.setTitle("Manage Multicast Address");
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

