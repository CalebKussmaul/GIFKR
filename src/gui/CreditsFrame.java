package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import utils.ImageTools;
import utils.ViewUtils;

public class CreditsFrame extends JFrame {

	private static final long serialVersionUID = -4062924878829150553L;

	
	public CreditsFrame() {
		super("Credits");
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = ViewUtils.createGBC();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty=1;
		gbc.insets = new Insets(0, 10, 10, 10);
		
		JLabel logoLabel = new JLabel(new ImageIcon(ImageTools.scaleToHeight(ImageTools.getResourceImage("icon.png"), 100, false)));
		logoLabel.setText("<HTML>GIFKR &copy; 2017 Caleb Kussmaul");
		logoLabel.setHorizontalTextPosition(JLabel.CENTER);
		logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
		
		add(logoLabel, gbc);
		
		gbc.gridy++;
		gbc.weighty=0;
		
		JTextPane dedicationPane = new JTextPane();
		
		dedicationPane.setText("Dedicated to my dad, Wes Kussmaul, for inspiring my passion for programming, creativity, and entrepreneurship.");
		dedicationPane.setOpaque(false);
		dedicationPane.setEditable(false);
		ViewUtils.centerText(dedicationPane);
		
		add(dedicationPane, gbc);
		gbc.gridy++;
		
		JTextPane copyrightPane = new JTextPane();
		copyrightPane.setText("Referenced libraries:\nOpen-Imaging under Apache 2.0\nJavaCV under Apache 2.0\nFFmpeg under LGPL 2.1");
		add(copyrightPane, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		
		
		JCheckBox doNotShowBox = new JCheckBox("Do not show again", !GIFKRPrefs.showCreditsFrame());
		doNotShowBox.addChangeListener(ce -> {
			GIFKRPrefs.setShowCreditsFrame(!doNotShowBox.isSelected());
		});
		
		add(doNotShowBox, gbc);
		
		setSize(500, 300);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
	}
	
	public static void main(String[] args) {
		
		CreditsFrame cf = new CreditsFrame();
		cf.setVisible(true);
		cf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
