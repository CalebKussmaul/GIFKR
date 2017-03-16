package filter.filters;

import filter.base.AlgebraicImageFilter;
import filter.base.ControlOverride;

public class AddFilter extends AlgebraicImageFilter {
	
	@ControlOverride(max = "255")
	public int constant;
	
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public int apply(int channel) {
		return channel + constant;
	}
	
	@Override
	protected boolean useLookupTable() {
		return true;
	}
}
