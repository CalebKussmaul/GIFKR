package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.*;

import filter.base.ImageFilter;
import image.ImageSequence;
import utils.ImageTools;
import utils.Refreshable;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -8928464335165056558L;

	private MainPanel mainP;

	private JPanel gifFrameWrap;
	private FieldControlsPanel gifFrameP;
	private JCheckBox animateBox;
	private AnimationControlPanel animationControlP;

	private JCheckBox channelBox;

	private JPanel cardPanel;
	private JTabbedPane channelPane;

	private FilterPanel allPanel;
	private FilterPanel rPanel;
	private FilterPanel gPanel;
	private FilterPanel bPanel;

	private JButton saveButton;

	public SettingsPanel(MainPanel r) {
		this.mainP = r;

		initializeComponents();
		addActionListeners();
		
		setSource(mainP.getAnimation().getSource());

		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc	= new GridBagConstraints();
		gbc.weightx				= 1;
		gbc.weighty				= 0;
		gbc.gridx				= 0;
		gbc.gridy				= 0;
		gbc.gridwidth			= 1;
		gbc.anchor 				= GridBagConstraints.CENTER;
		gbc.fill 				= GridBagConstraints.BOTH;

		add(gifFrameWrap, gbc);
		gbc.gridy++;

		add(animateBox, gbc);
		gbc.gridy++;

		add(animationControlP, gbc);
		gbc.gridy++;

		add(channelBox, gbc);
		gbc.gridy++;

		gbc.weighty = 1;

		add(cardPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 0;

		add(saveButton, gbc);
	}

	private void initializeComponents() {

		channelBox		= new JCheckBox("Filter by Channel");
		animateBox		= new JCheckBox("Animate");

		gifFrameWrap = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = -4162210119715402189L;

			@Override
			public Dimension getMinimumSize() {
				return getPreferredSize();
			}
		};

		allPanel = new FilterPanel(this, false);
		rPanel = new FilterPanel(this, true);
		gPanel = new FilterPanel(this, true);
		bPanel = new FilterPanel(this, true);

		animationControlP = new AnimationControlPanel(mainP);
		animationControlP.setVisible(false);

		channelPane = new JTabbedPane();
		channelPane.add("Red", rPanel);
		channelPane.add("Green", gPanel);
		channelPane.add("Blue", bPanel);


		cardPanel = new JPanel(new CardLayout());
		cardPanel.add(allPanel);
		cardPanel.add(channelPane);

		saveButton		= new JButton("Save...");
	}

	private void addActionListeners() {

		animateBox.addChangeListener(ce -> {
			allPanel.setAnimationMode(animateBox.isSelected());
			rPanel.setAnimationMode(animateBox.isSelected());
			gPanel.setAnimationMode(animateBox.isSelected());
			gPanel.setAnimationMode(animateBox.isSelected());
			animationControlP.setVisible(animateBox.isSelected());
			gifFrameP.setAnimationMode(animateBox.isSelected());
			//gifFrameControl.setAnimationLock(animateBox.isSelected());
			animationControlP.setAnimating(animateBox.isSelected());

			mainP.refresh();
		});

		channelBox.addActionListener(ae -> {
			if(channelBox.isSelected())
				((CardLayout) cardPanel.getLayout()).last(cardPanel);
			else
				((CardLayout) cardPanel.getLayout()).first(cardPanel);

			mainP.getAnimation().setFilter(getFilter());
			mainP.refreshUI();
		});

		saveButton.addActionListener(ae -> {
			SaveFrame.open(mainP.getAnimation(), animateBox.isSelected(), ae2 -> mainP.showProgress(), mainP, ae2 -> mainP.hideProgress());
		});
	}

	public void refreshFrame() {
		gifFrameP.updateChangedValues();
		mainP.refresh();
	}

	public int getGifFrameNumber() {
		return mainP.getAnimation().getSource().getFrameNumber();
	}

	public void setSource(ImageSequence s) {
		mainP.getAnimation().setSource(s);
		gifFrameP = s.getFrameControl(ce -> refreshFrame());
		gifFrameWrap.removeAll();
		gifFrameWrap.add(gifFrameP, BorderLayout.CENTER);
		animateBox.setSelected(s.getFrameCount() > 1);
		if(animationControlP != null)
			mainP.getAnimation().setFrameCount(animationControlP.setFrameCount(s.getFrameCount()));
	}

	public Refreshable getRefreshable() {
		return mainP;
	}

	public void refreshImageFilters() {
		mainP.getAnimation().setFilter(getFilter());
		mainP.refreshUI();
		mainP.refresh();
	}

	public boolean isAnimationMode() {
		return animateBox.isSelected();
	}

	private ImageFilter getFilter() {
		if (!channelBox.isSelected()) {
			return allPanel.getFilter();
		}
		else {
			return new ImageFilter() {
				@Override
				protected BufferedImage apply(BufferedImage img) {

					BufferedImage rimg = ImageTools.getRedChannel(img);
					BufferedImage gimg = ImageTools.getGreenChannel(img);
					BufferedImage bimg = ImageTools.getBlueChannel(img);


					rimg = rPanel.getFilter().getFilteredImage(rimg);
					gimg = gPanel.getFilter().getFilteredImage(gimg);
					bimg = bPanel.getFilter().getFilteredImage(bimg);

					return ImageTools.combine(null, rimg, gimg, bimg);
				}

				@Override
				public void setX(float x) {
					super.setX(x);
					rPanel.getFilter().setX(x);
					gPanel.getFilter().setX(x);
					bPanel.getFilter().setX(x);
				}
			};
		}
	}

	public AnimationControlPanel getAnimationControlPanel() {
		return animationControlP;
	}
}
