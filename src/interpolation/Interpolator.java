package interpolation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class Interpolator extends JFrame {

	private static final long serialVersionUID = -453395025925148885L;
	
	protected JTextArea instructionArea;
	
	public interface GetSet {
		public boolean isAnimationMode();
		public float getTime();
		public void set(Object o);
	}

	protected GetSet gs;
	protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	protected boolean animated;

	protected GridBagConstraints gbc;
	protected JButton animationButton;

	public Interpolator(GetSet gs, ChangeListener... listener) {
		this.gs = gs;
		for(ChangeListener l : listener)
			listeners.add(l);

		initializeComponents();
		addActionListeners();
	}

	private void initializeComponents() {

		instructionArea = new JTextArea(getInstructions());
		instructionArea.setLineWrap(true);
		instructionArea.setWrapStyleWord(true);
		instructionArea.setEditable(false);
		instructionArea.setOpaque(false);
		
		animationButton = new JButton() {
			private static final long serialVersionUID = 225462629234945413L;
			@Override 
			public void paint(Graphics ga) {
				Graphics2D g = (Graphics2D) ga;

				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				super.paint(ga);


				double xs = .9, ys = .75;

				g.translate(animationButton.getWidth()*((1-xs)/2), animationButton.getHeight()*((1-ys)/2));
				g.scale(xs, ys);
				paintButton(g, animationButton.getWidth(), animationButton.getHeight());

			}		
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 50);
			}
		};
	}

	private void addActionListeners() {
		animationButton.addActionListener(ae -> {setVisible(true);});
	}

	public JButton getAnimationButton() {
		return animationButton;
	}

	public void refreshValue() {
		if(gs != null)
			gs.set(getValue(gs.isAnimationMode(), gs.getTime()));
	}


	protected Object getValue(boolean animated, float time) {
		if(isKeyframable() && animated)
			return getAnimationValue(time);
		else return getStaticValue();
	}

	protected void fireChangeEvent() {

		refreshValue();

		for(ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

	public abstract void paintButton(Graphics2D g, int width, int height);
	protected abstract Object getAnimationValue(float time);
	protected abstract Object getStaticValue();
	public abstract String getInstructions();
	public abstract JComponent getManualController();
	//only use for non-standard keyframable interpolators like FilterInterpolator
	public void enterAnimationMode() {}
	public void exitAnimationMode() {}

	public abstract boolean isKeyframable();
}
