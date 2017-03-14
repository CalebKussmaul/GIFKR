package filter.filters;

import filter.base.AlgebraicImageFilter;

public class DivideFilter extends AlgebraicImageFilter {

	public double divisor;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public int apply(int channel) {
		return (int) ((255d*divisor)/(1+channel));
	}

}
