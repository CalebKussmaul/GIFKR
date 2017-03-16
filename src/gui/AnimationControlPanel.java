package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

public class AnimationControlPanel extends JPanel {

	private static final long serialVersionUID = -7010566457660237376L;

	
	private Timer t;
	private boolean wasAnimating;
	private int frame;
	private MainPanel r;
	private ActionListener refreshListener;
	
	private JLabel frameCountLabel;
	private JSpinner frameCountSpinner;
	
	JPanel buttonP = new JPanel(new GridLayout(1, 3));
	private JButton rewindButton;
	private JButton playPauseButton;
	private JButton ffButton;
	
	public AnimationControlPanel(MainPanel r) {
		super(new GridBagLayout());
		this.r			= r;
		
		initializeComponents();
		addActionListeners();
		
		GridBagConstraints gbc	= new GridBagConstraints();
		gbc.weightx				= 0;
		gbc.weighty				= 1;
		gbc.gridx				= 0;
		gbc.gridy				= 0;
		gbc.gridwidth			= 1;
		gbc.anchor 				= GridBagConstraints.CENTER;
		gbc.fill 				= GridBagConstraints.BOTH;
		
		add(frameCountLabel, gbc);
		gbc.gridx++;
		gbc.weightx =1;
		
		add(frameCountSpinner, gbc);
		gbc.gridx=0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		
		buttonP.add(rewindButton);
		buttonP.add(playPauseButton);
		buttonP.add(ffButton);
		
		add(buttonP, gbc);
	}

	private void initializeComponents() {
		
		refreshListener = ae -> {
			frame = Math.floorMod(frame, (Integer) frameCountSpinner.getValue());
			
			frameCountLabel.setText("Frame "+frame+" of: ");
			float x = frame/(float) ((Integer) frameCountSpinner.getValue()-1);
			r.getAnimation().setX(x);
		};
		
		
		t = new Timer(100, ae -> {
			frame++;
			refreshListener.actionPerformed(ae);
			r.refresh();
		});
		
//		t = new Timer();
//		t.scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				frame++;
//				refreshListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
//				r.refresh();
//			}
//		}, 0, 100);
		
		frameCountLabel = new JLabel("Frame "+frame+" of: ");
		frameCountSpinner = new JSpinner(new SpinnerNumberModel(50, 2, Integer.MAX_VALUE, 1));
		setFrameCount(1);
		
		rewindButton = new JButton() {
			private static final long serialVersionUID = 2094701990574049393L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(new Color(80, 80, 80));
				
				int w = rewindButton.getWidth(), h = rewindButton.getHeight();
				g.fillPolygon(new int[] {w/2-15, w/2, w/2}, new int[]{h/2, 8, h-8}, 3);
				if(t.isRunning())
					g.fillPolygon(new int[] {w/2, w/2+15, w/2+15}, new int[]{h/2, 8, h-8}, 3);
			}
			
			@Override
			public Dimension getMinimumSize() {
				return getPreferredSize();
			}
		};
		playPauseButton = new JButton() {
			private static final long serialVersionUID = -541416802034336897L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				int w = rewindButton.getWidth(), h = rewindButton.getHeight();
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(new Color(80, 80, 80));
				
				if(t.isRunning())
					g.fillPolygon(new int[] {w/2+9, w/2+9, w/2-9, w/2-9}, new int[]{8, h-8, h-8, 8}, 4);
				else
					g.fillPolygon(new int[] {w/2+9, w/2-9, w/2-9}, new int[]{h/2, 8, h-8}, 3);
			}
			
			@Override
			public Dimension getMinimumSize() {
				return getPreferredSize();
			}
		};
		ffButton = new JButton() {
			private static final long serialVersionUID = 2329925086131708015L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				int w = rewindButton.getWidth(), h = rewindButton.getHeight();
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(new Color(80, 80, 80));
				
				g.fillPolygon(new int[] {w/2+15, w/2, w/2}, new int[]{h/2, 8, h-8}, 3);
				if(t.isRunning())
					g.fillPolygon(new int[] {w/2, w/2-15, w/2-15}, new int[]{h/2, 8, h-8}, 3);
			}
			
			@Override
			public Dimension getMinimumSize() {
				return getPreferredSize();
			}
		};
		
		rewindButton.setBorderPainted(false);
		playPauseButton.setBorderPainted(false);
		ffButton.setBorderPainted(false);
	}
	
	private void addActionListeners() {
		playPauseButton.addActionListener(ae -> {
			if(t.isRunning())
				t.stop();
			else
				t.start();
			buttonP.repaint();
		});
		
		ffButton.addActionListener(ae -> {
			if(t.isRunning())
				t.setDelay(Math.max(10, t.getDelay()-10));
			else {
				frame++;
				refreshListener.actionPerformed(ae);
				r.refresh();
			}
			buttonP.repaint();
		});
		rewindButton.addActionListener(ae -> {
			if(t.isRunning())
				t.setDelay(t.getDelay()+10);
			else {
				frame--;
				refreshListener.actionPerformed(ae);
				r.refresh();
			}
			buttonP.repaint();
		});
		
		frameCountSpinner.addChangeListener(ce -> r.getAnimation().setFrameCount((Integer) frameCountSpinner.getValue()));
	}
	
	public void setAnimating(boolean animating) {
		if(animating)
			t.start();
		else
			t.stop();
	}
	
	public void pause() {
		wasAnimating = t.isRunning();
		setAnimating(false);
	}
	
	public void resume() {
		if(wasAnimating)
			setAnimating(true);
	}
	
	public int setFrameCount(int frames) {
		
		frameCountSpinner.setValue(frames > 1 ? frames : 50);
		r.getAnimation().setFrameCount(frames);
		setAnimating(frames > 1);
		
		return (Integer) frameCountSpinner.getValue();
	}
}
