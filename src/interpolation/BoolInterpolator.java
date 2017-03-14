package interpolation;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

public class BoolInterpolator extends FloatInterpolator {
	
	private static final long serialVersionUID = 6650429548952128775L;

	private JCheckBox manualBox;
	
	public BoolInterpolator(float startVal, GetSet gs, ChangeListener... listeners) {
		super(startVal, gs, listeners);
		manualBox = new JCheckBox("Enable", startVal > .5f);
		
		manualBox.addChangeListener(ce -> {
			fireChangeEvent();
		});
	}
	
	@Override
	public void paintGraph(Graphics2D g, int width, int height) {
		g.setColor(new Color(255, 0, 0, 80));
		g.fillRect(0, height/2, width, height);
		g.setColor(new Color(0, 255, 0, 80));
		g.fillRect(0, 0, width, height/2);

		super.paintGraph(g, width, height);
	}
	
	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		g.setColor(new Color(255, 0, 0, 80));
		g.fillRect(0, height/2, width, height/2);
		g.setColor(new Color(0, 255, 0, 80));
		g.fillRect(0, 0, width, height/2);

		super.paintButton(g, width, height);
	}
	
	@Override
	public Object getAnimationValue(float time) {
		return ((Float) super.getAnimationValue(time)) > .5f;
	}
	
	@Override
	public Object getStaticValue() {
		return manualBox.isSelected();
	}
	
	@Override
	public String getInstructions() {
		return "This variable is a boolean value. Any time the line is below 0.5 (the red area) it will be false, and otherwise it will be true.";
	}
	
	@Override
	public JComponent getManualController() {
		return manualBox;
	}
	
}