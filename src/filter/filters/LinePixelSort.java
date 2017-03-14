package filter.filters;

import java.awt.image.BufferedImage;

import filter.base.PixelSortFilter;

public class LinePixelSort extends PixelSortFilter {

	public LinePixelSort() {
		hideRandom();
	}
	
	@Override
	protected BufferedImage apply(BufferedImage img) {

		float flength = 1 + (img.getWidth()-1) * threshold;
		int length = Math.round(flength);

		for (int y = 0, x = 0; y < img.getHeight(); y++) {
			for(x = 0; x < img.getWidth() && ((img.getRGB(x, y) & 0xFF000000) == 0); x++);

			int firstEnd = Math.round(flength*(x/length +1)) -1;

			line(img, x, Math.min(img.getWidth()-1, firstEnd), y);
			x = firstEnd+1;

			for(float fx = x; x < img.getWidth()-length && ((img.getRGB(x + length, y) & 0xFF000000) != 0); fx += flength, x = Math.round(fx))
				line(img, x, Math.round(fx + flength)-1, y);

			int end = x;
			for(; end < img.getWidth() - 1 && ((img.getRGB(end, y) & 0xFF000000) != 0); end++);
			line(img, x, end, y);
		}
		return img;
	}
}
