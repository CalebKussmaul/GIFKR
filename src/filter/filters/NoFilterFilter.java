package filter.filters;

import java.awt.image.BufferedImage;

import filter.base.ImageFilter;

public class NoFilterFilter extends ImageFilter {
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	protected boolean angleControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		return img;
	}

}
