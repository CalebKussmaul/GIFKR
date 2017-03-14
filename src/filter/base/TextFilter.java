package filter.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import utils.ImageTools;


enum Sampling{BICUBIC, POINT}

public abstract class TextFilter extends ImageFilter {

	public Font font				= new Font(Font.MONOSPACED, Font.PLAIN, 12);
	public Sampling imageSampling	= Sampling.POINT;
	public Color backgroundColor	= Color.black;
	public double fontSize			= 12d;
	public double widthSpacing		= 1d;
	public double heightSpacing		= 1d;
	public boolean startOnNewLine	= false;
	public boolean antialiasing		= true;
	public boolean autoFitText		= true;
	
	protected StringBuilder sb;
	public JButton copyText;
	
	public TextFilter() {
		 copyText = new JButton("Copy text");
		 copyText.addActionListener(ae -> {
			 StringSelection stringSelection = new StringSelection(sb.toString());
			 Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			 clpbrd.setContents(stringSelection, null);
		 });
	}


	@Override
	protected final BufferedImage apply(BufferedImage img) {

		sb = new StringBuilder();
		
		widthSpacing		= Math.max(widthSpacing, .1);
		heightSpacing		= Math.max(heightSpacing, .1);

		BufferedImage img2	= new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g		= img2.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antialiasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		double fontS = Math.max(2, fontSize);

		font				= font.deriveFont((float) fontS);
		
		g.setFont(font.deriveFont((float)((int) fontS)));
		FontMetrics met0	= g.getFontMetrics();
		g.setFont(font.deriveFont((float)((int) fontS + 1)));
		FontMetrics met1	= g.getFontMetrics();
		double fPart 		= fontS - (int) fontS;
		double charWidth 	= widthSpacing * ((met0.getMaxAdvance() * (1-fPart)) + (met1.getMaxAdvance() * fPart));
		double charHeight	= heightSpacing * ((met0.getHeight() * (1-fPart)) + (met1.getHeight() * fPart));
		int count = 0;

		g.setFont(font);
		
		if(imageSampling == Sampling.BICUBIC) {
			BufferedImage sample = ImageTools.fitBoth(img, (int)(img.getWidth()/charWidth), (int)(img.getHeight()/charHeight), false);

			if(autoFitText) {
				charWidth *= img.getWidth()/(double)(charWidth*sample.getWidth());
				charHeight *= img.getHeight()/(double)(charHeight*sample.getHeight());
			}

			for(int y = 0; y <= sample.getHeight() + (autoFitText ? -1 : 0); y++) {
				for(int x = 0; x <= sample.getWidth()+ (autoFitText ? -1 : 0); x++, count++) {

					int rgb = sample.getRGB(Math.min(x, sample.getWidth()-1), Math.min(y, sample.getHeight()-1));
					g.setColor(getColor(rgb, count));

					char c = getChar(rgb, count);
					sb.append(c);
					g.drawString(Character.toString(c), Math.round(x*charWidth), Math.round((y+1)*charHeight)-g.getFontMetrics().getDescent());	
				}
				sb.append('\n');
				if (startOnNewLine)
					count = 0;
			}
		} else if(imageSampling == Sampling.POINT) {
			for(int y = 0; y < img.getHeight(); ) {
				for(int x = 0; x < img.getWidth(); count++) {
					int rgb = img.getRGB(x, y);
					String c = Character.toString(getChar(rgb, count));
					sb.append(c);
					g.setColor(getColor(rgb, count));
					g.drawString(c, x, Math.round(y+charHeight));
					x += g.getFontMetrics().stringWidth(c)*widthSpacing;
				}
				y += charHeight;
				sb.append('\n');
				if (startOnNewLine)
					count = 0;
			}
		}

		g.dispose();
		sb.toString();
		
		return img2;
	}

	public abstract char getChar(int color, int count);

	public abstract Color getColor(int color, int count);

	@Override
	public String getCategory() {
		return "Text";
	}
}
