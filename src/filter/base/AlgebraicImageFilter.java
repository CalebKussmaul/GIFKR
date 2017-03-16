package filter.base;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public abstract class AlgebraicImageFilter extends ImageFilter {

	private int[] lookupTable;

	public AlgebraicImageFilter() {
		lookupTable = new int[256];
	}

	public abstract int apply(int channel);

	protected boolean useLookupTable() {
		return false;
	}

	@Override
	protected boolean angleControls() {
		return false;
	}

	@Override
	protected final BufferedImage apply(BufferedImage img) {

		int[] data = new int[img.getWidth()*img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());

		if(useLookupTable()) {

			for(int i = 0; i < 256; i++)
				lookupTable[i] = apply(i);

			IntStream.range(0, data.length).parallel().forEach(i -> {
				int c = data[i];
				data[i] = (c & 0xFF000000) | lookupTable[(c >> 16) & 255] << 16 | lookupTable[(c >> 8) & 255] << 8 | lookupTable[c & 255];
			});
			//			for(int i = 0; i < data.length; i++) {
			//				int c = data[i];
			//				data[i] = (c & 0xFF000000) | lookupTable[(c >> 16) & 255] << 16 | lookupTable[(c >> 8) & 255] << 8 | lookupTable[c & 255];
			//			}
		}
		else {
			//			IntStream.range(0, data.length).parallel().forEach(i -> {
			//				int c = data[i];
			//				data[i] = ((c >> 24) & 255) << 24 | apply((c >> 16) & 255) << 16 | apply((c >> 8) & 255) << 8 | apply(c & 255);
			//			});
			for(int i = 0; i < data.length; i++) {
				int c = data[i];
				data[i] = (c & 0xFF000000) | apply((c >> 16) & 255) << 16 | apply((c >> 8) & 255) << 8 | apply(c & 255);
			}
		}

		img.setRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());
		return img;
	}

	@Override
	public String getCategory() {
		return "Algebraic";
	}
}
