package gui;

import java.awt.image.BufferedImage;

public interface RenderRequest {
	public BufferedImage render();
	public int getGifFrameNumber();
}
