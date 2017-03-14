package image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeListener;

import gui.FieldControl;
import gui.FieldControlsPanel;
import image.at.dhyan.open_imaging.GifDecoder;
import interpolation.IntInterpolator;

public abstract class ImageSequence {

	protected String name;

	protected int width;
	protected int height;
	protected int frames;

	public int frame;

	private FieldControlsPanel fcp;

	public ImageSequence() {
		name = "glitch";
	}

	public ImageSequence(String name) {
		this.name = name;
	}

	public abstract BufferedImage getFrame(int gifFrame);


	public BufferedImage getFrame() {
		return getFrame(Math.min(getFrameCount(), frame));
	}

	public static ImageSequence read(File f) throws IOException {

		String name = f.getName();

		if(name.contains("."))
			name = name.substring(0, name.lastIndexOf('.'));

		if(f.isDirectory()) {
			File[] imgFiles = f.listFiles();

			Arrays.sort(imgFiles, (a, b) -> {
				String s0 = ((File) a).getName();
				if(s0.contains("."))
					s0=s0.substring(0, s0.lastIndexOf('.'));
				String s1 = ((File) b).getName();
				if(s1.contains("."))
					s1=s1.substring(0, s1.lastIndexOf('.'));
				try {
					return new Integer(Integer.parseInt(s0)).compareTo(Integer.parseInt(s1));
				}
				catch(Exception e) {
					return s0.compareTo(s1);
				}
			});
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();

			for(int i = 0; i <imgFiles.length; i++) {
				try {
					imgs.add(ImageIO.read(new File(f+"/"+i+".png")));
				}
				catch (Exception e) {}
			}

			if(imgs.size() == 0)
				throw new IOException();

			return new BufferedImageSequence(name, imgs.toArray(new BufferedImage[imgs.size()]));
		}
		try {
			return new GifImageSequence(name, GifDecoder.read(new FileInputStream(f)));
		} catch (IOException e0) {
			BufferedImage img = ImageIO.read(f);
			if (img != null)
				return new BufferedImageSequence(name, new BufferedImage[]{img});
		}

		return new VidImageSequence(f);
	}

	public String getName() {
		return name;
	}

	public int getFrameCount() {
		return frames;
	}

	public int getFrameNumber() {
		return frame;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public FieldControlsPanel getFrameControl(ChangeListener listener) {
		ArrayList<FieldControl> fieldControls = new ArrayList<>();
		if(getFrameCount() > 1) {
			try {

				ChangeListener l2 = ce -> {
					if(frame > frames)
						frame = 0;
					listener.stateChanged(ce);
				};

				FieldControl fc = new FieldControl(ImageSequence.class.getDeclaredField("frame"), this, l2);
				IntInterpolator interp = (IntInterpolator) fc.getInterpolator();
				interp.setMax(frames-1);
				fc.allowAnimationControls(true);
				fc.setAnimationLock(true);
				fieldControls.add(fc);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fcp = new FieldControlsPanel(fieldControls, "", false);
		fcp.setBorder(BorderFactory.createEmptyBorder());
		fcp.setVisible(getFrameCount() > 1);
		return fcp;
	}

	public FieldControlsPanel getFrameControl() {
		return fcp;
	}

	//	public ImageSequence copy() {
	//		if(img == null) {
	//			return new ImageSequence(new GifImage(gif));
	//		}
	//		return this;
	//	}
}
