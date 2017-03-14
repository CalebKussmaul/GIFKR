package filter.filters;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;

public class ShiftFilter extends ImageFilter {


	public int passes = 10;	
	public float squareSize;
	public float distance;
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		
		int size = (int) (squareSize * Math.min(img.getWidth(), img.getHeight()));
		int shift = (int) (distance * Math.min(img.getWidth(), img.getHeight()));
		
		for(int i = 0; i < passes; i++) {
			
			int x = (int) (Math.random() * (img.getWidth()-size));
			int y = (int) (Math.random() * (img.getHeight()-size));
			
			Graphics g = img.getGraphics();
			g.copyArea(x, y, size, size, (int) ((Math.random()-.5) * shift), (int) ((Math.random()-.5) * shift));	
		}
		
		return img;
	}
}
