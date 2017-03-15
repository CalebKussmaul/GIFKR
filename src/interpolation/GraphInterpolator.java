package interpolation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import kussmaulUtils.ViewUtils;

public abstract class GraphInterpolator extends Interpolator {
	
	private static final long serialVersionUID = -3124467961989474300L;
	
	protected JPanel graphPanel;
	
	public GraphInterpolator(GetSet gs, ChangeListener... listener) {
		super(gs, listener);
		
		initializeComponents();
		addActionListeners();
		
		setLayout(new GridBagLayout());
		gbc = ViewUtils.createGBC();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.insets=new Insets(0, 3, 0, 3);
		
		add(instructionArea, gbc);
		gbc.gridy++;
		gbc.weighty=1;

		add(ViewUtils.wrapMacFancy(graphPanel), gbc);
		gbc.gridy++;
		gbc.weighty=0;
		
		setLocationRelativeTo(null);
	}
	
	private void initializeComponents() {
		
		graphPanel = new JPanel() {
			private static final long serialVersionUID = -1105560304467720245L;
			
			@Override 
			public Dimension getPreferredSize() {
				return getGraphSize();
			}
			
			@Override 
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintGraph((Graphics2D) g, graphPanel.getWidth(), graphPanel.getHeight());
			}
		};
		graphPanel.setFocusable(true);
	}

	private void addActionListeners() {
		
		graphPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved   (MouseEvent e) {graphMoved   (getX(e), getY(e), SwingUtilities.isRightMouseButton(e));}
			public void mouseDragged (MouseEvent e) {graphDragged (getX(e), getY(e), SwingUtilities.isRightMouseButton(e));}
		});
		graphPanel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {graphReleased(getX(e), getY(e), SwingUtilities.isRightMouseButton(e));}
			public void mouseExited  (MouseEvent e) {graphExited  (getX(e), getY(e), SwingUtilities.isRightMouseButton(e));}
			public void mouseClicked (MouseEvent e) {graphClicked (getX(e), getY(e), SwingUtilities.isRightMouseButton(e));}
			public void mousePressed (MouseEvent e) {
				graphPanel.requestFocus();
				graphPressed(getX(e), getY(e), SwingUtilities.isRightMouseButton(e));
			}
			public void mouseEntered(MouseEvent e) {
				graphPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphEntered(getX(e), getY(e), SwingUtilities.isRightMouseButton(e));
			}
		});
	}
	
	private float getX(MouseEvent e) {
		return Math.min(1, Math.max(0, e.getX()/(float)graphPanel.getWidth()));
	}
	
	private float getY(MouseEvent e) {
		float y = 1f - e.getY()/(float)graphPanel.getHeight();
		return restrictRange() ? Math.max(0, Math.min(1, y)) : y;
	}
	
	protected boolean restrictRange() {return true;}
	protected void graphMoved   (float x, float y, boolean rightClick) {}
	protected void graphDragged (float x, float y, boolean rightClick) {}
	protected void graphReleased(float x, float y, boolean rightClick) {}
	protected void graphPressed (float x, float y, boolean rightClick) {}
	protected void graphExited  (float x, float y, boolean rightClick) {}
	protected void graphEntered (float x, float y, boolean rightClick) {}
	protected void graphClicked (float x, float y, boolean rightClick) {}
	
	public abstract Dimension getGraphSize();
	public abstract void paintGraph(Graphics2D g, int width, int height);
}
