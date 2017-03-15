package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import kussmaulUtils.ImageTools;
import kussmaulUtils.ViewUtils;

public class ProgressFrame extends JFrame implements ProgressDisplay {

	private static final long serialVersionUID = -4503301545828109065L;
	
	private double progress;
	private double dispProgress;
	
	private Color bg = new Color(0, 0, 0, 127);
	private Color fg = new Color(0, 64, 175); //new Color(255, 69, 0);
	private Color accent = new Color(0, 44, 117); //new Color(255, 0, 0);
	
	private Timer smoothProgressTimer;
	private JLabel progressText;
	private JButton cancelButton;
	
	private ActionListener onCancel;
	
	public ProgressFrame() {
		
		initializeComponents();
		addActionListeners();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = ViewUtils.createGBC();
		gbc.fill = GridBagConstraints.NONE;
		
		add(progressText, gbc);
		gbc.gridy++;
		
		add(cancelButton, gbc);
		
		setSize(500, 200);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setBackground(bg);
	}
	
	private void initializeComponents() {
		progressText = new JLabel(new ImageIcon(ImageTools.scaleToHeight(ImageTools.getResourceImage("iconsmall.png"), 50, false)));
		progressText.setForeground(Color.white);
		progressText.setVerticalTextPosition(JLabel.BOTTOM);
		progressText.setHorizontalTextPosition(JLabel.CENTER);
		cancelButton = new JButton("Cancel");
		
		setContentPane(new JPanel() {
			private static final long serialVersionUID = -8098561857624764691L;
			@Override
			public void paintComponent(Graphics g) {
				setOpaque(false);
				super.paintComponent(g);
				paintBG((Graphics2D) g);
			}
		});
		cancelButton.addActionListener(ae -> {if(onCancel != null) onCancel.actionPerformed(ae);});
	}
	private void addActionListeners() {
		smoothProgressTimer = new Timer(1000/60, ae-> {
			
			dispProgress += (progress - dispProgress) *.05;
			repaint();
		});
	}
	
	private void paintBG(Graphics2D g) {
		
		g.setColor(fg);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int wp = (int) (getWidth()*dispProgress), h = getHeight();
		
		Polygon p = new Polygon(new int[] {0, 0, wp, wp+30}, new int[] {0, h, h, 0}, 4);
		g.fill(p);
		
		g.setColor(accent);
		p = new Polygon(new int[] {wp+20, wp-10, wp, wp+30}, new int[] {0, h, h, 0}, 4);
		g.fill(p);
		
		//g.fillRect(0, 0, (int) (getWidth()*progress), getHeight());
	}

	@Override
	public void setProgress(double d, String text) {
		progress = d;
		progressText.setText(((int)(d*100))+"% - "+text);
	}

	@Override
	public void showProgress() {
		repaint();
		cancelButton.setVisible(onCancel != null);
		setVisible(true);
		smoothProgressTimer.start();
	}
	
	@Override
	public void setCancel(ActionListener onCancel) {
		this.onCancel = onCancel;
		cancelButton.setVisible(true);
		repaint();
		setVisible(true);
		smoothProgressTimer.start();
	}

	@Override
	public void hideProgress() {
		setVisible(false);
		smoothProgressTimer.stop();
		dispProgress = 0;
		progress = 0;
		onCancel = null;
	}
	
	public static void main(String[] args) {
		ProgressFrame pf = new ProgressFrame();
		pf.showProgress();
		
		Scanner s = new Scanner(System.in);
		while (s.hasNextInt()) {
			pf.setProgress(s.nextInt()/100d, "test");
		}
		s.close();
		System.exit(0);
	}

}
