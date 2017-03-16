import java.awt.Color;

public class TestMain {

	public static void main(String[] args) {


		int c = new Color(8, 8, 8, 255).getRGB();
		
		System.out.println(Integer.toHexString(c >>> 8));
		System.out.println((c >> 8) & 255);
		System.out.println(c >>> 8);
		System.out.println(new Color(c).getAlpha());
	}
}
