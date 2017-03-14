package filter.filters;

import filter.base.AlgebraicImageFilter;

public class ColorLimitFilter extends AlgebraicImageFilter {

	public float tolerance =.5f;
	
	@Override
	protected boolean useLookupTable() {
		return true;
	}
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public int apply(int channel) {
//		
//		return (1+ (int) (tolerance * 255)) * Math.round((channel / (float) (1+ (int) (tolerance * 255))));
		
		int pxTol = Math.max(1, (int) (tolerance*255));
		int remainder = (channel % pxTol);
		
		return channel - remainder;
	}

}
