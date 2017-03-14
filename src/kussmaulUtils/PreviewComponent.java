package kussmaulUtils;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class PreviewComponent extends Component {

	private static final long serialVersionUID = -5318410876577677115L;
	private Refreshable r;

	private BufferedImage lastImg;
	private volatile RenderRequest waitingRequest;

	private JLabel imageLabel;
	private JLabel debugLabel = new JLabel();

	public PreviewComponent(Refreshable r, BufferedImage init) {
		this.r = r;

		initializeComponents();
		addActionListeners();
		setImage(init);
	}

	public boolean isWidthLimited() {
		return (getWidth() - lastImg.getWidth() < getHeight() - lastImg.getHeight());
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		double scale = isWidthLimited() ? getWidth()/(double) lastImg.getWidth() : getHeight()/(double) lastImg.getHeight();
		//		if(scale*lastImg.getWidth() == getWidth() || scale * lastImg.getHeight() == getHeight())
		//			scale = 1;

		g.drawImage(lastImg, (int) (getWidth() - lastImg.getWidth()*scale)/2, (int) (getHeight() - lastImg.getHeight()*scale)/2, (int) (lastImg.getWidth()*scale), (int) (lastImg.getHeight()*scale), Color.black, null);
	}

	private void initializeComponents() {

		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setVerticalAlignment(SwingConstants.TOP);

		imageLabel.setLayout(new GridLayout(1, 1));
		debugLabel.setHorizontalAlignment(SwingConstants.CENTER);
		debugLabel.setVerticalAlignment(SwingConstants.CENTER);
		imageLabel.add(debugLabel);
	}


	private void addActionListeners() {
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				r.refresh();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}

	public void setImage(BufferedImage img) {
		lastImg = img;
		imageLabel.setIcon(new ImageIcon(img));
	}

	public BufferedImage getLastImage() {
		return lastImg;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(1, 1);
	}

	public void addToRenderQueue(RenderRequest request) {
		if(waitingRequest == null && request != null) {
			new Thread(() -> {
				try {
					waitingRequest = request;

					lastImg = request.render();
					imageLabel.setIcon(new ImageIcon(lastImg));
					repaint();
				} catch (Exception e) {
					e.printStackTrace();
					waitingRequest = null;
				}
				if(request.equals(waitingRequest))
					waitingRequest = null;
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
}

interface RenderRequest {
	public BufferedImage render();
}
