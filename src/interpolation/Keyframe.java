package interpolation;

import java.io.Serializable;
import java.util.ArrayList;

public class Keyframe<T extends Serializable> implements Comparable<Keyframe<T>>, Serializable {

	private static final long serialVersionUID = -8804206336769590735L;
	private float time;
	private T value;

	public Keyframe() {
		this.time = -1f;
		this.value = null;
	}
	
	public Keyframe(float time, T value) {
		this.time = time;
		this.value = value;
	}

	@Override
	public int compareTo(Keyframe<T> t2) {
		return Float.compare(time, t2.time);
	}

	public float getTime() {
		return time;
	}

	public T getValue() {
		return value;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	public Keyframe<T> deepCopy() {
		return new Keyframe<T>(time, value);
	}
	
	public static ArrayList<Keyframe<Double>> floatToDouble(ArrayList<Keyframe<Float>> keys) {
		ArrayList<Keyframe<Double>> dKeys = new ArrayList<>();
		
		for(Keyframe<Float> k : keys)
			dKeys.add(new Keyframe<Double>(k.getTime(), k.getValue().doubleValue()));
		
		return dKeys;
	}
	
	public static ArrayList<Keyframe<Double>> intToDouble(ArrayList<Keyframe<Integer>> keys) {
		ArrayList<Keyframe<Double>> dKeys = new ArrayList<>();
		
		for(Keyframe<Integer> k : keys)
			dKeys.add(new Keyframe<Double>(k.getTime(), k.getValue().doubleValue()));
		
		return dKeys;
	}
	
	public static ArrayList<Keyframe<Integer>> doubleToInt(ArrayList<Keyframe<Double>> keys) {
		ArrayList<Keyframe<Integer>> dKeys = new ArrayList<>();
		
		for(Keyframe<Double> k : keys)
			dKeys.add(new Keyframe<Integer>(k.getTime(), k.getValue().intValue()));
		
		return dKeys;
	}
	
	public static ArrayList<Keyframe<Float>> doubleToFloat(ArrayList<Keyframe<Double>> keys) {
		ArrayList<Keyframe<Float>> dKeys = new ArrayList<>();
		
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
		for(Keyframe<Double> k : keys) {
			if(k.getValue() < min)
				min = k.getValue();
			if(k.getValue() > max)
				max = k.getValue();
		}
		
		if(min < 0) {
			max -= min;
			for(Keyframe<Double> k : keys)
				k.setValue(k.getValue()-min);
		}
		for(Keyframe<Double> k : keys)
			k.setValue(k.getValue()/max);
		
		for(Keyframe<Double> k : keys)
			dKeys.add(new Keyframe<Float>(k.getTime(), k.getValue().floatValue()));
		
		return dKeys;
	}
	
	public static ArrayList<Keyframe<Float>> intToFloat(ArrayList<Keyframe<Integer>> keys) {
		ArrayList<Keyframe<Double>> dKeys = new ArrayList<>();
		
		for(Keyframe<Integer> k : keys)
			dKeys.add(new Keyframe<Double>(k.getTime(), k.getValue().doubleValue()));
		
		return doubleToFloat(dKeys);
	}
	
	public static ArrayList<Keyframe<?>> deepCopy(ArrayList<? extends Keyframe<?>> keys) {
		ArrayList<Keyframe<?>> list = new ArrayList<>();
		
		for(Keyframe<?> k : keys) {
			list.add(k.deepCopy());
		}
		
		return list;
	}
 }
