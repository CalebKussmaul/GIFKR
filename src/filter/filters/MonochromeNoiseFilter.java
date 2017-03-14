package filter.filters;

import filter.base.PixelImageFilter;

public class MonochromeNoiseFilter extends PixelImageFilter {

	public float filterLevel;
	
	@Override
	public int apply(int c) {
		
		int noise = rand.nextInt(1+ (int) (filterLevel * 255));
		return ((c >> 24) & 255) << 24 | 
				((int) (filterLevel * noise) + (int) ((1-filterLevel) * ((c >> 16) & 255))) << 16 | 
				((int) (filterLevel * noise) + (int) ((1-filterLevel) * ((c >>  8) & 255))) <<  8 | 
				((int) (filterLevel * noise) + (int) ((1-filterLevel) * ((c & 255))));
	}

}
