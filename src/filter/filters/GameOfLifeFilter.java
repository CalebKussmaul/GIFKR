package filter.filters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import filter.base.ControlOverride;
import filter.base.ImageFilter;
import filter.base.ControlOverride.ControlType;
import utils.ImageTools;

public class GameOfLifeFilter extends ImageFilter {

	public int iteration = 0;
	@ControlOverride(animationControl = ControlType.STATIC)
	public boolean preserveBackground = true;
	public Color backgroundColor = Color.black;
	@ControlOverride(animationControl = ControlType.STATIC)
	public boolean linger = true;
	@ControlOverride(animationControl = ControlType.STATIC)
	public boolean invert = false;
	@ControlOverride(animationControl = ControlType.STATIC)
	public float initialThreshold = .5f;
	@ControlOverride(animationControl = ControlType.STATIC)
	public float noise = 0f;
	@ControlOverride(animationControl = ControlType.STATIC)
	public float bitSize = 0f;

	private static int lastStep;
	private static boolean[][] lastGOL;
	private static BufferedImage lastImg;

	private static long lastHash = -1;

	@Override
	public BufferedImage apply(BufferedImage img) {		
		
		int origW = img.getWidth();
		int origH = img.getHeight();
		int newW = Math.max(1, (int) (img.getWidth()*(1f - bitSize)));
		
		img = ImageTools.scaleToWidth(img, newW, true);
		
		int newStep = iteration;

		if(newStep < lastStep || !checkHash(img)) {
			reset(img);
		}

		while(lastStep < newStep) {
			iterate();
		}

		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.drawImage(lastImg, -1, -1, null);

		img = ImageTools.fitBoth(img, origW, origH, true);
		
		return img;
	}


	private void iterate() {

		lastStep++;

		List<Add> add = new ArrayList<Add>();
		List<Remove> rem = new ArrayList<Remove>();

		for(int y = 1; y < lastGOL.length-1; y++) {
			for(int x = 1; x < lastGOL[y].length-1; x++) {

				int r = 0, g = 0, b = 0;
				int neighbors = 0;
				boolean occupied = lastGOL[y][x];


				x++;
				y--;
				if(lastGOL[y][x]) {
					if(!occupied) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				y++;
				if(lastGOL[y][x]) {
					if(!occupied) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				y++;
				if(lastGOL[y][x]) {
					if(!occupied) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				x--;
				if(lastGOL[y][x]) {
					if(!occupied && neighbors < 3) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;	
				}
				x--;
				if(lastGOL[y][x]) {
					if(!occupied && neighbors < 3) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				y--;
				if(lastGOL[y][x]) {
					if(!occupied && neighbors < 3) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				y--;
				if(lastGOL[y][x]) {
					if(!occupied && neighbors < 3) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				x++;
				if(lastGOL[y][x]) {
					if(!occupied && neighbors < 3) {
						int c = lastImg.getRGB(x, y);
						r += ImageTools.getRed(c);
						g += ImageTools.getGreen(c);
						b += ImageTools.getBlue(c);
					}
					neighbors++;
				}
				y++;

				if(occupied) {
					if(neighbors < 2 || neighbors > 3)
						rem.add(new Remove(x, y));
				}
				else if(neighbors == 3) {
					add.add(new Add(x, y, ImageTools.toRGB(r/3, g/3, b/3)));
				}
			}
		}

		for(Add a : add) {
			lastGOL[a.y][a.x] = true;
			lastImg.setRGB(a.x, a.y, a.color);
		}
		for(Remove r : rem) {
			lastGOL[r.y][r.x] = false;
			if(!linger)
				lastImg.setRGB(r.x, r.y, 0x00000000);
		}
	}


	private boolean checkHash(BufferedImage img) {

		long colorHash = 0;
		
		for(int i = 0; i < img.getWidth(); i++) {
			colorHash += img.getRGB(i, img.getHeight()/2) % 1024;
		}
		
		long newHash = randomSeed + colorHash + (long) ((invert ? 345 : 456) + (linger ? 567 : 678) + (preserveBackground ? 789 : 890 ) + (initialThreshold*5001) + (noise* 3006));

		if(lastImg != null && newHash == lastHash && img.getWidth()+2 == lastImg.getWidth())
			return true;
		lastHash = newHash;
		return false;
	}

	private void reset(BufferedImage img) {

		lastStep	= 0;
		lastImg		= ImageTools.addEmptyBorder(img, 1, false);
		lastGOL		= new boolean[lastImg.getHeight()][lastImg.getWidth()];

		for(int y = 1; y < lastGOL.length-1; y++) {
			for(int x = 1; x < lastGOL[y].length-1; x++) {
				if(invert ^ (ImageTools.getTotalRGB(img.getRGB(x-1, y-1)) > 3*256*initialThreshold ^ (rand.nextFloat() < noise))) {
					lastGOL[y][x] = true;
					lastImg.setRGB(x, y, img.getRGB(x-1, y-1));
				}
				else {
					lastGOL[y][x] = false;
					if(!preserveBackground)
						lastImg.setRGB(x, y, 0x00000000);
				}
			}
		}
	}

	private class Add {
		int x;
		int y;
		int color;

		public  Add(int x, int y, int color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}

	private class Remove {
		int x;
		int y;

		public  Remove(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public String getCategory() {
		return "Cell automation";
	}
}
