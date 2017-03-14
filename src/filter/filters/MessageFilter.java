package filter.filters;

import java.awt.Color;

import filter.base.TextFilter;

public class MessageFilter extends TextFilter {

	@Override
	protected boolean randomControls() {
		return false;
	}
	
	public String message = "Hello, "+System.getProperty("user.name") + ". ";
	
	@Override
	public char getChar(int color, int count) {
		if(message.equals(""))
			return ' ';
		return message.charAt(count % message.length());
	}

	@Override
	public Color getColor(int color, int count) {
		return new Color(color);
	}

}
