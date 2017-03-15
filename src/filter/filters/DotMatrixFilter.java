package filter.filters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import filter.base.ImageFilter;
import kussmaulUtils.ImageTools;

enum ColorMode {ADDITIVE, SUBTRACTIVE, BLACK, WHITE};

public class DotMatrixFilter extends ImageFilter {

	public ColorMode mode = ColorMode.ADDITIVE;
	public Color background = Color.black;
	//public float scale = .8f;
	public int rows = 50;

	@Override
	protected boolean randomControls() {
		return false;
	}
	
	@Override
	public BufferedImage apply(BufferedImage img) {
		
		//float realScale = .5f + scale/2f;

		//BufferedImage sample = ImageTools.scale(img, 1-realScale, true);
		BufferedImage sample = ImageTools.scaleToHeight(img, rows, true);

		float scalex = img.getWidth()/(float) sample.getWidth();
		float scaley = img.getHeight()/(float) sample.getHeight();

		Graphics2D g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(background);
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		//g.drawImage(sample, 0, 0, img.getWidth(), img.getHeight(), null);

		for(int y = 0; y < sample.getHeight(); y++) {
			for(int x = 0; x < sample.getWidth(); x++) {

				int rgb = sample.getRGB(x, y);

				float radScale = Math.min(scalex, scaley);
				
				if(mode.equals(ColorMode.WHITE)) {
					g2.setColor(Color.WHITE);
					float radius = radScale * (ImageTools.getTotalRGB(rgb) / (3f * 255f));
					g2.fill(new Ellipse2D.Float(x * scalex-radius/2+scalex/2, y * scaley-radius/2+scaley/2, radius, radius));
				}
				else if (mode.equals(ColorMode.BLACK)) {
					g2.setColor(Color.BLACK);
					float radius = radScale * (1-(ImageTools.getTotalRGB(rgb) / (3f * 255f)));
					g2.fill(new Ellipse2D.Float(x * scalex-radius/2+scalex/2, y * scaley-radius/2+scaley/2, radius, radius));
				}
				else {
					int r = ImageTools.getRed(rgb), g = ImageTools.getGreen(rgb), b = ImageTools.getBlue(rgb);

					float bigR, midR, smallR;
					Color bigCircle, midCircle, smallCircle = Color.WHITE;

					if(r > g) {
						if(r > b) { //R-G-B or R-B-G
							bigCircle = Color.RED;
							bigR = radScale * (r / 255f);

							if(g > b) { //R-G-B
								midCircle = Color.YELLOW;
								midR = radScale * (g / 255f);

								smallR = radScale * (b / 255f);
							}
							else { //R-B-G
								midCircle = Color.MAGENTA;
								midR = radScale * (b / 255f);

								smallR = radScale * (g / 255f);
							}
						}
						else { //B-R-G
							bigCircle = Color.BLUE;
							bigR = radScale * (b / 255f);

							midCircle = Color.MAGENTA;
							midR = radScale * (r / 255f);

							smallR = radScale * (g / 255f);
						}
					}

					else {
						if(r > b) { //G-R-B
							bigCircle = Color.GREEN;
							bigR = radScale * (g / 255f);

							midCircle = Color.YELLOW;
							midR = radScale * (r / 255f);

							smallR = radScale * (b / 255f);

						}
						else { //B-G-R or //G-B-R

							midCircle = Color.CYAN;

							smallR = radScale * (r / 255f);

							if(g > b) { //G-B-R
								bigCircle = Color.GREEN;
								bigR = radScale * (g / 255f);

								midR = radScale * (b / 255f);
							}
							else { //B-G-R
								bigCircle = Color.BLUE;
								bigR = radScale * (b / 255f);

								midR = radScale * (g / 255f);
							}
						}
					}
					if(mode.equals(ColorMode.SUBTRACTIVE)) {
						float rSwap = smallR;
						smallR = scalex-bigR;
						midR = scalex-midR;
						bigR = scalex-rSwap;

						Color cSwap = midCircle;
						midCircle = bigCircle;
						bigCircle = cSwap;
						//						
						//						bigCircle = bigCircle.equals(Color.RED) ? Color.CYAN : (bigCircle.equals(Color.GREEN) ? Color.MAGENTA : Color.YELLOW);
						//						midCircle = midCircle.equals(Color.CYAN) ? Color.RED : (bigCircle.equals(Color.MAGENTA) ? Color.GREEN : Color.BLUE);
						smallCircle = Color.black;
					}
					g2.setColor(bigCircle);
					g2.fill(new Ellipse2D.Float(x * scalex-bigR/2+scalex/2, y * scaley-bigR/2+scaley/2, bigR, bigR));
					g2.setColor(midCircle);
					g2.fill(new Ellipse2D.Float(x * scalex-midR/2+scalex/2, y * scaley-midR/2+scaley/2, midR, midR));
					g2.setColor(smallCircle);
					g2.fill(new Ellipse2D.Float(x * scalex-smallR/2+scalex/2, y * scaley-smallR/2+scaley/2, smallR, smallR));

				}
			}
		}

		g2.dispose();
		return img;
	}
}
