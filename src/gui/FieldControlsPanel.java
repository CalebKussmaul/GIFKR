package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FieldControlsPanel extends JPanel {

	private static final long serialVersionUID = -4157573040305315271L;
	private List<FieldControl> controls;
	
	public FieldControlsPanel(List<FieldControl> controls, String borderLabel, boolean channel) {
		super(new BorderLayout());
		setOpaque(false);
		
		this.controls = controls;
		
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		
		GridBagConstraints gbc	= new GridBagConstraints();
		gbc.fill 				= GridBagConstraints.HORIZONTAL;
		gbc.anchor				= GridBagConstraints.CENTER;
		gbc.gridx				= 0;
		gbc.gridy				= 0;
		gbc.weighty				= 1;
		gbc.weightx				= 0;

		for(FieldControl control : controls) {

			JLabel label = control.getLabel();

			p.add(label, gbc);
			gbc.gridx++;
			
			gbc.weightx=1;
			p.add(control, gbc);
			gbc.gridwidth=1;
			gbc.gridx=0;
			gbc.weightx=0;
			gbc.gridy++;
		}
		
		setOpaque(false);
		if(borderLabel != null && !channel)
			setBorder(BorderFactory.createTitledBorder(borderLabel));
		add(p, BorderLayout.NORTH);
	}
	
	public void setX(float x) {
		for(FieldControl control : controls) {
			control.setX(x);
		}
	}
	
	public void updateChangedValues() {
		
//		for(FieldControl control : controls) {
//			control.setListenerEnabled(false);
//		}
//		
//		for(FieldControl control : controls) {
//			if(control.getValue() == null || !control.getValue().equals(control.getFieldValue())) {
//				control.setValue(control.getFieldValue());
//			}
//		}
//		
//		for(FieldControl control : controls) {
//			control.setListenerEnabled(true);
//		}
	}
	
	public void setAnimationMode(boolean show) {
		for(FieldControl control: controls) {
//			control.setListenerEnabled(false);
			control.allowAnimationControls(show);
//			control.setListenerEnabled(true);
		}
		revalidate();
	}
	
	public void hideBorder() {
		setBorder(BorderFactory.createEmptyBorder());
	}
}
