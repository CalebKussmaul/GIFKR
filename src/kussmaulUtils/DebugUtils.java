package kussmaulUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class DebugUtils {

	
	public static void displayImage(Image img) {
		JFrame f = new JFrame("DEBUG IMG");
		
		JLabel label = new JLabel(new ImageIcon(img));
		label.setBorder(BorderFactory.createLineBorder(Color.RED));
		f.setLayout(new BorderLayout());
		f.add(label, BorderLayout.WEST);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
