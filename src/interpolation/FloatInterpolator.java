package interpolation;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import gui.TransferableUtils;

public class FloatInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -6215153672610067743L;
	
	protected ArrayList<Keyframe<Float>> keyframes;
	protected Keyframe<Float> dragFrame;
	
	private Algebra function;
	private JTextField equationField;
	
	private JSpinner manualSpinner;
	
	private JSpinner perciseTime;
	private JSpinner perciseValue;
	private JButton perciseButton;

	public FloatInterpolator(float startingVal, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		
		keyframes = new ArrayList<Keyframe<Float>>();
		keyframes.add(new Keyframe<Float>(0f, startingVal));
		keyframes.add(new Keyframe<Float>(1f, startingVal));
		
		initializeComponents();
		addActionListeners();
		
		gbc.gridwidth=1;
		gbc.weightx = 0;
		add(new JLabel("f(x) ="), gbc);
		gbc.weightx=1;
		gbc.gridx++;
		add(equationField, gbc);
		gbc.gridy++;
		
		JPanel addPanel = new JPanel(new GridLayout(1, 4));
		addPanel.add(new JLabel("Add keyframe:"));
		addPanel.add(perciseTime);
		addPanel.add(perciseValue);
		addPanel.add(perciseButton);
		
		add(addPanel, gbc);
		
		pack();
	}

	private void initializeComponents() {
		
		perciseTime = new JSpinner(new SpinnerNumberModel(0d, 0d, 1d, .05d));
		perciseValue = new JSpinner(new SpinnerNumberModel(0d, 0d, 1d, .05d));
		perciseButton = new JButton("Add");
		
		function = new Algebra(keyframes.get(0).getValue().doubleValue()+"");
		equationField = new JTextField(function.getFunction());
		manualSpinner = new JSpinner(new SpinnerNumberModel(keyframes.get(0).getValue().doubleValue(), 0d, 1d, .1d));
		createMenuBar();
	}
	
	private void addActionListeners() {
		
		manualSpinner.addChangeListener(ce -> {
			fireChangeEvent();
		});
		
		perciseButton.addActionListener(ae -> {
			Keyframe<Float> key = new Keyframe<Float>(((Double) perciseTime.getValue()).floatValue(), ((Double) perciseValue.getValue()).floatValue());
			
			keyframes.add(key);
			
			for(int i = 0; i < keyframes.size(); i++)
				if(Math.abs(keyframes.get(i).getTime()-key.getTime()) < .05f) {
					keyframes.remove(i);
					i--;
				}
			keyframes.add(key);
			Collections.sort(keyframes);
			repaint();
			fireChangeEvent();
		});
		
		equationField.addActionListener(ae -> {
			if(function.setFunction(equationField.getText())) {
				clear(0, 1);
				equationField.setBackground(new Color(255, 255, 255, 255));
				for(float f = 0; f <= 1; f+=.02f)
					keyframes.add(new Keyframe<Float>(f, Math.min(1f, Math.max(0f, function.evalF(f)))));
				keyframes.add(new Keyframe<Float>(1f, Math.min(1f, Math.max(0f, function.evalF(1f)))));
				
				for (int i = 1; i < keyframes.size()-1; i++) {
					if(keyframes.get(i-1).getValue().equals(keyframes.get(i).getValue()) && keyframes.get(i).getValue().equals(keyframes.get(i+1).getValue())) {
						keyframes.remove(i);
						i--;
					}	
				}
				repaint();
				fireChangeEvent();
			}
			else {
				equationField.setBackground(new Color(255, 127, 127));
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void createMenuBar() {
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(ae -> TransferableUtils.copyObject(Keyframe.deepCopy(keyframes)));
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.addActionListener(ae -> {
			Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
			try {
				ArrayList<Keyframe<Serializable>> data = (ArrayList<Keyframe<Serializable>>) t.getTransferData(TransferableUtils.objectDataFlavor);
				if(data.isEmpty())
					return;
				//Yes this is ugly. There is really no better way of doing it though. Forgive me
				if(data.get(0).getValue() instanceof Float)
					keyframes = (ArrayList<Keyframe<Float>>) (Object) data;
				if(data.get(0).getValue() instanceof Double)
					keyframes = Keyframe.doubleToFloat((ArrayList<Keyframe<Double>>) (Object) data);
				if(data.get(0).getValue() instanceof Integer)
					keyframes = Keyframe.intToFloat((ArrayList<Keyframe<Integer>>) (Object) data);
				animationButton.repaint();
				repaint();
				fireChangeEvent();
			} catch (UnsupportedFlavorException | IOException e1) {
				System.err.println("Invalid data copied");
			}
		});
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.META_MASK));
		
		JMenuBar bar = new JMenuBar();
		JMenu edit = new JMenu("Edit");
		edit.add(copy);
		edit.add(paste);
		bar.add(edit);
		setJMenuBar(bar);
	}
	
	@Override
	protected void graphDragged(float x, float y, boolean rightClick) {
		if(dragFrame != null)
			clear(dragFrame.getTime(), x);
		dragFrame = new Keyframe<Float>(x, y);
		
		repaint();
		fireChangeEvent();
	}
	
	@Override
	protected void graphReleased(float x, float y, boolean rightClick) {
		if(dragFrame == null)
			dragFrame = new Keyframe<Float>(x, y);
		
		keyframes.add(dragFrame);
		
		Collections.sort(keyframes);
		
		if(keyframes.get(keyframes.size()-1).getTime() != 1f)
			keyframes.add(new Keyframe<Float>(1f, dragFrame.getValue()));
		if(keyframes.get(0).getTime() != 0f)
			keyframes.add(new Keyframe<Float>(0f, dragFrame.getValue()));
		
		dragFrame = null;
		animationButton.repaint();
		repaint();
		fireChangeEvent();
	}

	@Override
	public boolean isKeyframable() {
		return true;
	}


	@Override
	public Object getAnimationValue(float time) {
		if(keyframes.size() == 0)
			if(dragFrame != null)
				return dragFrame.getValue();
			else return 0;
		
		if(time <= keyframes.get(0).getTime())
			return keyframes.get(0).getValue();
		if(time >= keyframes.get(keyframes.size()-1).getTime() || keyframes.size() == 1)
			return keyframes.get(keyframes.size()-1).getValue();
		
		for(int i = 0; i < keyframes.size()-1; i++) {
			if(keyframes.get(i).getTime() <= time && keyframes.get(i + 1).getTime() > time) {
				float distTo0 = time - keyframes.get(i).getTime();
				float distTo1 = keyframes.get(i + 1).getTime() - time;
				float sum = distTo0 + distTo1;
				return (distTo0/sum) * keyframes.get(i+1).getValue() + (distTo1/sum) * keyframes.get(i).getValue(); 
			}
		}
		return 0f;
	}

	@Override
	public Object getStaticValue() {
		return ((Double) manualSpinner.getValue()).floatValue();
	}
	
	@Override
	public String getInstructions() {
		return "This variable is between 0 and 1. Simply draw a graph of how it should change through the animation, or enter a function to start.";
	}
	
	@Override
	public JComponent getManualController() {
		return manualSpinner;
	}
	
	private void clear(float t0, float t1) {
		Collections.sort(keyframes);
		
		for(int i = 0; i < keyframes.size(); i++) {
			float t = keyframes.get(i).getTime();
			if((t >= t0 && t <= t1) || (t >= t1 && t <= t0)) {
				keyframes.remove(i);
				i--;
			}
		}
	}

	@Override
	public Dimension getGraphSize() {
		return new Dimension(500, 300);
	}

	@Override
	public void paintGraph(Graphics2D g, int width, int height) {
		g.setColor(new Color(0, 0, 0, 127));
		
		g.drawString("1.0", 2, g.getFontMetrics().getHeight());
		g.drawString("0.0", 2, height-g.getFontMetrics().getDescent());
		g.setColor(new Color(0, 0, 0, 200));
		
		g.setStroke(new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		
		ArrayList<Keyframe<Float>> copy = new ArrayList<>(keyframes);
		if(dragFrame != null)
			copy.add(dragFrame);
		Collections.sort(copy);
		
		if(copy.get(0).getTime() != 0f)
			copy.add(0, new Keyframe<Float>(0f, copy.get(0).getValue()));
		if(copy.get(copy.size()-1).getTime() != 1f)
			copy.add(new Keyframe<Float>(1f, copy.get(copy.size()-1).getValue()));
		
		GeneralPath path = new GeneralPath();
		path.moveTo(copy.get(0).getTime() * width, (1f - copy.get(0).getValue()) * height);
		for(int i = 1; i < copy.size(); i++)
			path.lineTo(copy.get(i).getTime() * width, ((1f - copy.get(i).getValue()) * height-.5f));
		g.draw(path);

		g.setStroke(new BasicStroke(3)); //Draw red bar at each keyframe
		g.setColor(new Color(255, 0, 0, 32));
		for(int i = 1; i < copy.size()-1; i++)
			g.drawLine((int) (copy.get(i).getTime() * width), 0, (int) (copy.get(i).getTime() * width), height);
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		
		GeneralPath path = new GeneralPath();
		path.moveTo(keyframes.get(0).getTime() * width, (1f - keyframes.get(0).getValue()) * height);
		for(int i = 1; i < keyframes.size(); i++)
			path.lineTo(keyframes.get(i).getTime() * width, ((1f - keyframes.get(i).getValue()) * height-.5f));
		g.draw(path);
	}
}
