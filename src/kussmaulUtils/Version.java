package kussmaulUtils;

public enum Version{OSX, WIN, JAR;
	
	public String getName() {
		switch(this) {
		case OSX: return "_Mac"; //in order to still support legacy programs
		case WIN: return "_Win";
		default : return "";
		}
	}
}