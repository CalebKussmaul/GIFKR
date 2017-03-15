package kussmaulUtils;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class CalebKussmaul {
	
	private static Properties props;
	private static boolean online = refresh();

	public static boolean refresh(){
		props 							= new Properties();
		try {
			URLConnection connection	= new URL("http://kussmaul.net/projects.properties").openConnection();
			//connection.setReadTimeout(500);
			connection.setConnectTimeout(500);
			final InputStream in = connection.getInputStream();
			props.load(in);
			in.close();
			return true;
		} catch (Exception e) {
			props						= getDefaultProperties();
			return false;
		}
	}

	public static String getLatestVersion(Program p) {
		return props.getProperty(p.getName()+"_Version");
	}

	public static String getLatestDownload(Program p, Version v) {
		return props.getProperty(p.getName()+"_Download"+v.getName());
	}

	public static String getDownloadOnPlatform(Program p){
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.equals("mac os x"))
			return getLatestDownload(p, Version.OSX);
		if(osName.contains("win"))
			return getLatestDownload(p, Version.WIN);
		return getLatestDownload(p, Version.JAR);
	}

	public static int getNumOfAds(){
		return Integer.parseInt(props.getProperty("AdsOnSite"));
	}

	public static boolean enableSe7enSinsLink(){
		return props.getProperty("EnableSe7enLink").equals("true");
	}

	public static String getSe7enLink(){
		return props.getProperty("Se7enLink");
	}

	public static String getServerIP(){
		return props.getProperty("ServerIP");
	}

	public static boolean isOnline(){
		return online;
	}

	private static Properties getDefaultProperties(){
		Properties p = new Properties();
		p.put("BootGIF_Version", 			"2.3.2");
		p.put("BootGIF_Download_Mac", 		"http://www.mediafire.com/download/anczkovf4oc7zjc/BootGIF.dmg");
		p.put("BootGIF_Download_Win", 		"http://www.mediafire.com/download/70q8paa4ebi65zr/BootGIF.jar");
		p.put("BootGIF_Download_Lin", 		"http://www.mediafire.com/download/70q8paa4ebi65zr/BootGIF.jar");
		p.put("BootGIF_Download", 			"http://www.mediafire.com/download/70q8paa4ebi65zr/BootGIF.jar");

		p.put("Husky_Version", 				"1.2");
		p.put("Husky_Download_Mac", 		"http://www.mediafire.com/download/1paxrr1ujm0mo1g/Husky.jar");
		p.put("Husky_Download_Win", 		"http://www.mediafire.com/download/1paxrr1ujm0mo1g/Husky.jar");
		p.put("Husky_Download_Lin", 		"http://www.mediafire.com/download/1paxrr1ujm0mo1g/Husky.jar");
		p.put("Husky_Download",				"http://www.mediafire.com/download/1paxrr1ujm0mo1g/Husky.jar");

		p.put("Konsler_Version", 			"1.3.2");
		p.put("Konsler_Download_Mac", 		"http://www.mediafire.com/download/er4dvd8il6sflm6/KonslersCookies.dmg");
		p.put("Konsler_Download_Win", 		"http://www.mediafire.com/download/2dioev4dfu214dg/KonslersCookies.exe");
		p.put("Konsler_Download_Lin", 		"http://www.mediafire.com/download/rawwdd6h22idfsd/Konsler.jar");
		p.put("Konsler_Download", 			"http://www.mediafire.com/download/rawwdd6h22idfsd/Konsler.jar");
		
		p.put("Lab_Converter_Version", 		"1.1.2");
		p.put("Lab_Converter_Download_Mac", "http://www.mediafire.com/download/mh8xxje16e1fuo9/Labrador_Converter.dmg");
		p.put("Lab_Converter_Download_Win", "http://www.mediafire.com/download/c3aynpot8dpbkqo/LabConverter.jar");
		p.put("Lab_Converter_Download_Lin", "http://www.mediafire.com/download/c3aynpot8dpbkqo/LabConverter.jar");
		p.put("Lab_Converter_Download", 	"http://www.mediafire.com/download/c3aynpot8dpbkqo/LabConverter.jar");
		
		p.put("Glyphgrounder_Version", 		"1.0");
		p.put("Glyphgrounder_Download_Mac", "http://www.google.com");
		p.put("Glyphgrounder_Download_Win", "http://www.google.com");
		p.put("Glyphgrounder_Download_Lin", "http://www.google.com");
		p.put("Glyphgrounder_Download", 	"http://www.google.com");

		p.put("GIFKR_Version", 				"Beta 1.3.3");
		p.put("GIFKR_Download_Mac", 		"http://www.mediafire.com/download/4gc04moix0g9m14/GIFKR.dmg");
		p.put("GIFKR_Download_Win", 		"http://www.mediafire.com/download/lm8jglhhjlia92r/GIFKR.jar");
		p.put("GIFKR_Download_Lin", 		"http://www.mediafire.com/download/lm8jglhhjlia92r/GIFKR.jar");
		p.put("GIFKR_Download", 			"http://www.mediafire.com/download/lm8jglhhjlia92r/GIFKR.jar");
		
		p.put("GIFKR2_Version", 			"2.0 PRE-RELEASE 3");
		p.put("GIFKR2_Download_Mac", 		"http://www.mediafire.com/file/e5jic2us2fqsra6/GIFKR_2.dmg");
		p.put("GIFKR2_Download_Win", 		"http://www.mediafire.com/file/gwauau9frirarl0/GIFKR_2.jar");
		p.put("GIFKR2_Download_Lin", 		"http://www.mediafire.com/file/gwauau9frirarl0/GIFKR_2.jar");
		p.put("GIFKR2_Download", 			"http://www.mediafire.com/file/gwauau9frirarl0/GIFKR_2.jar");
		
		p.put("Folder_Cleaner_Version", 	"1.0");
		p.put("Folder_Cleaner_Download_Mac", "http://www.google.com");
		p.put("Folder_Cleaner_Download_Win", "http://www.google.com");
		p.put("Folder_Cleaner_Download_Lin", "http://www.google.com");
		p.put("Folder_Cleaner_Download", 	"http://www.google.com");
		
		p.put("AdsOnSite", 					"0");
		p.put("EnableSe7enLink",			"false");
		p.put("Se7enLink", 					"http://www.se7ensins.com/forums/threads/use-bootgif-to-make-bootlogos-from-animated-gifs.1308401/");
		p.put("ServerIP", 					"71.192.174.127");

		return p;
	}

	public static void main(String[] args){
		Properties props = getDefaultProperties();
		try {
			
			File f = new File("projects.properties");
			props.store(new FileOutputStream(f), null);
			System.out.println(f.getAbsolutePath());
			Desktop.getDesktop().open(f.getAbsoluteFile().getParentFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
