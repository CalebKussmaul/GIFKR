package interpolation;

import filter.base.ImageFilter;
import filter.filters.AddFilter;

public class TempMain {
	public static void main(String[] args){
		System.out.println(AddFilter.class.isAssignableFrom(ImageFilter.class));
		System.out.println(ImageFilter.class.isAssignableFrom(AddFilter.class));
	}

}
