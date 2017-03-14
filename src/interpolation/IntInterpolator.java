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
import utils.ViewUtils;

public class IntInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -6215153672610067743L;

	private JSpinner manualSpinner;
	
	private static boolean alwaysAutoResize = false;
	private static int scaleTextInset = 2; 
	private static Color scaleTextColor = new Color(0, 0, 0, 127);
	private static Color lineColor = new Color(0, 0, 0, 200);
	private static Color keyframeIndicatorColor = new Color(255, 0, 0, 32);
	private static Color zeroLineColor = new Color(0, 0, 0, 32);
	
	private JSpinner perciseTime;
	private JSpinner perciseValue;
	private JButton perciseButton;
	
	private int minValue;
	private int maxValue;
	private int startingMin;
	private int startingMax;
	private int startingVal;

	private ArrayList<Keyframe<Integer>> keyframes;
	private Keyframe<Integer> dragFrame;

	private JSpinner maxSpinner;
	private JSpinner minSpinner;
	private Algebra function;
	private JTextField equationField;
	private JLabel infoLabel;

	//double startingVal, double minValue, double maxValue, double visMin, double visMax, GetSet gs, ChangeListener... listeners)
	public IntInterpolator(int startingVal, int minValue, int maxValue, int visMin, int visMax, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		this.startingVal = startingVal;
		this.startingMin = Math.max(visMin, minValue);
		this.startingMax = Math.min(visMax, maxValue);
		this.minValue = minValue;
		this.maxValue = maxValue;

		keyframes = new ArrayList<Keyframe<Integer>>();
		keyframes.add(new Keyframe<Integer>(0f, startingVal));
		keyframes.add(new Keyframe<Integer>(1f, startingVal));

		initializeComponents();
		addActionListeners();

		gbc.gridwidth=2;
		add(infoLabel, gbc);
		gbc.gridy++;
		
		gbc.gridwidth=1;
		gbc.weightx = 0;
		add(new JLabel("f(x) ="), gbc);
		gbc.weightx=1;
		gbc.gridx++;
		add(equationField, gbc);
		
		gbc.gridwidth = 2;
		gbc.gridy++;
		gbc.gridx=0;
		
		
		JPanel addPanel = new JPanel(new GridLayout(1, 4));
		addPanel.add(new JLabel("Add keyframe:"));
		addPanel.add(perciseTime);
		addPanel.add(perciseValue);
		addPanel.add(perciseButton);
		
		add(addPanel, gbc);
		
		pack();
	}

	private void initializeComponents() {

		manualSpinner = new JSpinner(new SpinnerNumberModel(startingVal, 0, maxValue, 1));

		perciseTime = new JSpinner(new SpinnerNumberModel(0d, 0d, 1d, .05d));
		perciseValue = new JSpinner(new SpinnerNumberModel(0, minValue, maxValue, 1));
		perciseButton = new JButton("Add");
		
		maxSpinner = new JSpinner(new SpinnerNumberModel(startingMax, minValue+1, maxValue, 1));
		((JSpinner.DefaultEditor) maxSpinner.getEditor()).getTextField().setColumns(3);
		((JSpinner.DefaultEditor) maxSpinner.getEditor()).getTextField().setBackground(new Color(255, 255, 255, 127));
		minSpinner = new JSpinner(new SpinnerNumberModel(startingMin, minValue, maxValue-1, 1));
		((JSpinner.DefaultEditor) minSpinner.getEditor()).getTextField().setColumns(3);
		((JSpinner.DefaultEditor) minSpinner.getEditor()).getTextField().setBackground(new Color(255, 255, 255, 127));

		GridBagConstraints gb2 = ViewUtils.createGBC();
		gb2.anchor = GridBagConstraints.NORTHWEST;
		gb2.weighty = 0;
		graphPanel.setLayout(new GridBagLayout());
		graphPanel.add(maxSpinner, gb2);
		gb2.weighty = 1;
		gb2.gridy++;
		graphPanel.add(ViewUtils.createDummyComponent(), gb2);
		gb2.anchor = GridBagConstraints.SOUTHWEST;
		gb2.gridy++;
		graphPanel.add(minSpinner, gb2);

		function = new Algebra(startingVal+"");
		equationField = new JTextField(function.getFunction());
		infoLabel = new JLabel("Time: -- Value: -- Min: -- Max: --");
		createMenuBar();
	}
	
	@SuppressWarnings("unchecked")
	private void createMenuBar() {
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(ae -> {
			TransferableUtils.copyObject(Keyframe.deepCopy(keyframes));
			System.out.println("copy");
		});
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.addActionListener(ae -> {
			Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
			try {
				ArrayList<Keyframe<Serializable>> data = (ArrayList<Keyframe<Serializable>>) t.getTransferData(TransferableUtils.objectDataFlavor);
				if(data.isEmpty())
					return;
				System.out.println("paste");
				//Yes this is ugly. There is really no better way of doing it though. Forgive me
				if(data.get(0).getValue() instanceof Integer)
					keyframes = (ArrayList<Keyframe<Integer>>) (Object) data;
				if(data.get(0).getValue() instanceof Double)
					keyframes = Keyframe.doubleToInt((ArrayList<Keyframe<Double>>) (Object) data);
				refresh(false);
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

	private void addActionListeners() {

		minSpinner.addChangeListener(ce -> { 
			repaint();
		});
		maxSpinner.addChangeListener(ce -> {
			repaint();
		});

		manualSpinner.addChangeListener(ce -> {
			fireChangeEvent();
		});
		
		perciseButton.addActionListener(ae -> {
			Keyframe<Integer> key = new Keyframe<Integer>(((Double) perciseTime.getValue()).floatValue(), ((Integer) perciseValue.getValue()));
			
			for(int i = 0; i < keyframes.size(); i++)
				if(Math.abs(keyframes.get(i).getTime()-key.getTime()) < .05f) {
					keyframes.remove(i);
					i--;
				}
			
			keyframes.add(key);
			Collections.sort(keyframes);
			refresh(false);
		});
		
		equationField.addActionListener(ae -> {
			if(function.setFunction(equationField.getText())) {
				clear(0, 1);

				equationField.setBackground(new Color(255, 255, 255, 255));
				for(float f = 0; f <= 1; f+=.02f)
					keyframes.add(new Keyframe<Integer>(f, Math.min(maxValue, Math.round(function.evalF(f)))));
				keyframes.add(new Keyframe<Integer>(1f, Math.min(maxValue, Math.round(function.evalF(1f)))));

				for (int i = 1; i < keyframes.size()-1; i++) {
					if(keyframes.get(i-1).getValue().equals(keyframes.get(i).getValue()) && keyframes.get(i).getValue().equals(keyframes.get(i+1).getValue())) {
						keyframes.remove(i);
						i--;
					}
				}
				refresh(true);
			}
			else {
				equationField.setBackground(new Color(255, 127, 127));
			}
		});
	}
	
	
	@Override 
	protected boolean restrictRange() {
		return false;
	}
	
	public void setMax(int max) {
		keyframes.removeIf(a -> true);
		keyframes.add(new Keyframe<Integer>(0, 0));
		keyframes.add(new Keyframe<Integer>(1, max));
		maxSpinner.setValue(max);
		maxValue = max;
		((SpinnerNumberModel) manualSpinner.getModel()).setMaximum(max);
		((SpinnerNumberModel) maxSpinner.getModel()).setMaximum(max);
		refresh(true);
	}
	
	@Override
	protected void graphMoved(float x, float y, boolean rightClick) {
		refreshInfo(x, y);
	}
	
	@Override
	protected void graphDragged(float x, float y, boolean rightClick) {
		if(dragFrame != null)
			clear(dragFrame.getTime(), x);
		dragFrame = new Keyframe<Integer>(x, getValFromPos(y));

		refresh(false);
		refreshInfo(x, y);
		fireChangeEvent();
	}
	
	@Override
	protected void graphReleased(float x, float y, boolean rightClick) {
		if(dragFrame == null)
			dragFrame = new Keyframe<Integer>(x, getValFromPos(y));

		keyframes.add(dragFrame);

		Collections.sort(keyframes);

		if(keyframes.get(keyframes.size()-1).getTime() != 1f)
			keyframes.add(new Keyframe<Integer>(1f, dragFrame.getValue()));
		if(keyframes.get(0).getTime() != 0f)
			keyframes.add(new Keyframe<Integer>(0f, dragFrame.getValue()));

		animationButton.repaint();
		dragFrame = null;
		refresh(true);
		refreshInfo(x, y);
		fireChangeEvent();
	}
	
	@Override
	protected void graphPressed(float x, float y, boolean rightClick) {
		if(dragFrame != null)
			clear(dragFrame.getTime(), x);
		dragFrame = new Keyframe<Integer>(x, getValFromPos(y));

		refresh(true);
		refreshInfo(x, y);
		fireChangeEvent();
	}

	public void refreshInfo(float x, float y) {
		
		int value = dragFrame == null ? (int) getAnimationValue(x) : getValFromPos(y);
		infoLabel.setText(String.format(java.util.Locale.US, "Time: %.2f Value: %1d Min: %1d Max: %1d", x, value, getMin(), getMax()));
	}
	
	public void refresh(boolean resize) {
		
		int max = getMax();
		if(resize && (alwaysAutoResize || max >= (Integer) maxSpinner.getValue()))
			maxSpinner.setValue((int)Math.min(maxValue, max > 0 ? max * 1.25: max));
		((SpinnerNumberModel) maxSpinner.getModel()).setMinimum(max);
		
		int min = getMin();
		if(resize && (alwaysAutoResize || min <= (Integer) minSpinner.getValue()))
			minSpinner.setValue((int)Math.max(minValue, min));
		((SpinnerNumberModel) minSpinner.getModel()).setMaximum(min);
		
		repaint();
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
				return Math.round((distTo0/sum) * keyframes.get(i+1).getValue() + (distTo1/sum) * keyframes.get(i).getValue()); 
			}
		}
		return 0;
	}
	
	@Override
	public Object getStaticValue() {
		return ((Integer) manualSpinner.getValue());
	}

	@Override
	public String getInstructions() {
		return "This variable is an integer. Simply draw a graph of how it should change through the animation, or enter a function to start. You can adust the display range using the numerical input in the top right corner of the graph.";
	}
	
	@Override
	public JComponent getManualController() {
		return manualSpinner;
	}
	
	private int getValFromPos(float y) {
		int max = (Integer) maxSpinner.getValue();
		int min = (Integer) minSpinner.getValue();
		int range = max - min;
		return Math.round(Math.min(maxValue, Math.max(minValue, (y * range)+min)));
	}
	
	private int getMax() {
		int max = dragFrame == null ? 0 : dragFrame.getValue();

		for(int i = 0; i < keyframes.size(); i++)
			if(keyframes.get(i).getValue() > max)
				max = keyframes.get(i).getValue();
		return max;
	}
	
	private int getMin() {
		int min = dragFrame == null ? Integer.MAX_VALUE : dragFrame.getValue();

		for(int i = 0; i < keyframes.size(); i++)
			if(keyframes.get(i).getValue() < min)
				min = keyframes.get(i).getValue();
		return min;
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
		FontMetrics fm = g.getFontMetrics();
		float min = (Integer) minSpinner.getValue();
		float max = (Integer) maxSpinner.getValue();
		float range = max - min;

		g.setColor(scaleTextColor);

		g.drawString(String.format("%.1f", range/2f+min), scaleTextInset, (height+fm.getHeight())/2 - fm.getDescent());
		g.setColor(lineColor);

		g.setStroke(new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		ArrayList<Keyframe<Integer>> copy = new ArrayList<>(keyframes);
		if(dragFrame != null)
			copy.add(dragFrame);
		Collections.sort(copy);

		if(copy.get(0).getTime() != 0f)
			copy.add(0, new Keyframe<Integer>(0f, copy.get(0).getValue()));
		if(copy.get(copy.size()-1).getTime() != 1f)
			copy.add(new Keyframe<Integer>(1f, copy.get(copy.size()-1).getValue()));

		GeneralPath path = new GeneralPath();
		path.moveTo(copy.get(0).getTime() * width, Math.round(((1f - (copy.get(0).getValue()-min)/range) * height)));
		for(int i = 1; i < copy.size(); i++)
			path.lineTo(copy.get(i).getTime() * width, Math.round((1f - (copy.get(i).getValue()-min)/range) * height)-.5f);
		g.draw(path);

		g.setStroke(new BasicStroke(3)); //Draw red bar at each keyframe
		g.setColor(keyframeIndicatorColor);
		for(int i = 1; i < copy.size()-1; i++)
			g.drawLine((int) (copy.get(i).getTime() * width), 0, (int) (copy.get(i).getTime() * width), height);
		if(min < 0 && max > 0) {
			g.setColor(zeroLineColor);
			g.drawLine(0, (int) ((1-(-min/range))*height), width, (int) ((1-(-min/range))*height));
		}
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		float min = (Integer) minSpinner.getValue();
		float max = (Integer) maxSpinner.getValue();
		float range = max - min;
		
		GeneralPath path = new GeneralPath();
		path.moveTo(keyframes.get(0).getTime() * width, (1f - (keyframes.get(0).getValue()-min)/range) * height);
		for(int i = 1; i < keyframes.size(); i++)
			path.lineTo(keyframes.get(i).getTime() * width, ((1f - (keyframes.get(i).getValue()-min)/range) * height)-.5f);
		g.draw(path);
	}
	public static void main(String[] args) {
		IntInterpolator di = new IntInterpolator(0, -20, 20, -25, 25, null);
		di.setVisible(true);
		di.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
