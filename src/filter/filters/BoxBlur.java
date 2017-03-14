package filter.filters;

import filter.base.ConvolutionFilter;

public class BoxBlur extends ConvolutionFilter {

	public int size = 1;
	
	@Override
	public float[][] getMatrix() {
		
		int trueSize = Math.max(1, size);
		
		float[][] matrix = new float[trueSize][trueSize];
		
		for(int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = 1f/(trueSize*trueSize);
			}
		}
		
		normalize(matrix);
		return matrix;
	}
	
	@Override
	protected boolean randomControls() {
		return false;
	}
}
