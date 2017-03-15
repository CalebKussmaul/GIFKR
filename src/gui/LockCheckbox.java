package gui;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import kussmaulUtils.ImageTools;

public class LockCheckbox extends JCheckBox {

	private static BufferedImage locked = ImageTools.scaleToHeight(ImageTools.getResourceImage("locked.png"), new JCheckBox().getPreferredSize().height-10, false);
	private static BufferedImage unlocked = ImageTools.scaleToHeight(ImageTools.getResourceImage("unlocked.png"), new JCheckBox().getPreferredSize().height-10, false);
	
	private static final long serialVersionUID = -7161039114280867451L;
	
	public LockCheckbox() {
		super(new ImageIcon(locked));
		setSelectedIcon(new ImageIcon(unlocked));
	}
	
	public LockCheckbox(String s) {
		super(s, new ImageIcon(locked));
		setSelectedIcon(new ImageIcon(unlocked));
	}
	
	

}
