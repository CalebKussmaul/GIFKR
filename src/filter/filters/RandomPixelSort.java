package filter.filters;

import java.awt.image.BufferedImage;

import filter.base.PixelSortFilter;

public class RandomPixelSort extends PixelSortFilter {

	@Override
	protected BufferedImage apply(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y++) {
			int sortStart = 0;
			int c0 = img.getRGB(0, y);
			int pwr = Math.max(1, img.getWidth()/40);
			for (int x = 1; x < img.getWidth(); x++) {

				int c1 = img.getRGB(x, y);
				if(Math.pow(rand.nextDouble(), pwr) > threshold || (c0 >>> 24) < 254 || (c1 >>> 24) < 254) {
					line(img, sortStart, x - 1, y);
					c0 = img.getRGB(x, y);
					sortStart = x;
				}
			}
			line(img, sortStart, img.getWidth() - 1, y);
		}
		return img;
	}
}
