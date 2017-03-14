package filter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ControlOverride {
	
	/**
	 * Determines whether variable can be keyframed or should only show static controls even in animation mode
	 */
	public enum ControlType {KEYFRAME, STATIC} 
	public static String NO_OVERRIDE = "NO_OVERRIDE";
	
	ControlType animationControl() default ControlType.KEYFRAME;
	
	/**
	 * 
	 * @return String representing Integer max value for double and int variables
	 */
	String max() default NO_OVERRIDE;
	
	/**
	 * 
	 * @return String representing Integer min value for double and int variables
	 */
	String min() default NO_OVERRIDE;
}
