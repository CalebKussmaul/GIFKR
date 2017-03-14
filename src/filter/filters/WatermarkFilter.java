package filter.filters;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;

public class WatermarkFilter extends ImageFilter {

	public static enum TextPosition{TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT; 
	
		public int getX(int width, int strWidth) {
			switch (this) {
			case TOP_LEFT: 
			case BOTTOM_LEFT: return 0;
			case TOP_RIGHT:
			case BOTTOM_RIGHT: return width - strWidth;
			}
			return 0;
		}
		
		public int getY(int height, int strHeight) {
			switch (this) {
			case TOP_LEFT: 
			case TOP_RIGHT: return 0;
			case BOTTOM_LEFT:
			case BOTTOM_RIGHT: return height - strHeight;
			}
			return 0;
		}
	}
	
	public String text = "kussmaul.net/gifkr.html";
	public Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	public Color fontColor = Color.white;
	public Color backgroundColor = Color.black;
	public double fontSize = 12;
	public TextPosition position = TextPosition.TOP_LEFT;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		
		final int minSize = 2;
		//final int maxSize = 2*img.getWidth();

		float fontS = (float) Math.max(fontSize, minSize);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		

		g.setFont(font.deriveFont(fontS));
		FontMetrics fm = g.getFontMetrics();
		int strWidth = fm.stringWidth(text), strHeight = fm.getHeight();
		
		g.setColor(backgroundColor);
		g.fillRect(position.getX(img.getWidth(), strWidth), position.getY(img.getHeight(), strHeight), strWidth, strHeight);
		
		g.setColor(fontColor);
		g.drawString(text, position.getX(img.getWidth(), strWidth), position.getY(img.getHeight(), strHeight)-fm.getDescent()+strHeight);
		
		return img;
	}

	@Override
	public String getCategory() {
		return "Utilities";
	}
}
