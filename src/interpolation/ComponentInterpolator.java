package interpolation;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

public class ComponentInterpolator extends Interpolator {
	
	private static final long serialVersionUID = 6116763543834085563L;
	private JComponent component;
	
	public ComponentInterpolator(JComponent component, GetSet gs, ChangeListener... listener) {
		super(gs, listener);
		this.component = component;
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		
	}

	@Override
	protected Object getAnimationValue(float time) {
		return null;
	}

	@Override
	protected Object getStaticValue() {
		return null;
	}

	@Override
	public String getInstructions() {
		return null;
	}

	@Override
	public JComponent getManualController() {
		return component;
	}

	@Override
	public boolean isKeyframable() {
		return false;
	}

}
