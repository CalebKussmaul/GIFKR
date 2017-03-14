package filter.filters;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;
import utils.ImageTools;

public class LowResFilter extends ImageFilter {

	public float intensity;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		return ImageTools.fitBoth(ImageTools.scale(img, 1f-intensity, true), img.getWidth(), img.getHeight(), true);
	}
}
