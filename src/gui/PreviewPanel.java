package gui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import utils.Refreshable;

public class PreviewPanel extends JPanel {

	private static final long serialVersionUID = -5318410876577677115L;
	private Refreshable r;

	private BufferedImage lastRenderedImg;
	private int lastW, lastH;
	private volatile RenderRequest waitingRequest;

	private PreviewStatusPanel statusPanel;

	public PreviewPanel(Refreshable r, int origW, int origH) {
		//super(new GridBagLayout());

		this.r = r;
		//lastRenderedImg = init;

		initializeComponents();
		addActionListeners();

		statusPanel.setOriginalRes(origW, origH, 1);
	}

	public boolean isWidthLimited() {
		return (lastW/(double)getWidth() > lastH/(double)getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		if(lastRenderedImg == null)
			return;

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		double scale = isWidthLimited() ? getWidth()/(double) lastW : getHeight()/(double) lastH;

		g.drawImage(lastRenderedImg, (int) (getWidth() - lastW*scale)/2, (int) (getHeight() - lastH*scale)/2, (int) (lastW*scale), (int) (lastH*scale), Color.black, null);
	}

	private void initializeComponents() {
		statusPanel = new PreviewStatusPanel(r);
	}


	private void addActionListeners() {
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				//r.refresh();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}

	public PreviewStatusPanel getStatusPanel() {
		return statusPanel;
	}

	public BufferedImage getLastImage() {
		return lastRenderedImg;
	}

	public void setNewOriginalDimensions(int width, int height, int maxFrame) {
		statusPanel.setOriginalRes(width, height, maxFrame);
	}

	//	public double getOrigWtoH() {
	//		Dimension d = statusPanel.getOrigResolution();
	//		
	//		return d.getWidth()/d.getHeight();
	//	}
	//	
	public int getRenderHeight() {
		return (int) (statusPanel.getOrigResolution().height * statusPanel.getResolutionScale());
	}
	public int getRenderWidth() {
		return (int) (statusPanel.getOrigResolution().width * statusPanel.getResolutionScale());
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(1, 1);
	}

	public void addToRenderQueue(RenderRequest request) {

		if(statusPanel.toSkipFrames()) {

			if(waitingRequest == null && request != null) {
				new Thread(() -> {
					try {
						waitingRequest = request;
						statusPanel.setRendering();
						lastRenderedImg = request.render();
						lastW = lastRenderedImg.getWidth();
						lastH = lastRenderedImg.getHeight();
						statusPanel.setFinishedRendering(lastW, lastH, request.getGifFrameNumber());
						SwingUtilities.invokeLater(() -> repaint());
					} catch (Exception e) {
						e.printStackTrace();
						waitingRequest = null;
						statusPanel.setErrorRendering();
					}
					if(request.equals(waitingRequest)) {
						waitingRequest = null;
					}
					else {
						RenderRequest next = waitingRequest;
						waitingRequest = null;
						addToRenderQueue(next);
					}
				}).start();
			}
			else {
				waitingRequest = request;
			}
		}

		else {
			if(request == null)
				return;
			SwingUtilities.invokeLater(() -> statusPanel.setRendering());
			try {
			lastRenderedImg = request.render();
			SwingUtilities.invokeLater(() -> {
				lastW = lastRenderedImg.getWidth();
				lastH = lastRenderedImg.getHeight();
				statusPanel.setFinishedRendering(lastW, lastH, request.getGifFrameNumber());
				repaint();
			});
			} catch (Exception e) {
				statusPanel.setErrorRendering();
			}
		}
	}
}

interface RenderRequest {
	public BufferedImage render();
	public int getGifFrameNumber();
}
