package image;

import java.awt.image.BufferedImage;

import image.at.dhyan.open_imaging.GifDecoder;
import image.at.dhyan.open_imaging.GifDecoder.GifImage;

public class GifImageSequence extends ImageSequence {

	private GifDecoder.GifImage gif;
	
	public GifImageSequence(String name, GifImage gif) {
		super(name);
		this.gif	= gif;
		this.frames = gif.getFrameCount();
		this.width	= gif.getWidth();
		this.height	= gif.getHeight();
	}

	@Override
	public BufferedImage getFrame(int gifFrame) {
		return gif.getFrame(gifFrame);
	}
}
