package gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 * FolderGui
 * 
 * Creates the folder gui, which allows the user to change the directory
 * path the user wants to use.
 *
 */
public class FolderGui {
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
