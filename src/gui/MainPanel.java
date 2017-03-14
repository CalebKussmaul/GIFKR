package gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import filter.base.FilterLoader;
import utils.ImageTools;
import utils.Refreshable;
import image.BufferedImageSequence;
import image.ImageSequence;

public class MainPanel extends JPanel implements Refreshable, ProgressDisplay {

	private static final long serialVersionUID = -8438576029794021570L;

	private Animation animation;

	private ProgressFrame progressFrame;

	private PreviewPanel previewPanel;
	private SettingsPanel setPanel;
	private JSplitPane splitPane;

	public MainPanel() {

		animation = new Animation(new BufferedImageSequence("default", new BufferedImage[]{getSplash()}));

		initializeComponents();
		refresh();
		addActionListeners();

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	private void initializeComponents() {

		progressFrame	= new ProgressFrame();
		progressFrame.setVisible(false);

		previewPanel	= new PreviewPanel(this, animation.getSourceWidth(), animation.getSourceHeight());
		JPanel leftPanel= new JPanel(new BorderLayout());
		leftPanel.add(previewPanel, BorderLayout.CENTER);
		leftPanel.add(previewPanel.getStatusPanel(), BorderLayout.SOUTH);
		setPanel		= new SettingsPanel(this);
		splitPane		= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, setPanel);
		splitPane.setBorder(null);
		splitPane.setOpaque(false);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(1);
	}

