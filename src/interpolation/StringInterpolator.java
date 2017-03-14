package interpolation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.TransferableUtils;
import utils.StringUtil;

public class StringInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -1873283247289277395L;

	private JScrollPane sp;
	private JTextField tField;
	private JCheckBox interpolateBox;

	private ArrayList<Keyframe<String>> keyframes;
	private Keyframe<String> selectedFrame;

	private JPanel colorKeyframesPanel;
	private JLabel infoLabel;

	public StringInterpolator(String s0, String s1, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		keyframes = new ArrayList<Keyframe<String>>();
		keyframes.add(new Keyframe<String>(0f, s0));
		keyframes.add(new Keyframe<String>(1f, s1));

		initializeComponents();

		refresh();
		gbc.insets = new Insets(5, 5, 5, 5);

		add(colorKeyframesPanel, gbc);

		gbc.gridy++;
		add(infoLabel, gbc);
		gbc.gridy++;
		add(interpolateBox, gbc);
		
		pack();
	}

	private void initializeComponents() {

		tField = new JTextField(keyframes.get(0).getValue()) {
			private static final long serialVersionUID = -4597572910181970566L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(50, super.getPreferredSize().height);
			}
		};
		tField.setBorder(BorderFactory.createEmptyBorder());
		
		tField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				refresh();
			}
			public void insertUpdate(DocumentEvent e) {
				refresh();
			}
			public void changedUpdate(DocumentEvent e) {
				refresh();
			}
		});
		
		sp = new JScrollPane(tField);
		sp.setPreferredSize(new Dimension(50, sp.getPreferredSize().height));
		interpolateBox = new JCheckBox("Interpolate between keyframes");
		
		colorKeyframesPanel = new JPanel();
		infoLabel = new JLabel("Time: -- Value: --");
		
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
				if(data.get(0).getValue() instanceof String)
					keyframes = (ArrayList<Keyframe<String>>) (Object) data;

				//keyframes = (ArrayList<Keyframe<String>>) t.getTransferData(TransferableUtils.objectDataFlavor);
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
	protected void graphMoved(float x, float y, boolean rightClick) {
		refreshInfo(x, y);
	}
	
	@Override
	protected void graphDragged(float x, float y, boolean rightClick) {
		selectedFrame.setTime(x);

		refresh();
		refreshInfo(x, y);
	}
	
	@Override
	protected void graphPressed(float x, float y, boolean rightClick) {
		if(rightClick) {
			selectedFrame = new Keyframe<String>(x, (String) getAnimationValue(x));
			addKeyframe(selectedFrame);
		}
		else 
			selectedFrame = getNearestKeyframe(x);
		selectedFrame.setTime(x);

		refresh();
	}

	public void refreshInfo(float x, float y) {
		infoLabel.setText(String.format(java.util.Locale.US, "Time: %.2f Value: %1s", Math.min(1, Math.max(0,x)), (String) getAnimationValue(x)));
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
				String s0 = keyframes.get(i).getValue(), s1 = keyframes.get(i+1).getValue();
				
				if(interpolateBox.isSelected())
					return StringUtil.merge(s0, s1, distTo0/sum);
				else return s0;
			}
		}
		return "";
	}
	
	@Override
	public Object getStaticValue() {
		return tField.getText();
	}

	@Override
	public String getInstructions() {
		return "Click and drag to move a keyframe, right click to create a new keyframe.";
	}

	@Override
	public JComponent getManualController() {
		return sp;
	}

	private void refresh() {
		Collections.sort(keyframes);
		repaint();
		refreshStringKeyframePanelPanel(false, false);
		fireChangeEvent();
	}
	private void softRefresh() {
		repaint();
   		fireChangeEvent();
	}

	private void refreshStringKeyframePanelPanel(boolean add, boolean sub) {

		colorKeyframesPanel.removeAll();
		colorKeyframesPanel.setLayout(new GridLayout(keyframes.size(), 1));

		for(int i = 0; i < keyframes.size(); i++) {
			StringKeyframePanel p = new StringKeyframePanel(keyframes.get(i));
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

	private void addKeyframe(Keyframe<String> key) {
		keyframes.add(key);
		refreshStringKeyframePanelPanel(true, false);
		refresh();
	}

	private void removeKeyframe(Keyframe<String> key) {
		keyframes.remove(key);
		refreshStringKeyframePanelPanel(false, true);
		refresh();
	}

	private Keyframe<String> getNearestKeyframe(float time) {

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

	class StringKeyframePanel extends JPanel {

		private static final long serialVersionUID = -329577250085593950L;
		private JTextField changeField;
		private JButton deleteButton;

		public StringKeyframePanel(Keyframe<String> keyframe) {
			super(new BorderLayout());

			changeField = new JTextField(keyframe.getValue());
			deleteButton = new JButton("×");
			add(changeField, BorderLayout.CENTER);
			add(deleteButton, BorderLayout.EAST);

			deleteButton.addActionListener(ae -> {
				if(keyframes.size() > 1) {
					removeKeyframe(keyframe);
				}
			});
			
			changeField.getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {
					keyframe.setValue(changeField.getText());
					softRefresh();
				}
				public void insertUpdate(DocumentEvent e) {
					keyframe.setValue(changeField.getText());
					softRefresh();
				}
				public void changedUpdate(DocumentEvent e) {
					keyframe.setValue(changeField.getText());
					softRefresh();
				}
			});
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
		g.setStroke(new BasicStroke(3)); //Draw red bar at each keyframe
		g.setColor(Color.BLACK);//new String(255, 0, 0, 32));
		for(int i = 0; i < keyframes.size(); i++)
			g.drawLine((int) (keyframes.get(i).getTime() * width), 0, (int) (keyframes.get(i).getTime() * width), height);
		g.setStroke(new BasicStroke(1)); //Draw red bar at each keyframe
		g.setColor(Color.WHITE);//new String(255, 0, 0, 32));
		for(int i = 0; i < keyframes.size(); i++)
			g.drawLine((int) (keyframes.get(i).getTime() * width), 0, (int) (keyframes.get(i).getTime() * width), height);
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		// TODO Auto-generated method stub
		
	}
}
