package filter.filters;

import java.awt.Color;

import filter.base.PixelImageFilter;

public class FadeFilter extends PixelImageFilter {

	public Color color = Color.black;
	public float level;

	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public int apply(int c) {
		return ((c >> 24) & 255) << 24 |
				((int) ((1f-level) * ((c >> 16) & 255) + level * color.getRed())) << 16 |
				((int) ((1f-level) * ((c >> 8) & 255) + level * color.getGreen())) << 8 |
				((int) ((1f-level) * (c & 255) + level * color.getBlue()));
	}

	@Override
	public String getCategory() {
		return "Utilities";
	}
}
