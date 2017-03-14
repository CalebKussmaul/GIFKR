package filter.base;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public abstract class ConvolutionFilter extends ImageFilter {
	
	public boolean normalize = false;
	@ControlOverride(min = "-10000")
	public double multiplier = 1d;
	
	
	@Override
	public final BufferedImage apply(BufferedImage img) {
		
		float[][] matrix = getMatrix();
		float[] data = getKernelData(matrix);
		if(normalize)
			normalize(data); 
		scale(data);
		if(isZero(data))
			return new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		Kernel k = new Kernel(matrix[0].length, matrix.length, data);
		ConvolveOp op = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
		
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		img2.getGraphics().drawImage(img, 0, 0, null);
		
		return op.filter(img2, null);
	}
	
	@Override
	protected boolean angleControls() {
		return false;
	}
	
	public abstract float[][] getMatrix();
	
	
	private static final boolean isZero(float[] data) {
		
		for(float f : data)
			if(f > .0001f)
				return false;
		return true;
	}
	
	private static final float[] getKernelData(float[][] matrix) {
		float[] data = new float[matrix.length * matrix[0].length];
		for(int i = 0, count = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++, count++) {
				data[count] = matrix[i][j];
			}
		}
		return data;	
	}
	
	protected final void normalize(float[] data) {
		float sum = 0;
		for(float f : data)
			sum += f;
		if(sum == 0)
			return;
		for(int i = 0; i < data.length; i++)
			data[i] = data[i] / sum;
	}
	
	protected final void normalize(float[][] matrix) {
		float sum = 0;
		for(int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				sum += matrix[i][j];
		if(sum == 0)
			return;
		for(int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = matrix[i][j]/sum;
	}
	
	private final void scale(float[] data) {
		double trueScale = multiplier;
		if(Math.abs(multiplier) < .0001)
			trueScale = .0001;
		for(int i = 0; i < data.length; i++)
			data[i] = data[i] * (float) trueScale;
	}
	
	@Override
	public String getCategory() {
		return "Convolution";
	}
}