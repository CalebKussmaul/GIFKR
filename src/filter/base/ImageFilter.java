package filter.base;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import filter.base.ControlOverride.ControlType;
import gui.FieldControl;
import gui.FieldControlsPanel;
import kussmaulUtils.Refreshable;
import kussmaulUtils.ImageTools;
import kussmaulUtils.StringUtil;

public abstract class ImageFilter implements Comparable<ImageFilter> {

	@ControlOverride(max = "360")
	public double angle = 0;
	@ControlOverride(animationControl = ControlType.STATIC)
	public static boolean rotateCorrection;
	@ControlOverride(animationControl = ControlType.STATIC)
	public boolean randomizeSeed;
	@ControlOverride(animationControl = ControlType.STATIC)
	public int randomSeed = 8675309;

	protected final Random rand;
	
	protected List<Field> hiddenFields;

	private FieldControlsPanel p;

	public ImageFilter() {
		rand = new Random(randomSeed);
		hiddenFields = new ArrayList<>();
		
		if(!randomControls())
			hideRandom();
		if(!angleControls())
			hideAngle();
	}

	protected boolean randomControls() {
		return true;
	}
	
	protected boolean angleControls() {
		return true;
	}
	
	protected abstract BufferedImage apply(BufferedImage img);
	public String getCategory() {
		return "General";
	}

	protected void beforeFilter() {

	}

	protected void afterFilter() {
	}

	public final BufferedImage getFilteredImage(BufferedImage img) {
		
		randomSeed = randomizeSeed ? (int) (Math.random() * Integer.MAX_VALUE) : randomSeed;
		rand.setSeed(randomSeed);

		beforeFilter();

		BufferedImage filtered = apply(ImageTools.rotate(img, angle, false));
		img = ImageTools.unrotate(filtered, angle, img.getWidth(), img.getHeight(), rotateCorrection, false);

		afterFilter();
		
		return img;
	}
	
	public final FieldControlsPanel getSettingsPanel() {
		return p;
	}

	public final FieldControlsPanel getSettingsPanel(Refreshable r, boolean channel) {

		if(p != null)
			return p;
		
		List<Field> fields = getPublicFields(new ArrayList<Field>(), getClass());
		ArrayList<FieldControl> controlComponents = new ArrayList<FieldControl>();

		for(Field f : fields) {
			try {
				controlComponents.add(new FieldControl(f, this, ce -> {r.refresh();}));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("No control for "+f.getType().getName() +".class in "+this.getClass().getSimpleName()+".class");
			}
		}

		p = new FieldControlsPanel(controlComponents, StringUtil.deCamel(this.getClass().getSimpleName())+" settings", channel);
		
		return p;
	}

	@Override
	public String toString() {
		return (getCategory() == null ? "" : getCategory()+" - ") + StringUtil.deCamel(getClass().getSimpleName().replaceAll("(\\s?Filter$)", ""));
	}

	@Override
	public final int compareTo(ImageFilter o) {
		if(getCategory() == null ^ o.getCategory() == null)
			return getCategory() == null ? -1 : 1;

		return toString().compareTo(o.toString());
	}

	protected final void hideRandom() {
		try {
			hide(this.getClass().getField("randomizeSeed"));
			hide(this.getClass().getField("randomSeed"));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	protected final void hideAngle() {
		try {
			hide(this.getClass().getField("angle"));
			hide(this.getClass().getField("rotateCorrection"));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	protected final void hide(Field f) {
		hiddenFields.add(f);
	}
	
	private List<Field> getPublicFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		for(int i = 0; i < fields.size(); i++) {	
			int mod = fields.get(i).getModifiers();
			if(!Modifier.isPublic(mod) || Modifier.isFinal(mod) || hiddenFields.contains(fields.get(i))) {
				fields.remove(i);
				i--;
			}
		}
		if (type.getSuperclass() != null) {
			fields = getPublicFields(fields, type.getSuperclass());
		}
		return fields;
	}
	
	@Override
	public final boolean equals(Object o) {
		return this.getClass() == o.getClass();
	}
	
	public void setX(float x) {
		if(p != null)
			p.setX(x);
	}
}
