package gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public abstract class Renderer {

	private boolean skipFrames;
	
	private Timer t;
	
	private RenderRequestGenerator gen;
	private RenderRequest waitingSkip;
	private List<RenderRequest> waiting;
	
	public Renderer() {
		
		waiting = new ArrayList<>();
		
		t = new Timer(100, ae -> {
			
			RenderRequest r = gen.createRequest();
			
			if(waiting.size() == 0)
				render(r);
			else {
				waiting.add(r);
			}
			
		});
	}
	
	public void render(RenderRequest r) {
		if(skipFrames || !t.isRunning()) {

			if(waitingSkip == null && r != null) {
				new Thread(() -> {
					try {
						waitingSkip = r;
						onRenderStart();
						refresh(r.render());
					} catch (Exception e) {
						e.printStackTrace();
						waitingSkip = null;
						displayError(e.getMessage());
					}
					if(r.equals(waitingSkip)) {
						waitingSkip = null;
					}
					else {
						RenderRequest next = waitingSkip;
						waitingSkip = null;
						render(next);
					}
				}).start();
			}
		}
		
		else {
			
		}
	}
	
	public void startAnimating(RenderRequestGenerator gen) {
		this.gen = gen;
		if(!t.isRunning())
			t.start();
	}
	
	public void setAnimationDelay(int delay) {
		if(t.isRunning())
			t.setDelay(delay);
	}
	
	public int getAnimationDelay() {
		return t.getDelay();
	}
	
	public abstract void onRenderStart();
	public abstract void displayError(String error);
	public abstract void refresh(BufferedImage renderedImage);

	
	

}

interface RenderRequestGenerator {
	public RenderRequest createRequest();
}
