package filter.filters;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import filter.base.ImageFilter;

public class JPEGFilter extends ImageFilter {

	public float compressionLevel;
	
	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {

		if(compressionLevel == 0f)
			return img;
		
		try {
			ImageWriter iw = ImageIO.getImageWritersByFormatName("jpeg").next();
			JPEGImageWriteParam iwp = (JPEGImageWriteParam) iw.getDefaultWriteParam();
			iwp.setOptimizeHuffmanTables(false);
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
			iwp.setCompressionQuality(1f-compressionLevel);


			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			iw.setOutput(new MemoryCacheImageOutputStream(baos)); 

			IIOImage outputImage = new IIOImage(img, null, null);
			iw.write(null, outputImage, iwp);
			iw.dispose();
			
			baos.flush(); 
			byte[] returnImage = baos.toByteArray(); 
			baos.close();
			
			
			BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(returnImage));
			
			if(img2 == null)
				throw new Exception();
			else
				img = img2;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return img;
	}

}
