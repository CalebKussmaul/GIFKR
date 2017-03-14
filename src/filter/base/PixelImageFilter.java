package filter.base;

import java.awt.image.BufferedImage;

public abstract class PixelImageFilter extends ImageFilter {

	public abstract int apply(int color);
	
	@Override
	protected boolean angleControls() {
		return false;
	}
	
	@Override
	protected final BufferedImage apply(BufferedImage img) {
		int[] data = new int[img.getWidth()*img.getHeight()];
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());
		for(int i = 0; i < data.length; i++)
			data[i] = apply(data[i]);
		
		img.setRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());
		return img;
	}
	
	@Override
	public String getCategory() {
		return "Pixel";
	}
}
