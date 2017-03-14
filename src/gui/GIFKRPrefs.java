package gui;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class GIFKRPrefs {

	private static Preferences p = Preferences.userNodeForPackage(GIFKR.class);
	
	private static String KEY_CREDITS_FRAME = "KEY_CREDITS_FRAME";
	
	
	public static boolean showCreditsFrame() {
		return p.getBoolean(KEY_CREDITS_FRAME, true);
	}
	
	public static void setShowCreditsFrame(boolean value) {
		p.putBoolean(KEY_CREDITS_FRAME, value);
		try {
			p.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
