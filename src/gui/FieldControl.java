package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import filter.base.ControlOverride;
import filter.base.ControlOverride.ControlType;
import interpolation.*;
import interpolation.Interpolator.GetSet;
import kussmaulUtils.StringUtil;

public class FieldControl extends JPanel {

	private static final long serialVersionUID = -7830243454843123445L;
	
	private Field field;
	private Object obj;
	private ChangeListener listener; //refreshes display
	
	private boolean animationMode = false;
	private float time = 0f;
	
	private JCheckBox keyframeBox;
	private Interpolator interp;

	public FieldControl(Field field, Object obj, ChangeListener listener) throws Exception {
		this.field		= field;
		this.obj		= obj;
		this.listener	= listener;
		
		this.setOpaque(false);
		
		this.interp = createInterpolator();
		initializeComponents();
	}

	private Interpolator createInterpolator() throws Exception {
		Class<?> c = field.getType();
		
		GetSet gs = new GetSet() {

			@Override
			public boolean isAnimationMode() {
				return animationMode && keyframeBox.isSelected();
			}

			@Override
			public float getTime() {
				return time;
			}

			@Override
			public void set(Object o) {
				try {
					field.set(obj, o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		};
		if(c.isAssignableFrom(Boolean.TYPE))
			return new BoolInterpolator(((Boolean) field.get(obj)) ? .75f : .25f, gs, listener);
		if(c.isAssignableFrom(Color.class))
			return new ColorInterpolator((Color) field.get(obj), Color.BLACK, gs, listener);
		if(c.isAssignableFrom(Double.TYPE)) {
			double val = (Double) field.get(obj);
			double min = ((overrideMin() == null) ? (val < 0 ? Integer.MIN_VALUE : 0) : overrideMin());
			double max = ((overrideMax() == null) ? Integer.MAX_VALUE : overrideMax());
			return new DoubleInterpolator(val, min, max, (val < 0 || val > 10) ? val-10 : 0, max <= 1000 ? max : val+10, gs, listener);
		}
		if(c.isEnum())
			return new EnumInterpolator(field.getType(), field.get(obj).toString(), gs, listener);
		if(c.isAssignableFrom(Float.TYPE))
			return new FloatInterpolator((Float) field.get(obj), gs, listener);
		if(c.isAssignableFrom(Font.class))
			return new FontInterpolator((Font) field.get(obj), gs, listener);
		if(c.isAssignableFrom(Integer.TYPE)) {
			int val = (Integer) field.get(obj);
			int min = (int) ((overrideMin() == null) ? (val < 0 ? Integer.MIN_VALUE : 0) : overrideMin());
			int max = (int) ((overrideMax() == null) ? Integer.MAX_VALUE : overrideMax());
			return new IntInterpolator(val, min, max, (val < 0 || val > 10) ? val-10 : 0, max <= 1000 ? max : val+10, gs, listener);
		}
		if(c.isAssignableFrom(String.class))
			return new StringInterpolator((String) field.get(obj),(String) field.get(obj), gs, listener);
		if(JComponent.class.isAssignableFrom(c))
			return new ComponentInterpolator((JComponent) field.get(obj), gs, listener);
		if(float[][].class.isAssignableFrom(c))
			return new MatrixInterpolator((float[][]) field.get(obj), gs, listener);
		
		throw new Exception("No interpolator for object type: "+c.getName());
	}
	
	private void initializeComponents() {
		
		keyframeBox = new LockCheckbox();
		keyframeBox.addActionListener(ae -> refreshUI());
		
		setLayout(new BorderLayout());
		
		refreshUI();
	}
	
	public JLabel getLabel() {
		return new JLabel(StringUtil.deCamelCap(field.getName()));
	}
	
	public void setX(float x) {
		this.time = x;
		interp.refreshValue();
	}
	
	public void allowAnimationControls(boolean show) {
		this.animationMode = show;
		refreshUI();
	}
	
	public void setAnimationLock(boolean open) {
		keyframeBox.setSelected(open);
		refreshUI();
	}
	
	private boolean overrideIsKeyframable() {
		ControlOverride o = field.getAnnotation(ControlOverride.class);
		if(o != null && o.animationControl().equals(ControlType.STATIC))
			return false;
		return true;
	}
	
	private Double overrideMax() {
		ControlOverride o = field.getAnnotation(ControlOverride.class);
		if(o != null && !o.max().equals(ControlOverride.NO_OVERRIDE))
			try {
				return Double.parseDouble(o.max());
			} catch (Exception e) {
			}
		return null;
	}
	
	private Double overrideMin() {
		ControlOverride o = field.getAnnotation(ControlOverride.class);
		if(o != null && !o.min().equals(ControlOverride.NO_OVERRIDE))
			try {
				return Double.parseDouble(o.min());
			} catch (Exception e) {
			}
		return null;
	}
	
	public Interpolator getInterpolator() {
		return interp;
	}
	
	public void refreshUI() {
		
		this.removeAll();
		if(animationMode && interp.isKeyframable() && overrideIsKeyframable()) {
			add(keyframeBox, BorderLayout.WEST);
			if(keyframeBox.isSelected()) {
				add(interp.getAnimationButton(), BorderLayout.CENTER); 
			}
			else {
				interp.exitAnimationMode();
				add(interp.getManualController(), BorderLayout.CENTER);
			}
		}
		else {
			interp.exitAnimationMode();
			add(interp.getManualController(), BorderLayout.CENTER);
		}
		
		if(animationMode)
			interp.enterAnimationMode();
		else
			interp.exitAnimationMode();
		
		revalidate();
	}
}
