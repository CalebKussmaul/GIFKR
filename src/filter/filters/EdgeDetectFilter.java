package filter.filters;

import filter.base.ConvolutionFilter;

public class EdgeDetectFilter extends ConvolutionFilter {

	@Override
	public float[][] getMatrix() {
		return new float[][] {
			{ 0f,-1f, 0f},
			{-1f, 4f,-1f},
			{ 0f,-1f, 0f}
		};
	}

	@Override
	protected boolean randomControls() {
		return false;
	}
}
