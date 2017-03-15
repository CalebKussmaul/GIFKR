package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import utils.Refreshable;

public class PreviewStatusPanel extends JPanel {

	private static final long serialVersionUID = -7198039609391537335L;
	
	private static String format = "Time: %1$9s | Resolution: %2$9s | Original: %3$9s";
	private static String formatGif = "Time: %1$9s | Resolution: %2$9s | Original: %3$9s | Frame: %4$9s";
	
	private Refreshable r;
	
	private long renderStart;

	private int lastRendTime;
	private int origW;
	private int origH;
	private int dispW;
	private int dispH;
	private int frame;
	private int frameCount;
	
	private JSlider qualitySlider;
	private JLabel statusLabel;
	private JCheckBox skipFrames;
	
	private boolean error = false;

	public PreviewStatusPanel(Refreshable r) {
//		((FlowLayout) getLayout()).setAlignment(FlowLayout.TRAILING);
		this.r = r;

		initializeComponents();

		setBackground(new Color(50, 50, 50));

		add(new JLabel("Quality:"));
		add(qualitySlider);
		add(statusLabel);
		add(skipFrames);
		
		for(Component c : getComponents()) {
			if(c instanceof JComponent) {
				((JComponent) c).setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
				((JComponent) c).setForeground(Color.white);
			}
		}
		refreshText();
	}

	private void initializeComponents() {
		qualitySlider = new JSlider(10, 100, 50) {
			private static final long serialVersionUID = -3633632766184431178L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width/2, new JLabel(" ").getPreferredSize().height);
			}
		};
		qualitySlider.addChangeListener(ce -> r.refresh());
		
		qualitySlider.setOpaque(false);
		statusLabel = new JLabel();
		skipFrames = new JCheckBox("Skip frames", false);
		skipFrames.setOpaque(false);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}
	
	public void setRendering() {
		renderStart = System.currentTimeMillis();
		lastRendTime =-1;
		refreshText();
	}

	public void setFinishedRendering(int w, int h, int gifFrame) {
		error = false;
		lastRendTime = (int) (System.currentTimeMillis()-renderStart);
		dispW = w;
		dispH = h;
		this.frame = gifFrame;
		refreshText();
	}
	
	public void setErrorRendering() {
		error = true;
		refreshText();
	}
	
	private void refreshText() {
		SwingUtilities.invokeLater(() -> {
			
			String renderText = error ? "error" : (lastRendTime == -1 ? "rendering" : lastRendTime+"ms");
			
			if(frameCount > 1)
				statusLabel.setText(String.format(formatGif, renderText, dispW+"x"+dispH, origW+"x"+origH, frame+1 +" of "+frameCount));
			else
				statusLabel.setText(String.format(format, renderText, dispW+"x"+dispH, origW+"x"+origH));
		});
	}

	public void setOriginalRes(int w, int h, int frameCount) {
		this.frameCount = frameCount;
		origW = w;
		origH = h;
	}
	
	public float getResolutionScale() {
		return qualitySlider.getValue()/100f;
	}
	
	public Dimension getOrigResolution() {
		return new Dimension(origW, origH);
	}
	
	public boolean toSkipFrames() {
		return skipFrames.isSelected();
	}
}
