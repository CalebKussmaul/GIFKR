package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import org.bytedeco.javacv.Java2DFrameConverter;

public class VidImageSequence extends ImageSequence {

	FFmpegFrameGrabber g;

	public VidImageSequence(File f) throws IOException {
		
		super(f.getName());
		
		try {
			g = new FFmpegFrameGrabber(f);
			g.start();
			width	= g.getImageWidth();
			height	= g.getImageHeight();
			frames	= g.getLengthInFrames();
			
		} catch (Exception e) {
			throw new IOException();
		}
	}

	@Override
	public BufferedImage getFrame(int gifFrame) {
		
		BufferedImage frame = null;
		
		try {
			g.setFrameNumber(gifFrame+1);
			frame = new Java2DFrameConverter().convert(g.grabImage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frame == null? new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR) : frame;
	}
}
