package kussmaulUtils;

import java.io.File;

public class StringUtil {

    public static String incrementFileName(String originalName) {

        int extensionStart;

        if(originalName.length() > 1)
            extensionStart = originalName.substring(1).indexOf('.'); //first char '.' is not extension
        else
            extensionStart = -1;

        String name = (extensionStart != -1) ? originalName.substring(0, extensionStart+1) : originalName;
        String ext = (extensionStart != -1) ? originalName.substring(extensionStart+1) : "";
        int number = 1;

        if(name.endsWith(")")) {
            int i = name.length()-2; //find old file number
            while(i > 0 && Character.isDigit(name.charAt(i)))
                i--;

            if(name.charAt(i) == '(') {
                try {
                    number = Integer.parseInt(name.substring(i + 1, name.length() - 1));
                } catch (Exception e) {}//Nothing to do here, number already set to 1 as it should be
            }
        }

        if(name.endsWith("("+number+")")) //remove old number
            name = name.substring(0, name.length()-("("+number+")").length());

        if(!name.endsWith(" ")) //add space if missing
            name+=" ";

        return name+"("+(number+1)+")"+ext;
    }

	public static File resolveConflictName(File dir, String name, boolean ignoreExtension) {

		if(dir == null)
			dir = new File(System.getProperty("user.home"));
		
		if(!dir.exists() || !dir.isDirectory())
			dir = dir.getParentFile();
		
		final String[] finalName = new String[]{name};
		
		while(dir.listFiles((folder, fileName) -> {
			if(ignoreExtension)
				return removeExtension(fileName.toLowerCase()).equals(removeExtension(finalName[0].toLowerCase()));
			return fileName.toLowerCase().equals(finalName[0].toLowerCase());
				
			}).length > 0) {
			finalName[0] = incrementFileName(finalName[0]);
		}
		return new File(dir +"/"+finalName[0]);

	}

	public static String removeExtension(String path) {
		int nameStart = path.lastIndexOf('/')+1; //if no '/' start at 0, else start at index of '/' +1
		int extensionStart = nameStart + 1 + path.substring(nameStart+1).indexOf('.');
		return (extensionStart != nameStart) ? path.substring(0, extensionStart) : path;
	}

	public static String getFullExtension(String path) {

		if(path.length() <= 1 || path.endsWith("/"))
			return "";

		String fileName = path.substring(path.lastIndexOf('/')+1); //if no '/' start at 0, else start at index of '/' +1
		int extensionStart = fileName.substring(1).indexOf('.');
		return (extensionStart != -1) ? fileName.substring(extensionStart+1) : "";
	}

	public static String deCamel(String s) {

		if(s.length() <2)
			return s;
		
		StringBuilder sb = new StringBuilder();
		sb.append(s.charAt(0));

		for(int i = 0; i < s.length()- 2; i++) {

			boolean[] c = new boolean[] {Character.isUpperCase(s.charAt(i)), Character.isUpperCase(s.charAt(i+1)), Character.isUpperCase(s.charAt(i+2))};

			if(c[1] && !(c[0] && c[2]))
				sb.append(' ');
			sb.append((c[1] && !c[2]) ? Character.toLowerCase(s.charAt(i+1)) : s.charAt(i+1));
		}
		return sb.append(s.charAt(s.length()-1)).toString();
	}
	
	public static String deCamelCap(String s) {
		
		if(s.length() == 0)
			return s;
		
		return Character.toUpperCase(s.charAt(0)) + deCamel(s.substring(1));
	}
	
	public static String deCap(String s) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(s.charAt(0));

		for(int i = 1; i < s.length(); i++) {
			if(s.charAt(i) == '_') {
				sb.append(' ');
				if(i <s.length()-1) {
					sb.append(s.charAt(i+1));
					i++;
				}
			}
			else
				sb.append(Character.toLowerCase(s.charAt(i)));
		}
		return sb.toString();
	}
	
	public static String merge(String s0, String s1, float x) {
		int length = Math.round((1-x) * s0.length() + x * s1.length());
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < length; i++) {
			sb.append(Math.random() > x ? s0.charAt((int) ((i/(float) length) * s0.length())) : s1.charAt((int) ((i/(float) length) * s1.length())));
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(merge("test", "zvcfdsgsfdgsdf", .1f));
	}
}
