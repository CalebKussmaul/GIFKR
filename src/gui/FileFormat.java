package gui;

import java.util.ArrayList;
import java.util.List;

public enum FileFormat {PNG, JPEG, BMP, GIF, PNGS, ANIMATEDGIF, MP4;

	public String getExtension() {
		switch (this) {
		case PNG: return "png";
		case JPEG: return "jpg";
		case BMP: return "bmp";
		case GIF:
		case ANIMATEDGIF: return "gif";
		case PNGS: return "sequence";
		case MP4: return "mp4";
		}

		return "";
	}

	public String getDescription() {
		switch (this) {
		case PNG: return "PNG";
		case JPEG: return "JPEG";
		case BMP: return "Bitmap";
		case GIF: return "GIF";
		case ANIMATEDGIF: return "Animated GIF";
		case PNGS: return "PNG sequence";
		case MP4: return "MP4";
		}

		return "";
	}
	
	public boolean isAnimatedFormat() {
		switch (this) {
		case PNG:
		case JPEG:
		case GIF:
		case BMP: return false;
		case ANIMATEDGIF:
		case PNGS:
		case MP4: return true;
		}
		return false;
	}
	
	public static FileFormat[] getAnimatedFormats() {
		List<FileFormat> animatedFormats = new ArrayList<>();
		for(FileFormat f : FileFormat.values())
			if(f.isAnimatedFormat())
				animatedFormats.add(f);
		return animatedFormats.toArray(new FileFormat[animatedFormats.size()]);
	}
	
	public static FileFormat[] getImageFormats() {
		List<FileFormat> animatedFormats = new ArrayList<>();
		for(FileFormat f : FileFormat.values())
			if(!f.isAnimatedFormat())
				animatedFormats.add(f);
		return animatedFormats.toArray(new FileFormat[animatedFormats.size()]);
	}
}
