package filter.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;
import kussmaulUtils.ImageTools;

public class DealWithItFilter extends ImageFilter {

	private static final BufferedImage glasses = ImageTools.getResourceImage("deal.png");
	
	public float scale = .1f;
	public float xPosition = .5f;
	public float yPosition = .5f;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		
		Graphics2D g = img.createGraphics();
		
		int glassHeight = (int) (scale * img.getHeight());
		
		BufferedImage scaledGlass = ImageTools.scaleToHeight(glasses, glassHeight, false);
		
		int x = (int) (xPosition * (img.getWidth()+ scaledGlass.getWidth())) - scaledGlass.getWidth();
		int y = (int) (yPosition * (img.getHeight()+ scaledGlass.getHeight())) - scaledGlass.getHeight();
		
		g.drawImage(scaledGlass, x, y, null);
		
		return img;
	}
	
	@Override
	public String getCategory() {
		return "Novelty";
	}
}
