package filter.filters;

import filter.base.AlgebraicImageFilter;

public class SubtractFilter extends AlgebraicImageFilter {

	public int constant;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public int apply(int channel) {
		return Math.floorMod(channel - constant, 256);
	}

}
