package filter.filters;

import filter.base.ConvolutionFilter;

public class GaussianBlur extends ConvolutionFilter {

	public double sigma = 1;
	
	@Override
	public float[][] getMatrix() {
		
		int dim = dim();
		
		float[][] matrix = new float[dim][dim];
		
		for(int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = gaussianDistribution(Math.sqrt(Math.pow(dim/2 - j, 2) + Math.pow(dim/2 - j, 2)));
			}
		}
		
		normalize(matrix);
		return matrix;
	}

	private float gaussianDistribution(double x) {
		return (float) (Math.exp(-x*x/(2 * sigma * sigma)) * (1d/Math.sqrt(2 * Math.PI) * sigma));
	}
	
	private int dim() {
		return 2 * (int) sigma + 1;
	}
	
	@Override
	protected boolean randomControls() {
		return false;
	}
}
