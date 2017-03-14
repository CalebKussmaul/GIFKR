package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import filter.base.FilterLoader;
import filter.base.ImageFilter;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = -8746574196794838966L;

	private boolean channel;
	private SettingsPanel setP;
	
	private JComboBox<ImageFilter> filterBox;
	private FieldControlsPanel fcp;
	private JScrollPane settingsPane;
	
	public FilterPanel(SettingsPanel setP, boolean channel) {
		this.setP = setP;
		this.channel = channel;
		
		initializeComponents();
		addActionListeners();
		
		setOpaque(false);
		setLayout(new BorderLayout());
		add(filterBox, BorderLayout.NORTH);
		add(settingsPane, BorderLayout.CENTER);
		
	}
	
	private void initializeComponents() {
		filterBox	= FilterLoader.getFilterBox();
		fcp			= getFilter().getSettingsPanel(setP.getRefreshable(), channel);
		
		settingsPane= new JScrollPane(fcp);
		settingsPane.setMinimumSize(new Dimension(fcp.getPreferredSize().width, Math.min(100, fcp.getMinimumSize().height)));
		settingsPane.getViewport().setOpaque(false);
		settingsPane.setBorder(BorderFactory.createEmptyBorder());
		settingsPane.setOpaque(false);
	}
	private void addActionListeners() {
		filterBox.addActionListener(ae -> {
			fcp = getFilter().getSettingsPanel(setP.getRefreshable(), channel);
			settingsPane.setViewportView(fcp);
//			animationControlP.setFCP(fcp);
			fcp.setAnimationMode(setP.isAnimationMode());
			setP.refreshImageFilters();
		});
	}
	
	public ImageFilter getFilter() {
		return (ImageFilter) filterBox.getSelectedItem();
	}
	
	public FieldControlsPanel getFCP() {
		return fcp;
	}
	
	public void setAnimationMode(boolean animated) {
		fcp.setAnimationMode(animated);
	}
	
}
