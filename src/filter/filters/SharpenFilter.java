package filter.filters;

import filter.base.ConvolutionFilter;

public class SharpenFilter extends ConvolutionFilter {

	@Override
	public float[][] getMatrix() {
		return new float[][] {
			{ 0f,-1f,  0},
			{-1f, 5f,-1f},
			{ 0f,-1f,  0}
		};
	}

}
