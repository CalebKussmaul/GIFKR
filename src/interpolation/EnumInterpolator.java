package interpolation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import utils.StringUtil;

public class EnumInterpolator extends GraphInterpolator {

	private static final long serialVersionUID = -2141463220530079347L;
	
	private JComboBox<String> box = new JComboBox<String>();
	private HashMap<String, Object> map = new HashMap<String, Object>();
	
	public EnumInterpolator(Class<?> type, String selectedVal, GetSet gs, ChangeListener... listeners) {
		super(gs, listeners);
		
		for(Object constant :type.getEnumConstants()) {
			String name = StringUtil.deCap(constant.toString());
			box.addItem(name);
			map.put(name, constant);
		}
		box.setSelectedItem(StringUtil.deCap(selectedVal));
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
		return map.get(box.getSelectedItem());
	}

	@Override
	public String getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent getManualController() {
		return box;
	}

}
