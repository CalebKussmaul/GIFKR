package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.SwingUtilities;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

import filter.base.ImageFilter;
import filter.filters.NoFilterFilter;
import image.at.dhyan.open_imaging.GifSequenceWriter;
import image.ImageSequence;
import utils.ImageTools;
import utils.StringUtil;

public class Animation {

	private ImageFilter filter;
	private int frames;

	private ImageSequence sequence;
	private long lastHash = 0l;
	private BufferedImage lastImg;

	private volatile boolean saveStopped;

	public Animation(ImageSequence sequence) {
		this.sequence = sequence;
		filter = new NoFilterFilter();
	}

	public void setFilter(ImageFilter filter) {
		this.filter = filter;
	}

	public BufferedImage renderFrame(int newW) {

		long newHash = newW * (10 + sequence.getFrameNumber());

		if(lastHash != newHash || lastImg == null) {
			lastHash = newHash;
			lastImg = ImageTools.scaleToWidth(sequence.getFrame(), newW, false);
		}

		return filter.getFilteredImage(ImageTools.deepCopy(lastImg));
	}

	public ImageSequence getSource() {
		return sequence;
	}

	public void setX(float x) {
 		if(sequence.getFrameControl() != null)
			sequence.getFrameControl().setX(x);
		filter.setX(x);
	}

	public void setFrameCount(int frames) {
		this.frames = frames;
	}

	public void setSource(ImageSequence sequence) {
		this.sequence = sequence;
	}

	public int getSourceWidth() {
		return sequence.getWidth();
	}

	public int getSourceHeight() {
		return sequence.getHeight();
	}

	public int getSourceFrameCount() {
		return sequence.getFrameCount();
	}

	public void saveGIF(File f, int width, int frameDelay, ProgressDisplay d, ActionListener onFinish) {
		saveStopped = false;
		new Thread(() -> {
			try {
				d.setProgress(0, "Starting export");
				d.setCancel(ae -> saveStopped = true);

				String name	= f.getName();
				int dotIdx	= name.lastIndexOf('.');
				if(dotIdx !=- 1)
					name = name.substring(0, dotIdx);
				String ext = ".gif";

				File out = StringUtil.resolveConflictName(f.getParentFile(), name+ext, false);

				ImageOutputStream output = new FileImageOutputStream(out);
				GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, frameDelay, true);

				for(int i = 0; i < frames && !saveStopped; i++) {
					try {
						setX(i/((float)frames-1));
						d.setProgress(i/(float)(frames-1), "Writing frame "+i+" of " + frames);
						BufferedImage frame = renderFrame(width);
						BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
						img.getGraphics().drawImage(frame, 0, 0, null);

						writer.writeToSequence(img);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				writer.close();
				output.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if(onFinish != null)
					SwingUtilities.invokeLater(() -> onFinish.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save")));
			}
		}).start();
	}

	public void saveVid(File f, int width, int fps, ProgressDisplay d, ActionListener onFinish) {
		saveStopped = false;
		new Thread(() -> {
			try {
				d.setProgress(0, "Starting export");
				d.setCancel(ae -> saveStopped = true);

				String name	= f.getName();
				int dotIdx	= name.lastIndexOf('.');
				if(dotIdx !=- 1)
					name = name.substring(0, dotIdx);
				String ext = ".mp4";

				File out = StringUtil.resolveConflictName(f.getParentFile(), name+ext, false);

				int height = (int)Math.round(width * getSourceHeight()/(float)getSourceWidth());
				height = height %2 == 0 ? height : height + 1; 
				FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(out.getAbsolutePath(), width, height);
				recorder.setFrameRate(fps);
				recorder.setFormat("mp4");
				recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
				//recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
				recorder.setVideoBitrate(recorder.getImageWidth() * recorder.getImageHeight() * fps * 10);

				recorder.setVideoQuality(.1);
				recorder.start();

				for(int i = 0; i < frames && !saveStopped; i++) {
					try {
						setX(i/(float)(frames-1));
						d.setProgress((i/(float)(frames-1))*.95f, "Writing frame "+i+" of " + frames);
						BufferedImage frame = renderFrame(width);
						BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
						img.getGraphics().drawImage(frame, 0, 0, null);
						recorder.record(new Java2DFrameConverter().convert(img), avutil.AV_PIX_FMT_ARGB);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				d.setProgress(.98, "Finishing export...");
				recorder.stop();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if(onFinish != null)
					SwingUtilities.invokeLater(() -> onFinish.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save")));
			}
		}).start();
	}

	public void saveImage(File f, String format, int width, ProgressDisplay d, ActionListener onFinish) {
		new Thread(() -> {
			try {
				d.setProgress(.5, "Rendering image");

				setX(0);
				
				String name	= f.getName();
				int dotIdx	= name.lastIndexOf('.');
				if(dotIdx !=- 1)
					name = name.substring(0, dotIdx);

				File out = StringUtil.resolveConflictName(f.getParentFile(), name+"."+format, false);
				
				
				ImageIO.write(renderFrame(width), format, out);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(onFinish != null)
					SwingUtilities.invokeLater(() -> onFinish.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save")));
			}
		}).start();
	}


	private static FilenameFilter getPNGSFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("[0-9]*\\.png");
			}	
		};
	}

	public void savePNGSequence(File f, int width, ProgressDisplay d, ActionListener onFinish) {
		saveStopped = false;
		new Thread(() -> {
			try {
				d.setProgress(0, "Starting export");
				d.setCancel(ae -> saveStopped = true);

				File out = f;
				
				if(f.exists() && !f.isDirectory()) {
					out = StringUtil.resolveConflictName(f.getParentFile(), f.getName(), false);
				}
				
				int offset = 0;
				if(out.isDirectory()) {
					offset = out.listFiles(getPNGSFilter()).length;
				}

				out.mkdirs();

				for(int i = 0; i < frames && !saveStopped; i++) {
					try {
						setX(i/(float)(frames-1));
						d.setProgress(i/(float)(frames-1), "Writing frame "+i+" of " + frames);
						ImageIO.write(renderFrame(width), "png", new File(out+"/"+(i+offset)+".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if(onFinish != null)
					SwingUtilities.invokeLater(() -> onFinish.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save")));
			}
		}).start();
	}
}
