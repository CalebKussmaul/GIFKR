package gui;

import java.awt.CardLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ChannelSettingsPanel extends JPanel {

	private static final long serialVersionUID = 7948927184196130090L;
	
	private MainPanel r;
	
	private JCheckBox channelBox;
	
	private JPanel cardPanel;
	private JTabbedPane channelPane;
	
	private SettingsPanel allSets;
	private SettingsPanel rSets;
	private SettingsPanel gSets;
	private SettingsPanel bSets;
		
	public ChannelSettingsPanel(MainPanel r) {
		this.r = r;
		initializeComponents();
		addActionListeners();
		
		add(cardPanel);
	}
	
	public void initializeComponents() {
		channelBox = new JCheckBox("Filter by channel", false);
		
		allSets = new SettingsPanel(r);
		rSets = new SettingsPanel(r);
		gSets = new SettingsPanel(r);
		bSets = new SettingsPanel(r);
		
		channelPane = new JTabbedPane();
		channelPane.add("Red", rSets);
		channelPane.add("Green", gSets);
		channelPane.addTab("Blue", bSets);
		
		cardPanel = new JPanel(new CardLayout());
		cardPanel.add(allSets, "all");
		cardPanel.add(channelPane, "rgb");
	}
	
	
	public void addActionListeners() {
		
		channelBox.addActionListener(ae -> {
			CardLayout cl = (CardLayout)(cardPanel.getLayout());
			cl.show(this, channelBox.isSelected() ? "all" : "rgb");
		});	
	}
}
