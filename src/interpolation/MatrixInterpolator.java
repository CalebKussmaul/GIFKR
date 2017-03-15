package interpolation;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import filter.base.ControlOverride;
import gui.LockCheckbox;
import kussmaulUtils.ViewUtils;


public class MatrixInterpolator extends Interpolator {

	private static final long serialVersionUID = -5023494824369171208L;

	private boolean manualMode = false;
	private float time;

	private JSpinner rows;
	private JSpinner cols;

	private JButton manualButton;
	private MatrixElement[][] elements;
	private JPanel matrixP;

	public MatrixInterpolator(float[][] initialMatrix, GetSet gs, ChangeListener... listener) {
		super(gs, listener);

		elements = new MatrixElement[initialMatrix.length][initialMatrix[0].length];
		for(int r = 0; r < elements.length; r++) {
			for(int c = 0; c < elements[r].length; c++) {
				elements[r][c] = new MatrixElement(initialMatrix[r][c]);
			}
		}

		initializeComponents();
		addActionListeners();

		setLayout(new GridBagLayout());
		gbc = ViewUtils.createGBC();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		add(instructionArea, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		
		JPanel rowColP = new JPanel(new GridLayout(2, 2));
		rowColP.add(new JLabel("Rows: "));
		rowColP.add(rows);
		rowColP.add(new JLabel("Columns: "));
		rowColP.add(cols);
		
		add(rowColP, gbc);
		gbc.gridy++;
		gbc.fill= GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(matrixP, gbc);
		setSize(500, 250);
		setLocationRelativeTo(null);
	}

	private void initializeComponents() {
		this.rows = new JSpinner(new SpinnerNumberModel(elements[0].length, 1, 999, 2));
		this.cols = new JSpinner(new SpinnerNumberModel(elements.length, 1, 999, 2));

		manualButton = new JButton("Define matrix");

		matrixP = new JPanel();
		refreshMatrixPanel();
	}

	private void addActionListeners() {
		rows.addChangeListener(cl -> {
			if (((Integer) rows.getValue()) % 2 == 0)
				rows.setValue(((Integer) rows.getValue()) + 1);
			else 
				changeDimensions();
		});
		cols.addChangeListener(cl -> {
			if (((Integer) cols.getValue()) % 2 == 0)
				cols.setValue(((Integer) cols.getValue()) + 1);
			else 
				changeDimensions();
		});

		manualButton.addActionListener(ae -> {
			manualMode = true;
			for(MatrixElement[] ma : elements)
				for(MatrixElement mi : ma)
					mi.refreshUI();
			this.setVisible(true);
		});
		
		animationButton.addActionListener(ae -> {
			manualMode = false;
			for(MatrixElement[] ma : elements)
				for(MatrixElement mi : ma)
					mi.refreshUI();
			refreshMatrixPanel();
		});
	}

	private void changeDimensions() {

		MatrixElement[][] newElts = new MatrixElement[(Integer) rows.getValue()][(Integer) cols.getValue()];
		for(int r = 0; r < (Integer) rows.getValue(); r++) {
			for(int c = 0; c < (Integer) cols.getValue(); c++) {
				MatrixElement element;
				if((r < elements.length) && (c < elements[r].length))
					element = elements[r][c];
				else
					element = new MatrixElement(0f);
				newElts[r][c] = element;

			}
		}
		elements = newElts;
		refreshMatrixPanel();
		animationButton.repaint();
	}

	private void refreshMatrixPanel() {

		matrixP.removeAll();
		matrixP.setLayout(new GridBagLayout());
		GridBagConstraints mConstraints = ViewUtils.createGBC();
		mConstraints.weighty=1;

		for(int r = 0; r < (Integer) rows.getValue(); r++, mConstraints.gridy++) {
			for(int c = 0; c < (Integer) cols.getValue(); c++, mConstraints.gridx++) {
				mConstraints.gridx = c;
				mConstraints.gridy = r;
				matrixP.add(elements[r][c], mConstraints);
			}
			mConstraints.gridx=0;
		}
		revalidate();
		repaint();
	}

	@Override
	public void paintButton(Graphics2D g, int width, int height) {
		for(int i = 0; i < (Integer) rows.getValue()+1; i++)
			g.drawLine(3, 3+ i * ((height-6)/(Integer) rows.getValue()), width-3, 3 + i * ((height-6)/(Integer) rows.getValue()));
		for(int i = 0; i < (Integer) cols.getValue()+1; i++)
			g.drawLine(3+ i * ((width-6)/(Integer) cols.getValue()), 3, 3+ i * ((width-6)/(Integer) cols.getValue()), height-3);
	}

	@Override
	public Object getAnimationValue(float time) {
		this.time = time;
		
		float[][] matrix = new float[(Integer) rows.getValue()][(Integer) cols.getValue()];

		for(int r = 0; r < elements.length; r++) {
			for(int c = 0; c < elements[r].length; c++) {
				matrix[r][c] = (float) elements[r][c].getValue();
			}
		}
		return matrix;
	}

	@Override
	public Object getStaticValue() {
		float[][] matrix = new float[(Integer) rows.getValue()][(Integer) cols.getValue()];

		for(int r = 0; r < elements.length; r++) {
			for(int c = 0; c < elements[r].length; c++) {
				matrix[r][c] = (float) elements[r][c].getValue();
			}
		}
		return matrix;
	}

	@Override
	public String getInstructions() {
		return "This filter is for advanced users. You can represent many types of image filters, from edge detection to blurs, as a matrix convolution. Define your matrix below.";
	}

	@Override
	public JComponent getManualController() {
		return manualButton;
	}

	@Override
	public boolean isKeyframable() {
		return true;
	}

	class MatrixElement extends JPanel {
		private static final long serialVersionUID = 1069705944736570474L;
		@ControlOverride(min="-10000",max="10000")
		public double value;
		private DoubleInterpolator interp;

		private LockCheckbox keyframeBox;

		public MatrixElement(float startingVal) {
			
			value = startingVal;
			initializeComponents();
		}

		private void initializeComponents() {

			keyframeBox = new LockCheckbox();
			keyframeBox.addActionListener(ae -> refreshUI());

			GetSet gs = new GetSet() {

				@Override
				public void set(Object o) {
					value = (Double) o;
				}

				@Override
				public boolean isAnimationMode() {
					return !manualMode && keyframeBox.isSelected();
				}

				@Override
				public float getTime() {
					return time;
				}
			};
			interp = new DoubleInterpolator(value, -10000, 10000, -5, 5, gs, ce -> MatrixInterpolator.this.fireChangeEvent());
			MatrixElement.this.setLayout(new BorderLayout());

			refreshUI();
		}

		public float getValue() {
			interp.refreshValue();
			return (float) value;
		}

		public void refreshUI() {

			this.removeAll();
			if(!manualMode) {
				add(keyframeBox, BorderLayout.WEST);
				if(keyframeBox.isSelected()) {
					add(interp.getAnimationButton(), BorderLayout.CENTER); 
				}
				else {
					interp.exitAnimationMode();
					add(interp.getManualController(), BorderLayout.CENTER);
				}
			}
			else {
				interp.exitAnimationMode();
				add(interp.getManualController(), BorderLayout.CENTER);
			}

			if(!manualMode)
				interp.enterAnimationMode();
			else
				interp.exitAnimationMode();

			MatrixInterpolator.this.revalidate();
		}
	}

}
