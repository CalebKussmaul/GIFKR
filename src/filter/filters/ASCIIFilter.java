package filter.filters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;

import filter.base.TextFilter;
import utils.ImageTools;

public class ASCIIFilter extends TextFilter {

	public String characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*-+=\\;<>?/~•»—–¬‹·. ";	
	
	public Color fontColor = Color.white;
	public boolean invert = false;
	public float rangeFix = 0;
	
	private TreeMap<Float, Character> map = new TreeMap<Float, Character>();

	@Override
	public char getChar(int color, int count) {
		
		float darkness = ((ImageTools.getTotalRGB(color)*(ImageTools.getAlpha(color)/255f))/765f);
		if(invert)
			darkness = 1 - darkness;
		darkness = (1-rangeFix) * darkness + rangeFix * darkness * (map.floorKey(9999f) == null ? 1 : map.floorKey(9999f));
		
		Map.Entry<Float, Character> low = map.floorEntry(darkness);
		Map.Entry<Float, Character> high = map.ceilingEntry(darkness);
		Character res = ' ';
		if (low != null && high != null) {
		    res = Math.abs(darkness-low.getKey()) < Math.abs(darkness-high.getKey())
		    ?   low.getValue()
		    :   high.getValue();
		} else if (low != null || high != null) {
		    res = low != null ? low.getValue() : high.getValue();
		}
		return res;
	}
	
	@Override
	public Color getColor(int color, int count) {
		return fontColor;
	}

	@Override
	public void beforeFilter() {
		
		map.clear();
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		FontMetrics f = img.createGraphics().getFontMetrics(font.deriveFont(Math.min(30, font.getSize2D())));
		
		float max = 0;
		float[] levels = new float[characterSet.length()];
		
		for(int i = 0; i < characterSet.length(); i++) {
			float fill = getFillLevel(characterSet.charAt(i), f);
			if(fill > max)
				max = fill;
			levels[i] = fill;
		}
		
		for(int i = 0; i < characterSet.length(); i++) {
			map.put(levels[i] *  (1f/max)*.5f, characterSet.charAt(i));
		}
	}
	
	private float getFillLevel(char c, FontMetrics f) {
		
		BufferedImage img = new BufferedImage(f.charWidth(c), f.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = img.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antialiasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(c+"", 0, img.getHeight()-f.getDescent());
		
		int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		int totalOpacity = 0;
		
		for(int color : rgb)
			totalOpacity += ImageTools.getAlpha(color);
		
		return totalOpacity / (float)(rgb.length*255);
	}
}
