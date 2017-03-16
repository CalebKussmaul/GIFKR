package filter.filters;

import filter.base.ConvolutionFilter;

public class MatrixFilter extends ConvolutionFilter {

	public float[][] matrix = new float[][] {
		{0,0,0},
		{0,1,0},
		{0,0,0}};
	
	@Override
	public float[][] getMatrix() {
		return matrix;
	}

}
