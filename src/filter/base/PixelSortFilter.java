package filter.base;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import kussmaulUtils.ImageTools;

public abstract class PixelSortFilter extends ImageFilter {

	public float threshold;
	public Fill fill = Fill.STANDARD;
	public Color gradientColor = Color.black;
	public Comparator comparator = Comparator.TOTAL_RGB;

	protected int getComparator(int color) {
		return comparator.getComparator(color);
	}

	@Override
	public String getCategory() {
		return "Pixel sort";
	}

	public static enum Comparator {TOTAL_RGB, MAX_RGB, HUE, SATURATION; 
		public String toString() {
			switch(this) {
			case TOTAL_RGB:	return "Total RGB";
			case MAX_RGB:	return "Max RGB";
			case HUE:		return "Hue";
			case SATURATION:return "Saturation";
			default:		return null;
			}
		}

		public int getComparator(int color) {
			switch(this) {
			case TOTAL_RGB:	return ((color >> 16) & 255) + ((color >> 8) & 255) + (color & 255);
			case MAX_RGB:	return 3 * Math.max(Math.max((color >> 16) & 255, (color >> 8) & 255), color & 255);
			case HUE:		return 3 * (int)(255 * ImageTools.getHSB(color)[0]);
			case SATURATION:			
				int max =  Math.max(Math.max((color >> 16) & 255, (color >> 8) & 255), color & 255);
				if(max == 0) return 0;
				int min =  Math.min(Math.min((color >> 16) & 255, (color >> 8) & 255), color & 255);
				return 3 * (int) (255 * (min/(float)max));
			default:		return 0;
			}
		}
	}

	public static enum Fill {STANDARD, SOLID, GRADIENT;

		public String toString() {
			switch(this) {
			case GRADIENT:	return "Gradient";
			case SOLID:		return "Solid";
			case STANDARD:	return "Standard";
			default:		return null;
			}
		}
	}

	protected void line(BufferedImage img, int x0, int x1, int y) {

		if(x1-x0 < 1)
			return;

		switch (fill) {
		case STANDARD:
			lineSort(img, x0, x1, y);
			break;
		case SOLID:
			lineFill(img, x0, x1, y);
			break;
		case GRADIENT:
			lineGradient(img, x0, x1, y);
			break;
		}
	}

	protected void lineSort(BufferedImage img, int x0, int x1, int y) {

		int length = x1 - x0 + 1;

		int[] javaSucks = new int [length]; //primative arrays cannot easily be sorted with a custom comparator, must be copied to integer[]
		img.getRGB(x0, y, length, 1, javaSucks, 0, img.getWidth());

		Integer[] pixels = new Integer[javaSucks.length];

		for(int i = 0; i < pixels.length; i++)
			pixels[i] = javaSucks[i];

		Arrays.sort(pixels, (a, b) -> {
			return getComparator(b) - getComparator(a);
		});

		for(int i = 0; i < pixels.length; i++)
			javaSucks[i] = pixels[i];

		img.setRGB(x0, y, length, 1, javaSucks, 0, img.getWidth());
	}

	protected void lineFill(BufferedImage img, int x0, int x1, int y) {

		int c = img.getRGB(x0, y);	

		for(int x = x0+1; x <= x1; x++) {
			img.setRGB(x, y, c);
		}
	}
	protected void lineGradient(BufferedImage img, int x0, int x1, int y) {

		int length = x1-x0 +1;
		int color  = gradientColor.getRGB();

		for(int i = 0, c0 = img.getRGB(x0, y); i < length; i++) {
			float f = ((length - i)/(float)(length));
			img.setRGB(x0 + i, y, ImageTools.gradientRGB(c0, color, f));
		}
	}
}
