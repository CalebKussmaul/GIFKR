package gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;

public class SingleColorChooser {

	private static SingleColorChooser singleton = new SingleColorChooser();
	private JColorChooser chooser;
	private JFrame frame;
	private ChangeListener currentListener;
	
	
	public SingleColorChooser() {
		chooser = new JColorChooser();
		frame = new JFrame("Select color");
		frame.setContentPane(chooser);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {
				removeListener();
			}
		});
	}
	
	public void removeListener() {
		if(currentListener!=null)
			chooser.getSelectionModel().removeChangeListener(currentListener);
	}
	
	public static void showDialog(ChangeListener listener) {
		singleton.show(listener);
	}
	
	public static void showDialog(Color startingColor, ChangeListener listener) {
		singleton.show(startingColor, listener);
	}
	
	private void show(ChangeListener listener) {
		removeListener();
		currentListener = listener;
		chooser.getSelectionModel().addChangeListener(listener);
		frame.setVisible(true);
	}
	
	private void show(Color startingColor, ChangeListener listener) {
		removeListener();
		currentListener = listener;
		chooser.getSelectionModel().addChangeListener(listener);
		chooser.setColor(startingColor);
		frame.setVisible(true);
	}
	
	public static Color getColor() {
		return singleton.chooser.getColor();
	}
	
	
}
