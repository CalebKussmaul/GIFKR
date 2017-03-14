package filter.filters;

import filter.base.AlgebraicImageFilter;

public class ReplacementFilter extends AlgebraicImageFilter {

	public float filterLevel;
	
	@Override
	protected boolean useLookupTable() {
		return true;
	}
	
	@Override
	public int apply(int channel) {
		return rand.nextInt(1+ (int) (filterLevel * 255)) + (int) ((1-filterLevel)* channel);
	}

}
