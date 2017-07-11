package filter.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;

public class PerspectiveFilter extends ImageFilter {

	public float shift = 0;
	
	@Override
	protected BufferedImage apply(BufferedImage img) {
		
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = img2.createGraphics();
		
		for(int y = 0; y < img.getHeight(); y++) {
			BufferedImage row = img.getSubimage(0, y, img.getWidth(), 1);
			
			int offset = Math.round((y/2f - img.getHeight()/4f) * (2 * shift * img.getWidth()));
			offset = offset % img.getWidth();
			
			g.drawImage(row, offset, y, null);
			g.drawImage(row, (offset < 0 ? 1 : -1) * img.getWidth() + offset, y, null);
		}

		g.dispose();
		
		return img2;
	}

}
