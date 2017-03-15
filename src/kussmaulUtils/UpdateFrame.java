package kussmaulUtils;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UpdateFrame extends JFrame {

	private static final long serialVersionUID = 4427834004362539522L;
	
	private Program p;
	private String version;
	
	private JTextPane infoPane;
	private JButton updateButton;
	
	public UpdateFrame(Program p, String version) {
		
		super("Update");
		
		this.p = p;
		this.version = version;
		
		initializeComponents();
		addActionListeners();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc	= new GridBagConstraints(); 
		gbc.weightx				= 1;
		gbc.weighty				= 0;
		gbc.gridx				= 0;
		gbc.gridy				= 0;
		gbc.gridwidth 			= 3;
		gbc.anchor 				= GridBagConstraints.NORTH;
		gbc.fill				= GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5,5,5,5);
		
		add(infoPane, gbc);
		gbc.gridy++;
		
		gbc.fill				= GridBagConstraints.NONE;

		
		add(updateButton, gbc);
		
		setAlwaysOnTop(true);
		setSize(450, 150);
		setResizable(false);
	}

	private void initializeComponents() {
		infoPane		= new JTextPane();
		infoPane.setText("You are using version "+version+" but the current version is " +CalebKussmaul.getLatestVersion(p)+". It is strongly reccomended that you update to take advantage of the latest additions and fixes.");
		
		StyledDocument doc = infoPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		infoPane.setEditable(false);
		infoPane.setOpaque(false);
		updateButton 	= new JButton("Download update...");
	}

	private void addActionListeners() {
		updateButton.addActionListener(ae ->{
			try {
				Desktop.getDesktop().browse(new URI(CalebKussmaul.getDownloadOnPlatform(p)));
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