	private void addActionListeners() {
		previewPanel.setDropTarget(new DropTarget(previewPanel, new DropTargetListener(){

			public void dragEnter(DropTargetDragEvent dtde) {}
			public void dragOver(DropTargetDragEvent dtde) {}
			public void dropActionChanged(DropTargetDragEvent dtde) {}
			public void dragExit(DropTargetEvent dte) {}
			@Override
			public void drop(DropTargetDropEvent dtde) {

				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					setImage(dtde.getTransferable());
				}
				else {
					dtde.rejectDrop();
				}
			}
		}));
	}

	private BufferedImage getSplash() {
		BufferedImage splash = ImageTools.getResourceImage("splash"+((int) (Math.random()*11))+".png");
		BufferedImage overlay = ImageTools.getResourceImage("splashoverlay.png");

		splash.getGraphics().drawImage(overlay, 0, 0, null);
		return splash;
	}

	public JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu file = new JMenu("File");

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(ae -> {

			try {		
				JFileChooser f = new JFileChooser();
				f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				if(f.showOpenDialog(MainPanel.this) != JFileChooser.APPROVE_OPTION)
					return;

				setImage(ImageSequence.read(f.getSelectedFile()));

			} catch (Exception e) {
				JOptionPane.showMessageDialog(MainPanel.this, "File was not a GIF, image, or sequence", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		file.add(open);
		bar.add(file);

		JMenu edit = new JMenu("Edit");

		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		copy.addActionListener(ae -> {
			TransferableUtils.copy(previewPanel.getLastImage());
		});
		
		JMenuItem copyFull = new JMenuItem("Copy full resolution");
		copyFull.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK|KeyEvent.SHIFT_DOWN_MASK));
		copyFull.addActionListener(ae -> { //TODO:re-implement this
			showProgress();
			setProgress(.5, "Rendering image");
			
			new Thread(()-> {
				TransferableUtils.copy(animation.renderFrame(animation.getSourceWidth()));
				hideProgress();

			}).start();
		});

		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.META_MASK));
		paste.addActionListener(ae -> {
			this.setImage(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this));
		});

		edit.add(copy);
		edit.add(copyFull);
		edit.add(paste);
		bar.add(edit);


		JMenu window = new JMenu("Window");
		boolean horizontal = splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT;
		JMenuItem changeArrangement = new JMenuItem("Change to "+(horizontal? "vertical": "horizontal")+" arrangement");
		changeArrangement.addActionListener(ae -> {
			boolean h = splitPane.getOrientation() != JSplitPane.HORIZONTAL_SPLIT;
			changeArrangement.setText("Change to "+(h ? "vertical": "horizontal")+" arrangement");
			splitPane.setOrientation(h ? JSplitPane.HORIZONTAL_SPLIT: JSplitPane.VERTICAL_SPLIT);
			//SwingUtilities.getWindowAncestor(this).pack();
			//refresh();
			splitPane.setDividerLocation(-1);
		});

		JMenuItem openBeta = new JMenuItem("Launch GIFKR beta 1.3.3");
		openBeta.addActionListener(ae -> beta.gui.GIFKR.main(new String[]{}));

		JMenuItem showCredits = new JMenuItem("About GIFKR");
		showCredits.addActionListener(ae -> new CreditsFrame().setVisible(true));

		window.add(changeArrangement);
		window.add(openBeta);
		window.add(showCredits);
		bar.add(window);

		JMenu dev = new JMenu("Development");
		JMenuItem jconsole = new JMenuItem("JConsole");
		jconsole.addActionListener(ae -> {
			try {
				Runtime.getRuntime().exec("jconsole");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		JMenuItem deleteMods = new JMenuItem("Delete external filters");
		deleteMods.addActionListener(ae -> {
			FilterLoader.deleteExternalFilters();
		});

		JMenuItem testProgress = new JMenuItem("Test progress thing");
		testProgress.addActionListener(ae -> {
			double[] d = new double[] {0};

			Timer timer = new Timer(1000, null);
			timer.addActionListener(ae2 -> {
				if(d[0] > 0d) {
					showProgress();
					setProgress(d[0], "doing stuff");
				}
				d[0]+=.05;
				if(d[0] >= 1d) {
					hideProgress();
					timer.stop();
				}
			});
			timer.start();
		});

		dev.add(jconsole);
		dev.add(deleteMods);
		dev.add(testProgress);
		bar.add(dev);

		return bar;
	}

	private boolean setImage(Transferable t) {
		try {

			if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {


				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				if(files.size() > 0) {
					new Thread(() -> { 

						List<File> classFiles = new ArrayList<File>();
						if(files.size() == 1 && files.get(0).isDirectory()) {
							for(File f : files.get(0).listFiles())
								if(f.getName().endsWith(".class")) {
									classFiles.add(f);
								}
						}
						for(File f: files) {
							if(f.getName().endsWith(".class"))
								classFiles.add(f);
						}

						if(!classFiles.isEmpty()) {
							FilterLoader.CopyFilterFiles(files, this);
						} else {
							try {
								setImage(ImageSequence.read(files.get(0)));
							} catch (Exception e) {
								JOptionPane.showMessageDialog(this, "Image/GIF/sequence/video could not be read", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}).start();
				}
			}
			else if(t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				Object img = t.getTransferData(DataFlavor.imageFlavor);
				setImage(new BufferedImageSequence("pasted image", new BufferedImage[]{ImageTools.toBufferedImage((Image)img)}));
			}
		} catch (UnsupportedFlavorException | IOException e) {
			new Thread(() -> {
				JOptionPane.showMessageDialog(this, "Image/GIF/sequence/video could not be read", "Error", JOptionPane.ERROR_MESSAGE);
			}).start();
		} catch (NoSuchMethodError e) {
			new Thread(() -> {
				JOptionPane.showMessageDialog(this, "This feature is broken with this Java version/OS. Use image file instead", "Error", JOptionPane.ERROR_MESSAGE);
			}).start();
		} catch (Exception e) {
			new Thread(() -> {
				JOptionPane.showMessageDialog(this, "Unknown error. Please send console output to calebcode@gmail.com", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}).start();
		}
		return false;
	}

	private boolean setImage(ImageSequence s) {
		setPanel.setSource(s);
		previewPanel.setNewOriginalDimensions(s.getWidth(), s.getHeight(), s.getFrameCount());
		refresh();
		return true;
	}

	@Override
	public void refresh() {

		previewPanel.addToRenderQueue(new RenderRequest() {

			int frameNumber = setPanel.getGifFrameNumber();

			@Override
			public BufferedImage render() {
				return animation.renderFrame(previewPanel.getRenderWidth());	
			}
			@Override
			public int getGifFrameNumber() {
				return frameNumber;
			}
		});
	}

	public void refreshUI() {
		if(splitPane.getRightComponent().getWidth() < splitPane.getRightComponent().getPreferredSize().width) {
			splitPane.resetToPreferredSizes();
			splitPane.setDividerLocation((splitPane.getDividerLocation()/(double)splitPane.getWidth())*.9);
		}
		refresh();
		//splitPane.setDividerLocation(splitPane.getDividerLocation()-((Integer)UIManager.get("ScrollBar.width")).intValue());
	}

	public Animation getAnimation() {
		return animation;
	}

	@Override
	public void setProgress(double d, String text) {
		progressFrame.setProgress(d, text);

	}

	@Override
	public void showProgress() {
		SwingUtilities.getWindowAncestor(this).setVisible(false);
		setPanel.getAnimationControlPanel().pause();
		progressFrame.showProgress();

	}

	@Override
	public void setCancel(ActionListener onCancel) {
		progressFrame.setCancel(onCancel);
	}

	@Override
	public void hideProgress() {
		SwingUtilities.getWindowAncestor(this).setVisible(true);
		setPanel.getAnimationControlPanel().resume();
		progressFrame.hideProgress();
	}
}
