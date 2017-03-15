package filter.filters;

import java.awt.Color;

import filter.base.TextFilter;
import kussmaulUtils.ImageTools;

public class TheMatrixFilter extends TextFilter {

	enum CharSet{ALPHABETIC, ALPHANUMERIC, BINARY, DECIMAL, HEXADECIMAL;
		public char[] getChars() {
			switch(this) {
			case ALPHABETIC: 	return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
			case ALPHANUMERIC:	return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
			case BINARY:		return "01".toCharArray();
			case DECIMAL:		return "0123456789".toCharArray();
			case HEXADECIMAL:	return "ABCDEF0123456789".toCharArray();
			default:			return null;
			}
		}
	}

	enum ColorScale{NORMAL, GRAYSCALE, REDSCALE, GREENSCALE, BLUESCALE;

		public Color getColor(int c) {
			int col3 = ImageTools.getTotalRGB(c)/3;

			switch(this) {		
			case BLUESCALE: return new Color(0, 0, col3, ImageTools.getAlpha(c));
			case GRAYSCALE:	return new Color(col3, col3, col3, ImageTools.getAlpha(c));
			case GREENSCALE:return new Color(0, col3, 0, ImageTools.getAlpha(c));
			case NORMAL:	return new Color(c);
			case REDSCALE:	return new Color(col3, 0, 0, ImageTools.getAlpha(c));
			default:		return null;
			}
		}
	}

	public CharSet characterSet = CharSet.HEXADECIMAL;
	public ColorScale pallet = ColorScale.GREENSCALE;

	private char[] chars;


	@Override
	protected void beforeFilter() {
		chars = characterSet.getChars();
	}

	@Override
	public char getChar(int color, int count) {
		return chars[rand.nextInt(chars.length)];
	}

	@Override
	public Color getColor(int color, int count) {
		return pallet.getColor(color);
	}

}
