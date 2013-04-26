package gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 * DelayGui
 * 
 * This class creates the delay gui. This gui allows the user to change the 
 * delay rate for congestion control.
 *
 */
public class DelayGui {
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
