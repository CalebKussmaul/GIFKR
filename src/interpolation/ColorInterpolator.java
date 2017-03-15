package interpolation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import gui.SingleColorChooser;
import gui.TransferableUtils;
import kussmaulUtils.ImageTools;

public class ColorInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -1873283247289277395L;

	private static int gradPadding = 15;
	
	private ArrayList<Keyframe<Color>> keyframes;
	private Keyframe<Color> selectedFrame;

	private JPanel colorKeyframesPanel;
	
	private JButton manualButton;
	private Color manualColor;

	public ColorInterpolator(Color c0, Color c1, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		keyframes = new ArrayList<Keyframe<Color>>();
		keyframes.add(new Keyframe<Color>(0f, c0));
		keyframes.add(new Keyframe<Color>(1f, c1));
		manualColor = c0;

		initializeComponents();
		//addActionListeners();

		refresh();
		
		gbc.insets = new Insets(5, 5, 5, 5);
		add(colorKeyframesPanel, gbc);

		pack();
	}

	private void initializeComponents() {
		colorKeyframesPanel = new JPanel(); 
		
		manualButton = new JButton("Select color");
		manualButton.addActionListener(ae -> {
			SingleColorChooser.showDialog(manualColor, ce -> {manualColor = SingleColorChooser.getColor(); fireChangeEvent();});
		});
		createMenuBar();
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
				if(data.get(0).getValue() instanceof Color)
					keyframes = (ArrayList<Keyframe<Color>>) (Object) data;
				refresh();
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
		selectedFrame.setTime(Math.min(1f, Math.max(0f, x)));
		refresh();
	}
	
	@Override
	public boolean isKeyframable() {
		return true;
	}

	@Override
	public Object getAnimationValue(float time) {
		if(time <= keyframes.get(0).getTime())
			return keyframes.get(0).getValue();
		if(time >= keyframes.get(keyframes.size()-1).getTime() || keyframes.size() == 1)
			return keyframes.get(keyframes.size()-1).getValue();

		for(int i = 0; i < keyframes.size()-1; i++) {
			if(keyframes.get(i).getTime() <= time && keyframes.get(i + 1).getTime() > time) {
				float distTo0 = time - keyframes.get(i).getTime();
				float distTo1 = keyframes.get(i + 1).getTime() - time;
				float sum = distTo0 + distTo1;
				return new Color(ImageTools.gradientRGB(keyframes.get(i+1).getValue().getRGB(), keyframes.get(i).getValue().getRGB(), distTo0 / sum)); 
			}
		}
		return Color.white;
	}
	
	@Override
	public Object getStaticValue() {
		return manualColor;
	}

	@Override
	public String getInstructions() {
		return "Create a gradient for the variable to traverse over time. Click and drag to move a keyframe, right click to create a new keyframe.";
	}

	@Override
	public JComponent getManualController() {
		return manualButton;
	}

	private void refresh() {
		Collections.sort(keyframes);
		refreshColorKeyframePanelPanel(false, false);
		animationButton.repaint();
		repaint();
		fireChangeEvent();
	}

	private void refreshColorKeyframePanelPanel(boolean add, boolean sub) {

		colorKeyframesPanel.removeAll();
		colorKeyframesPanel.setLayout(new GridLayout(keyframes.size(), 1));

		for(int i = 0; i < keyframes.size(); i++) {
			ColorKeyframePanel p = new ColorKeyframePanel(keyframes.get(i));
			p.setCloseable(keyframes.size() != 1);
			colorKeyframesPanel.add(p);
		}
		if(!(add||sub))
			colorKeyframesPanel.revalidate();
		else
			SwingUtilities.invokeLater(() -> {
				setSize(getWidth(), getHeight() + (add ? 1 : -1) * colorKeyframesPanel.getComponent(0).getPreferredSize().height);
			});
	}

	private void addKeyframe(Keyframe<Color> key) {
		keyframes.add(key);
		refreshColorKeyframePanelPanel(true, false);
		refresh();
	}

	private void removeKeyframe(Keyframe<Color> key) {
		keyframes.remove(key);
		refreshColorKeyframePanelPanel(false, true);
		refresh();
	}

	private Keyframe<Color> getNearestKeyframe(float time) {

		if (time <= keyframes.get(0).getTime())
			return keyframes.get(0);
		if (time >= keyframes.get(keyframes.size()-1).getTime())
			return keyframes.get(keyframes.size()-1);

		int bestIdx = 0;

		for(int i = 1; i < keyframes.size(); i++) {

			if(Math.abs(keyframes.get(i).getTime() - time) > Math.abs(keyframes.get(bestIdx).getTime() - time))
				break;
			else bestIdx = i;
		}
		return keyframes.get(bestIdx);
	}

	class ColorKeyframePanel extends JPanel {

		private static final long serialVersionUID = -329577250085593950L;
		private JButton changeButton;
		private JButton deleteButton;
		private Keyframe<Color> key;

		public ColorKeyframePanel(Keyframe<Color> keyframe) {
			super(new BorderLayout());
			this.key = keyframe;
			
			changeButton = new JButton("change color");
			deleteButton = new JButton("×");
			add(changeButton, BorderLayout.WEST);
			add(deleteButton, BorderLayout.EAST);

			deleteButton.addActionListener(ae -> {
				if(keyframes.size() > 1) {
					removeKeyframe(keyframe);
				}
			});

			changeButton.addActionListener(ae -> {
				SingleColorChooser.showDialog(key.getValue(), ce -> {keyframe.setValue(SingleColorChooser.getColor()); refresh();});
//				Color c = JColorChooser.showDialog(null, "Change keyframe color", keyframe.getValue());
//				if(c != null) {
//					keyframe.setValue(c);
//					refresh();
//				}
			});
		}
		
		@Override
		public void paintComponent(Graphics g2) {
			Graphics2D g = (Graphics2D) g2;
			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int padding = 0;
			int osxBottomFix = System.getProperty("os.name").toLowerCase().contains("mac") ? 3 : 0;
			
			g.setColor(key.getValue());
			g.fillRoundRect(padding, padding, getWidth()-2*padding, getHeight()-2*padding-osxBottomFix, 10, 10);
			g.setColor(key.getValue().darker());
			g.drawRoundRect(padding, padding, getWidth()-2*padding, getHeight()-2*padding-osxBottomFix, 10, 10);
		}

		public void setCloseable(boolean closeable) {
			deleteButton.setEnabled(closeable);
		}
	}

	@Override
	public Dimension getGraphSize() {
		return new Dimension(500, 100);
	}

	@Override
	public void paintGraph(Graphics2D g, int width, int height) {
		g.setColor(keyframes.get(0).getValue());
		g.fillRect(0, gradPadding, (int) (keyframes.get(0).getTime()*width), height-2*gradPadding);
		g.setColor(keyframes.get(keyframes.size()-1).getValue());
		g.fillRect((int) ((keyframes.get(keyframes.size()-1).getTime())*width), gradPadding, width - (int) ((keyframes.get(keyframes.size()-1).getTime())*width), height-2*gradPadding);

		Paint origP = g.getPaint();

		for(int i = 0; i < keyframes.size()-1; i++) {

			Keyframe<Color> k0 = keyframes.get(i), k1 = keyframes.get(i+1);

			g.setPaint(new GradientPaint(k0.getTime()*width, 0, k0.getValue(), k1.getTime()*width, 0, k1.getValue()));
			g.fillRect((int) (k0.getTime()*width), gradPadding-3, (int) (k1.getTime()*width-k0.getTime()*width), height-2*(gradPadding-3));
		}
		g.setPaint(origP);

		g.setStroke(new BasicStroke(3)); //Draw red bar at each keyframe
		g.setColor(Color.BLACK);//new Color(255, 0, 0, 32));
		for(int i = 0; i < keyframes.size(); i++)
			g.drawLine((int) (keyframes.get(i).getTime() * width), 0, (int) (keyframes.get(i).getTime() * width), height);
		g.setStroke(new BasicStroke(1)); //Draw red bar at each keyframe
		g.setColor(Color.WHITE);//new Color(255, 0, 0, 32));
		for(int i = 0; i < keyframes.size(); i++)
			g.drawLine((int) (keyframes.get(i).getTime() * width), 0, (int) (keyframes.get(i).getTime() * width), height);
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		g.setColor(keyframes.get(0).getValue());
		g.fillRect(0, gradPadding, (int) (keyframes.get(0).getTime()*width), height-2*gradPadding);
		g.setColor(keyframes.get(keyframes.size()-1).getValue());
		g.fillRect((int) ((keyframes.get(keyframes.size()-1).getTime())*width), gradPadding, width - (int) ((keyframes.get(keyframes.size()-1).getTime())*width), height-2*gradPadding);

		
		for(int i = 0; i < keyframes.size()-1; i++) {

			Keyframe<Color> k0 = keyframes.get(i), k1 = keyframes.get(i+1);

			g.setPaint(new GradientPaint(k0.getTime()*width, 0, k0.getValue(), k1.getTime()*width, 0, k1.getValue()));
			g.fillRect((int) (k0.getTime()*width), gradPadding-3, (int) (k1.getTime()*width-k0.getTime()*width), height-2*(gradPadding-3));
		}
		
	}
	
	@Override
	protected void graphPressed(float x, float y, boolean rightClick) {

		if(rightClick) {
			selectedFrame = new Keyframe<Color>(x, (Color) getAnimationValue(x));
			addKeyframe(selectedFrame);
		}
		else 
			selectedFrame = getNearestKeyframe(x);
		selectedFrame.setTime(x);

		refresh();
		fireChangeEvent();
	}
}
