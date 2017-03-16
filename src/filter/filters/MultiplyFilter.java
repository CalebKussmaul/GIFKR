package filter.filters;

import filter.base.AlgebraicImageFilter;

public class MultiplyFilter extends AlgebraicImageFilter {

	public double multiplicationFactor = 1d;
	
	@Override
	public int apply(int channel) {
		return (int) (channel * multiplicationFactor);
	}
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	protected boolean useLookupTable() {
		return true;
	}
}
