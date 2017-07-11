package filter.base;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import filter.filters.*;

public class FilterLoader {

	private static final File filterPath = new File(System.getProperty("user.home") + "/GIFKR Filters");

	private static List<ImageFilter> filters;

	static {
		try {

			if(!filterPath.exists()) {
				filterPath.mkdirs();
			}

			filters = new ArrayList<ImageFilter>();

			URLClassLoader cl = new URLClassLoader(new URL[]{filterPath.toURI().toURL()});

			for(File f : filterPath.listFiles()) {
				try {
					Class<?> c = cl.loadClass(f.getName().replace(".class", ""));

					if (ImageFilter.class.isAssignableFrom(c)) {
						filters.add((ImageFilter) c.newInstance());
					} else {
						System.err.println(c.getName() + "is a valid java class, but not an ImageFilter.");
					}

				} catch (Exception e) {
					System.err.println(f.getName() + " is not a valid class.");
				} catch (NoClassDefFoundError e) {
					System.err.println(f.getName() + " is already loaded. You cannot override built in filters.");
				}
			}

			try {
				cl.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		for(ImageFilter f : getDefaultFilters())
			filters.add(f);
	}

	public static boolean CopyFilterFiles(List<File> files, Component parent) {

		int response = JOptionPane.showConfirmDialog(parent, "<html><body><p style='width: 200px;'>"+FilterLoader.getFilterWarning()+"</p></body></html>", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		if(response != JOptionPane.OK_OPTION)
			return false;

		for(File f : files) {
			try {
				Files.copy(f.toPath(), new File(filterPath+"/"+f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	

//		GIFKR.restartApplication();

		return true;
	}

	public static String getFilterWarning() {
		return "Only install image filters from sources you trust. Filters will be loaded on next launch.";
	}

	public static List<ImageFilter> getFilters() {
		
		List<ImageFilter> filterClones = new ArrayList<ImageFilter>();
		
		for(ImageFilter filter : filters)
			try {
				filterClones.add(filter.getClass().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		
		return filterClones;
	}

	public static void deleteExternalFilters() {
		for(File f : filterPath.listFiles())
			f.delete();
	}
	
	private static ImageFilter[] getDefaultFilters() {
		return new ImageFilter[] {
				new NoFilterFilter(),
				new AddFilter(),
				new SubtractFilter(),
				new MultiplyFilter(),
				new DivideFilter(),
				new NoiseFilter(),
				new MonochromeNoiseFilter(),
				new ReplacementFilter(),
				new ColorLimitFilter(),
				new JPEGFilter(),
				new DealWithItFilter(),
				new DotMatrixFilter(),
				new GameOfLifeFilter(),
				new HSBFilter(),
				new LineTranslateFilter(),
				new WatermarkFilter(),
				new LowResFilter(),
				new MessageFilter(),
				new TheMatrixFilter(),
				new ASCIIFilter(),
				new FadeFilter(),
//				new ChannelFilter(),
				new ShiftFilter(),
				new RandomPixelSort(),
				new StandardPixelSort(),
				new LinePixelSort(),
				new EdgeDetectFilter(),
				new GaussianBlur(),
				new BoxBlur(),
				new MatrixFilter(),
				new SharpenFilter(),
				//new MultiFilter(),
				new PerspectiveFilter(),
		};
	}
	
	public static JComboBox<ImageFilter> getFilterBox() {
		List<ImageFilter> filterClones = FilterLoader.getFilters();
		Collections.sort(filterClones);
		JComboBox<ImageFilter> filterBox = new JComboBox<ImageFilter>(filterClones.toArray(new ImageFilter[filterClones.size()])) {

			private static final long serialVersionUID = 1615706533571206675L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension((int) (super.getPreferredSize().width * .75), super.getPreferredSize().height);
			}
		};
		for(int i = 0; i < filterClones.size(); i++) 
			if(filterClones.get(i).getClass().equals(NoFilterFilter.class))
				filterBox.setSelectedIndex(i);
		return filterBox;
	}
}
