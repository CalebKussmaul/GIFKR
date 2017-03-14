package image;

import java.awt.image.BufferedImage;

public class BufferedImageSequence extends ImageSequence {

	private BufferedImage[] img;

	public BufferedImageSequence(String name, BufferedImage[] img) {
		super(name);
		this.img	= img;
		this.frames	= img.length;
		this.width 	= img[0].getWidth();
		this.height	= img[0].getHeight();
	}

	@Override
	public BufferedImage getFrame(int gifFrame) {
		return img[gifFrame];
	}

}
