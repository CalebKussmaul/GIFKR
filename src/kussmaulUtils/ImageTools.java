package kussmaulUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

enum ImageHandling{FITWIDTH, FITHEIGHT, FITBOTH, ACTUALSIZE, CUSTOM}

public class ImageTools {


	public static BufferedImage toBufferedImage(Image img) {
		if(img instanceof BufferedImage)
			return (BufferedImage)img;

		BufferedImage imgb=new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=imgb.createGraphics();
		g.drawImage(img,0, 0, null);

		g.dispose();

		return imgb;
	}

	public static BufferedImage resize(BufferedImage img, int width, int height, boolean fast){
		if(img.getWidth()==width && img.getHeight()==height)
			return deepCopy(img);

		width = Math.max(width, 1);
		height = Math.max(height, 1);

		Image resizedImg = img.getScaledInstance(width, height, fast ? Image.SCALE_FAST : Image.SCALE_SMOOTH);
		if(resizedImg instanceof BufferedImage)
			return (BufferedImage)resizedImg;

		BufferedImage imgb=new BufferedImage(width, height, img.getType());
		Graphics2D g=imgb.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, fast ? RenderingHints.VALUE_INTERPOLATION_BILINEAR: RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(resizedImg,0, 0, null);

		g.dispose();

		return imgb;
	}
	
	public static BufferedImage fitBoth(BufferedImage img, int width, int height, Object interpolation){
		if(img.getWidth()==width && img.getHeight()==height)
			return deepCopy(img);
		
		if (interpolation.equals(RenderingHints.VALUE_INTERPOLATION_BICUBIC))
			return fitBoth(img, width, height, false);

		width = Math.max(width, 1);
		height = Math.max(height, 1);

		BufferedImage imgb=new BufferedImage(width, height, img.getType());
		Graphics2D g=imgb.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
		g.drawImage(img, 0, 0, imgb.getWidth(), imgb.getHeight(), null);

		g.dispose();

		return imgb;
	}

	public static BufferedImage fitWidth(BufferedImage img, int width, int height, boolean fast){
		img=resize(img, width, (int)(img.getHeight()*(width/(double)img.getWidth())), fast);
		if(img.getHeight()>height)
			img=img.getSubimage(0, (img.getHeight()-height)/2, img.getWidth(), height);

		BufferedImage fullImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)fullImage.getGraphics();
		if(!fast){
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		g.drawImage(img, 0, (height-img.getHeight())/2, null);

		g.dispose();

		return fullImage;	
	}

	public static BufferedImage fitHeight(BufferedImage img, int width, int height, boolean fast){
		img=resize(img, (int)(img.getWidth()*(height/(double)img.getHeight())), height, fast);
		if(img.getHeight()>height){
			img=img.getSubimage((img.getWidth()-width)/2, 0, width, img.getHeight());
		}

		BufferedImage fullImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)fullImage.getGraphics();
		if(!fast){
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		g.drawImage(img, (width-img.getWidth())/2, 0, null);

		g.dispose();

		return fullImage;	
	}

	public static BufferedImage doNotScale(BufferedImage img, int width, int height, boolean fast){

		width = Math.max(width, 1);
		height = Math.max(height, 1);

		BufferedImage fullImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)fullImage.getGraphics();
		if(!fast){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}
		g.drawImage(img, (fullImage.getWidth()-img.getWidth())/2, (height-img.getHeight())/2, null);

		g.dispose();

