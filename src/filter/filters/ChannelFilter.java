package filter.filters;

import java.awt.image.BufferedImage;

import filter.base.ImageFilter;
import kussmaulUtils.ImageTools;

public class ChannelFilter extends ImageFilter {

	public ImageFilter redFilter	= new NoFilterFilter();
	public ImageFilter greenFilter	= new NoFilterFilter();
	public ImageFilter blueFilter 	= new NoFilterFilter();
	
	@Override
	protected BufferedImage apply(BufferedImage img) {
		
		BufferedImage rimg = ImageTools.getRedChannel(img);
		BufferedImage gimg = ImageTools.getGreenChannel(img);
		BufferedImage bimg = ImageTools.getBlueChannel(img);
		
		rimg = redFilter.getFilteredImage(rimg);
		gimg = greenFilter.getFilteredImage(gimg);
		bimg = blueFilter.getFilteredImage(bimg);
		
		return ImageTools.combine(null, rimg, gimg, bimg);
	}
	
	@Override
	public String getCategory() {
		return "Meta";
	}

}
