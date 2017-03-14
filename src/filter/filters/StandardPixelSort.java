package filter.filters;

import java.awt.image.BufferedImage;

import filter.base.PixelSortFilter;

public class StandardPixelSort extends PixelSortFilter {

	@Override
	protected BufferedImage apply(BufferedImage img) {
		
		int thresholdInt = (int) (threshold * 3 * 256);
		
		for (int y = 0; y < img.getHeight(); y++) {

			int nextStart = 0;

			for (int x = 0, c = img.getRGB(0, y), comp = getComparator(c), startComp = comp; x < img.getWidth()-1; x++, c = img.getRGB(x, y), comp = getComparator(c)) {
				if(((c >> 24) & 255) == 0) {

					line(img, nextStart, x, y);

					startComp = getComparator(img.getRGB(Math.min(x+1, img.getWidth()-1), y));
					nextStart = x+1;
				}
				else if (Math.abs(startComp - comp) >= thresholdInt) {
					line(img, nextStart, x - 1, y);

					startComp = comp;
					nextStart = x;
				}
			}
			line(img, nextStart+1, img.getWidth()-1, y);
		}
		return img;
	}
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
}
