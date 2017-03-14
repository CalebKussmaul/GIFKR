package kussmaulUtils;

public enum Program{BOOTGIF, KONSLER, HUSKY, LABCONVERT, LABCREATE, GLYPHGROUNDER, GIFKR, GIFKR2, FOLDERCLEANER;

	public String getName() {
		switch(this) {
		case BOOTGIF: 		return "BootGIF";
		case KONSLER: 		return "Konsler";
		case HUSKY: 		return "Husky";
		case LABCONVERT: 	return "Lab_Converter";
		case LABCREATE: 	return "Lab_Creator";
		case GLYPHGROUNDER: return "Glyphgrounder";
		case GIFKR: 		return "GIFKR";
		case GIFKR2: 		return "GIFKR2";
		case FOLDERCLEANER: return "Folder_Cleaner";
		default: 			return "";
		}
	}
}