		return fullImage;
	}

	public static BufferedImage fitBoth(BufferedImage img, int width, int height, boolean fast){
		return resize(img, width, height, fast);
	}

	public static BufferedImage customFit(BufferedImage img, int width, int height, double scalex, double scaley, boolean fast){
		img = resize(img, (int) Math.round(img.getWidth()*scalex), (int) Math.round(img.getHeight()*scaley), fast);
		return  doNotScale(img, width, height, fast);
	}

	public static BufferedImage scale(BufferedImage img, double factor, boolean fast){
		return resize(img, (int) Math.round(img.getWidth()*factor), (int) Math.round(img.getHeight()*factor), fast);
	}

	public static BufferedImage fit(BufferedImage img, int width, int height, ImageHandling ih, double scalex, double scaley, boolean fast){
		if(ih.equals(ImageHandling.ACTUALSIZE))
			return doNotScale(img, width, height, fast);
		if(ih.equals(ImageHandling.CUSTOM))
			return customFit(img, width, height, scalex, scaley, fast);
		if(ih.equals(ImageHandling.FITBOTH))
			return fitBoth(img, width, height, fast);
		if(ih.equals(ImageHandling.FITHEIGHT))
			return fitHeight(img, width, height, fast);
		if(ih.equals(ImageHandling.FITWIDTH))
			return fitWidth(img, width, height, fast);
		return null;
	}

	public static BufferedImage addEmptyBorder(BufferedImage img, int Pixels, boolean fast){
		return doNotScale(img, img.getWidth()+2*Pixels, img.getHeight()+2*Pixels, fast);
	}
	public static BufferedImage scaleToHeight(BufferedImage img, int height, boolean fast){
		return resize(img, (int) Math.round(img.getWidth()*(height/(double)img.getHeight())), height, fast);
	}
	public static BufferedImage scaleToWidth(BufferedImage img, int width, boolean fast){
		return resize(img, width, (int) Math.round(img.getHeight()*(width/(double)img.getWidth())), fast);
	}

	public static BufferedImage limitToSize(BufferedImage img, int maxw, int maxh, boolean fast) {
		//return new BufferedImage(maxw, maxh, BufferedImage.TYPE_3BYTE_BGR);
		float ratio = img.getWidth()/(float)img.getHeight();
		float maxRatio = maxw/(float)maxh;

		if(ratio < maxRatio)
			return scaleToHeight(img, maxh, fast);
		else
			return scaleToWidth(img, maxw, fast);
	}

	public static BufferedImage rotate(BufferedImage img, double angle, boolean fast) {
		angle = angle % 360;
		if(angle == 0)
			return ImageTools.deepCopy(img);

		angle = Math.toRadians(angle);

		double sin = Math.abs(Math.sin(angle)),
				cos = Math.abs(Math.cos(angle));

		int w = img.getWidth(), h = img.getHeight();

		double neww = (int) (w*cos + h*sin),
				newh = (int) (h*cos + w*sin);

		BufferedImage bimg = new BufferedImage((int) Math.round(neww), (int) Math.round(newh), BufferedImage.TYPE_INT_ARGB); //alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bimg.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_RENDERING, fast ? RenderingHints.VALUE_RENDER_SPEED : RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		g.translate((neww - w)/2, (newh - h)/2);
		g.rotate(angle, w/2.0, h/2.0);
		g.drawImage(img, null, null);
		g.dispose();

		return bimg;
	}

	public static BufferedImage unrotate(BufferedImage img, double angle, int origw, int origh, boolean subpixel, boolean fast) {

		img = rotate(img, 360 - (angle % 360), fast);

		int startx = (img.getWidth()-origw)/2;
		int starty = (img.getHeight()-origh)/2;

		int extra = (img.getWidth() - origw) >2 ? 1 : 0;

		img = img.getSubimage(startx, starty, origw + extra, origh + extra);

		AffineTransform t = new AffineTransform();

		if(subpixel) {
			double xerr = 0, yerr = 0;
			for(int x = 0; x < img.getWidth(); x++)
				xerr += ImageTools.getAlpha(img.getRGB(x, 0));
			for(int y = 0; y < img.getHeight(); y++)
				yerr += ImageTools.getAlpha(img.getRGB(0, y));

			xerr /= (img.getWidth() * 255);
			xerr = 1 - xerr;
			yerr /= (img.getHeight() * 255);
			yerr = 1 - yerr;
			t.translate(-yerr, -xerr);
		}

		BufferedImage ret = new BufferedImage(origw, origh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = ret.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.drawImage(img, t, null);
		g.dispose();

		return ret;
	}

	public static BufferedImage rotate(BufferedImage img, double angle, Object interpolation) {
		if(angle % 360 == 0)
			return ImageTools.deepCopy(img);

		angle = Math.toRadians(angle);

		double sin = Math.abs(Math.sin(angle)),
				cos = Math.abs(Math.cos(angle));

		int w = img.getWidth(), h = img.getHeight();

		double neww = (int) (w*cos + h*sin),
				newh = (int) (h*cos + w*sin);

		BufferedImage bimg = new BufferedImage((int) Math.round(neww), (int) Math.round(newh), BufferedImage.TYPE_INT_ARGB); //alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bimg.createGraphics();

		if(interpolation.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		else
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);

		g.translate((neww - w)/2, (newh - h)/2);
		g.rotate(angle, w/2.0, h/2.0);
		g.drawImage(img, null, null);
		g.dispose();

		return bimg;
	}

	public static BufferedImage unrotate(BufferedImage img, double angle, int origw, int origh, boolean subpixel, Object interpolation) {

		img = rotate(img, 360 - angle, interpolation);

		int startx = (img.getWidth()-origw)/2;
		int starty = (img.getHeight()-origh)/2;

		int extra = (img.getWidth() - origw) >2 ? 1 : 0;

		img = img.getSubimage(startx, starty, origw + extra, origh + extra);

		AffineTransform t = new AffineTransform();

		if(subpixel) {
			double xerr = 0, yerr =0;
			for(int x = 0; x < img.getWidth(); x++)
				xerr += ImageTools.getAlpha(img.getRGB(x, 0));
			for(int y = 0; y < img.getHeight(); y++)
				yerr += ImageTools.getAlpha(img.getRGB(0, y));

			xerr /= (img.getWidth() * 255);
			xerr = 1 - xerr;
			yerr /= (img.getHeight() * 255);
			yerr = 1 - yerr;
			t.translate(-yerr, -xerr);
		}

		BufferedImage ret = new BufferedImage(origw, origh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = ret.createGraphics();
		if(interpolation.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		else
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);

		g.drawImage(img, t, null);
		g.dispose();

		return ret;
	}


	public static BufferedImage deepCopy(BufferedImage img) {
		ColorModel cm = img.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = img.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage getResourceImage(String name){
		try {
			return ImageIO.read(ClassLoader.getSystemResource(name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage getBestIconForSize(BufferedImage[] imgs, int size) {
		BufferedImage bestSoFar = null;

		for(BufferedImage img : imgs) {
			if(bestSoFar == null)
				bestSoFar = img;
			else if(isSquareish(img) && !isSquareish(bestSoFar))
				bestSoFar = img;
			else if(!(!isSquareish(img) && isSquareish(bestSoFar))) {
				if(bestSoFar.getWidth() < size && img.getWidth() > bestSoFar.getWidth())
					bestSoFar = img;
				else if(bestSoFar.getWidth() > size && img.getWidth() < bestSoFar.getWidth() && img.getWidth() >= size)
					bestSoFar = img;
			}
		}
		return bestSoFar;
	}

	public static BufferedImage getBestIconForSize(List<BufferedImage> imgs, int size) {
		return getBestIconForSize(imgs.toArray(new BufferedImage[imgs.size()]), size);
	}

	public static boolean isSquareish(BufferedImage img) {
		return Math.abs(img.getWidth()-img.getHeight()) < 5 ;
	}

	public static int getAlpha(int c) {
		return (c >> 24) & 255;
	}
	public static int getRed(int c) {
		return (c >> 16) & 255;
	}
	public static int getGreen(int c) {
		return (c >> 8) & 255;
	}
	public static int getBlue(int c) {
		return c & 255;
	}
	public static int getTotalRGB(int c) {
		return ((c >> 16) & 255) + ((c >> 8) & 255) + (c & 255);
	}
	public static float getMaxRGB(int color) {
		return Math.max(Math.max((color >> 16) & 255, (color >> 8) & 255), color & 255);
	}
	public static float[] getHSB(int c) {
		float[] hsbVals = new float[3];
		Color.RGBtoHSB((c >> 16) & 255, (c >> 8) & 255, c & 255, hsbVals);		
		return hsbVals;
	}
	public static int gradientRGB(int c0, int c1, float level) {

		float comp = 1-level;

		int a = (int) (level * ((c0 >> 24) & 255)	+ comp * ((c1 >> 24) & 255));
		int r = (int) (level * ((c0 >> 16) & 255) 	+ comp * ((c1 >> 16) & 255));
		int g = (int) (level * ((c0 >> 8) & 255) 	+ comp * ((c1 >> 8) & 255));
		int b = (int) (level * (c0 & 255) 			+ comp * (c1 & 255));

		return (a << 24 | r << 16 | g << 8 | b);

	}

	public static int toRGB(int a, int r, int g, int b) {
		return (a << 24 | r << 16 | g << 8 | b);
	}
	public static int toRGB(int r, int g, int b) {
		return (0xFF000000 | r << 16 | g << 8 | b);
	}

	//	public static BufferedImage[] getRGBChannels(BufferedImage img) {
	//		
	//		BufferedImage[] channels = new BufferedImage[3];
	//		
	//		for(int i = 0; i < 3; i++)
	//			channels[i] = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
	//		
	//		return channels;
	//		
	//	}

	private static BufferedImage getChannel1(BufferedImage img, int channel) {

		//		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

		//		for(int y = 0; y < img.getHeight(); y++) //slow
		//			for(int x = 0; x < img.getWidth(); x++)
		//				img2.setRGB(x, y, img.getRGB(x, y) & channel);	

		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);

		int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());


		for(int i = 0; i < rgb.length; i ++)
			rgb[i] = rgb[i] & channel;

		img2.setRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());		

		return img2;
	}

	public static BufferedImage getChannel(BufferedImage img, int channel) {

		if(channel == 0xFFFF0000)
			channel = 0;
		else if(channel == 0xFF00FF00)
			channel = 1;
		else if(channel == 0xFF0000FF)
			channel = 2;

		WritableRaster r = img.getRaster();

		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[] nullArray = null;

		img2.getRaster().setSamples(0, 0, r.getWidth(), r.getHeight(), channel, r.getSamples(0, 0, r.getWidth(), r.getHeight(), channel, nullArray));
		return img2;

	}

	public static BufferedImage getChannel2(BufferedImage img, int channel) {

		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		int[] oldd = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		int[] newd = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();

		for(int i = 0; i < newd.length; i++) {
			newd[i] = oldd[i] & channel;
		}

		return img2;

	}

	public static BufferedImage getRedChannel(BufferedImage img) {
		return getChannel(img, 0xFFFF0000);
	}

	public static BufferedImage getGreenChannel(BufferedImage img) {
		return getChannel(img, 0xFF00FF00);
	}

	public static BufferedImage getBlueChannel(BufferedImage img) {
		return getChannel(img, 0xFF0000FF);
	}

	public static BufferedImage getAlphaChannel(BufferedImage img) {
		return getChannel(img, 0xFF000000);
	}

	public static BufferedImage combine(BufferedImage a, BufferedImage r, BufferedImage g, BufferedImage b) {

		//		BufferedImage img = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//
		//		for(int y = 0; y < r.getHeight(); y++)
		//			for(int x = 0; x < r.getWidth(); x++)
		//				img.setRGB(x, y, r.getRGB(x, y) | g.getRGB(x, y) | b.getRGB(x, y));
		//		//img.setRGB(x, y, 0xFF000000 + (r.getRGB(x, y) & 0x00FF0000) + (g.getRGB(x, y) & 0x0000FF00) + (b.getRGB(x, y) & 0x000000FF));
		//		return img;


		//slower
		//		int[] nullArray = null;
		//
		//		r.getRaster().setSamples(0, 0, g.getRaster().getWidth(), g.getRaster().getHeight(), 1, g.getRaster().getSamples(0, 0, g.getWidth(), g.getHeight(), 1, nullArray));
		//		r.getRaster().setSamples(0, 0, b.getRaster().getWidth(), b.getRaster().getHeight(), 2, b.getRaster().getSamples(0, 0, b.getWidth(), b.getHeight(), 2, nullArray));
		//		
		//		return r;


		int w = r.getWidth(), h = r.getHeight();

		int[] rarr = r.getRGB(0, 0, w, h, null, 0, w);
		int[] garr = g.getRGB(0, 0, w, h, null, 0, w);
		int[] barr = b.getRGB(0, 0, w, h, null, 0, w);

		for(int i = 0; i < rarr.length; i ++)
			rarr[i] = (rarr[i] & 0xFFFF0000) | (garr[i] & 0x0000FF00) | (barr[i]& 0x000000FF);

		r.setRGB(0, 0, w, h, rarr, 0, w);		

		return r;
	}
}
/*g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
 */
