package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.ViewUtils;

public class SaveFrame extends JFrame {

	private static final long serialVersionUID = 7593935847720677138L;
	private static SaveFrame p;

	private Animation animation;

	private ProgressDisplay d;

	private JSpinner widthSpinner;
	private JSpinner heightSpinner;
	private JLabel frameDelayLabel;
	private JSpinner frameDelaySpinner;
	private JFileChooser chooser;

	private ActionListener onStart;
	private ActionListener onFinish;

	public SaveFrame(Animation animation, ProgressDisplay d) {
		super("Save");
		this.animation = animation;
		this.d = d;

		initializeComponents();
		addActionListeners();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = ViewUtils.createGBC();

		JPanel spinPanel = new JPanel(new GridLayout(3,2));
		spinPanel.add(new JLabel("Width:"));
		spinPanel.add(widthSpinner);
		spinPanel.add(new JLabel("Height:"));
		spinPanel.add(heightSpinner);
		spinPanel.add(frameDelayLabel);
		spinPanel.add(frameDelaySpinner);

		add(spinPanel, gbc);
		gbc.gridy++;


		JLabel noteLabel = new JLabel("Filename conflicts will be resolved automatically");
		noteLabel.setFont(noteLabel.getFont().deriveFont(noteLabel.getFont().getSize() - 2f));
		add(noteLabel, gbc);
		gbc.gridy++;

		add(chooser, gbc);
		pack();
	}

	public void initializeComponents() {
		widthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 100));
		heightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 100));
		frameDelayLabel = new JLabel("Frame delay (ms):");
		frameDelaySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));

		chooser	= new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setAcceptAllFileFilterUsed(false);
		//setAnimationMode(true);
		//		chooser.setSelectedFile(StringUtil.resolveConflictName(chooser.getSelectedFile(), "glitch", true));
	}
	public void addActionListeners() {

		widthSpinner.addChangeListener(ce -> {
			heightSpinner.setValue(Math.round(((Integer) widthSpinner.getValue()) * (animation.getSourceHeight()/(float) animation.getSourceWidth()))); 
		});
		heightSpinner.addChangeListener(ce -> {
			widthSpinner.setValue(Math.round(((Integer) heightSpinner.getValue()) * (animation.getSourceWidth()/(float) animation.getSourceHeight()))); 
		});


		chooser.addActionListener(ae -> {

			if(ae.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				save();
			}
			setVisible(false);
		});

		chooser.addPropertyChangeListener(pcl -> {
			if(pcl.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
				if(getFormat().equals(FileFormat.ANIMATEDGIF.getDescription())) {
					frameDelaySpinner.setEnabled(true);
					frameDelaySpinner.setValue(0);
					frameDelayLabel.setText("Frame delay (ms):");
				}
				else if(getFormat().equals(FileFormat.MP4.getDescription())) {
					frameDelaySpinner.setEnabled(true);
					frameDelaySpinner.setValue(30);
					frameDelayLabel.setText("Frames/second:");
				}
				else {
					frameDelaySpinner.setEnabled(false);
				}
			} 
		});
	}

	private String getFormat() {
		return chooser.getFileFilter() == null ? "PNG" : (String) chooser.getFileFilter().getDescription();
	}

	private void setAnimationMode(boolean animationMode) {

		for (FileFilter f : chooser.getChoosableFileFilters())
			chooser.removeChoosableFileFilter(f);

		if(animationMode) {
			for(FileFormat f : FileFormat.getAnimatedFormats()) {
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(f.getDescription(), f.getExtension()));
			}
		} else {
			for(FileFormat f : FileFormat.getImageFormats())
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(f.getDescription(), f.getExtension()));
		}
	}

	private void save() {
		if(onStart != null)
			SwingUtilities.invokeLater(() -> onStart.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save")));

		switch (getFormat().toLowerCase()) {
		case "png sequence" : 
			animation.savePNGSequence(chooser.getSelectedFile(), (Integer) widthSpinner.getValue(), d,  onFinish); 
			break;
		case "mp4" : 
			animation.saveVid(chooser.getSelectedFile(), (Integer) widthSpinner.getValue(), (Integer) frameDelaySpinner.getValue(), d, onFinish); 
			break;
		case "animated gif" : 
			animation.saveGIF(chooser.getSelectedFile(), (Integer) widthSpinner.getValue(), (Integer) frameDelaySpinner.getValue(), d,  onFinish); 
			break;
		case "png":
		case "gif":
		case "bmp":
		case "jpg": animation.saveImage(chooser.getSelectedFile(), getFormat(), (Integer) widthSpinner.getValue(), d, onFinish); break;
		}
	}

	public static void open(Animation animation, boolean animationMode, ActionListener onStart, ProgressDisplay d, ActionListener onFinish) {
		if(p == null)
			p = new SaveFrame(animation, d);
		else {
			p.animation = animation;
			p.d = d;
		}

		p.widthSpinner.setValue(animation.getSourceWidth());
		p.onStart = onStart;
		p.onFinish = onFinish;

		//p.chooser.putClientProperty("FileChooser.listViewBorder", BorderFactory.createEmptyBorder());

		p.setLocationRelativeTo(null);
		p.setVisible(true);
		p.setAnimationMode(animationMode);
	}
}
