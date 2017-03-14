package image;

public class Keyframe {

	public final int gifFrame;
	public final float filterLevel;
	public final float angleR;
	public final float angleG;
	public final float angleB;
	
	public Keyframe(int gifFrame, float filterLevel, float angle) {
		this(gifFrame, filterLevel, angle, angle, angle);
	}
	
	public Keyframe(int gifFrame, float filterLevel, float angleR, float angleG, float angleB) {
		this.gifFrame 		= gifFrame;
		this.filterLevel	= filterLevel;
		this.angleR			= angleR;
		this.angleG			= angleG;
		this.angleB			= angleB;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Keyframe) {
			Keyframe key = (Keyframe) o;
			return gifFrame == key.gifFrame && filterLevel == key.filterLevel && angleR == key.angleR && angleG == key.angleG && angleB == key.angleB;
		}
		return false;
	}
	
	@Override 
	public String toString() {
		return "GifFrame: " + gifFrame + " Filter level: " + filterLevel + " Angle R:" + angleR + " Angle G: " + angleG + " Angle B:"+ angleB;
	}
}
