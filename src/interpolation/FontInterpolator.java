package interpolation;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

public class FontInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -2141463220530079347L;
	
	private JComboBox<String> box;
	public FontInterpolator(Font selectedFont, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		String[] fontNames = new String[fonts.length];
		for(int i = 0; i < fonts.length; i++)
			fontNames[i] = fonts[i].getFontName();
		
		box = new JComboBox<String>(fontNames) {

			private static final long serialVersionUID = 2454221612986775298L;

			@Override 
			public Dimension getPreferredSize() {
				Dimension ps = super.getPreferredSize();
				return new Dimension(Math.min(ps.width, 120), ps.height);
			}
		};
		
		box.setSelectedItem(selectedFont.getFontName());
		box.addActionListener(ae -> {
			fireChangeEvent();
		});
	}
	
	@Override
	public Dimension getGraphSize() {
		return null;
	}

	@Override
	public boolean isKeyframable() {
		return false;
	}

	@Override
	public void paintGraph(Graphics2D g, int width, int height) {
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
	}

	@Override
	public Object getAnimationValue(float time) {
		return null;
	}

	@Override
	public Object getStaticValue() {
		return new Font((String) box.getSelectedItem(), Font.PLAIN, 12);
	}

	@Override
	public String getInstructions() {
		return null;
	}

	@Override
	public JComponent getManualController() {
		return box;
	}

}
