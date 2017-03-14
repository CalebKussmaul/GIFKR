package filter.filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import filter.base.FilterLoader;
import filter.base.ImageFilter;

public class MultiFilter extends ImageFilter {

	private List<ImageFilter> filters;
	private JPanel filterPanel;
	
	public MultiFilter() {
		List<ImageFilter> filters = new ArrayList<ImageFilter>();
		JPanel filterPanel = getPanel();
		
		JButton addButton = new JButton("add Filter");
		JButton removeButton = new JButton("delete Filter");
		
	}
	
	@Override
	protected BufferedImage apply(BufferedImage img) {
		
		for(ImageFilter f : filters)
			img = f.getFilteredImage(img);
		
		return img;
	}

	@Override
	public String getCategory() {
		return "Meta";
	}
	
	private JPanel getPanel() {
		JPanel p = new JPanel();
		
		return p;
	}
}
