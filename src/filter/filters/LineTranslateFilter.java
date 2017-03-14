package filter.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;

public class LineTranslateFilter extends ImageFilter {

	public float shift;
	public float lines;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		int offset = (int) (shift * img.getWidth());
		int lineHeight = Math.min(img.getHeight(), (int) ((1f-lines) * img.getHeight()) + 1);
		
		if(offset == 0 || offset == img.getWidth())
			return img;
		
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = img2.createGraphics();
		
		int y;
		boolean b = false;
		for(y = 0; y < img.getHeight(); y+=lineHeight, b = !b) {
			BufferedImage chunk = img.getSubimage(0, y, img.getWidth(), Math.min(lineHeight, img.getHeight()-y));
			
			g.drawImage(chunk, b ? offset : -offset, y, null);
			g.drawImage(chunk, b ? offset-img.getWidth() : img.getWidth()-offset, y, null);
		}

		g.dispose();
		
		return img2;
	}
}
