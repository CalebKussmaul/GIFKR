package kussmaulUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ViewUtils {

	public static JPanel enumToRadioPanel(Object defValue, Consumer<Object> listener) {
		JPanel setP = new JPanel();

		Object[] types = defValue.getClass().getEnumConstants();
		ButtonGroup bg = new ButtonGroup();
		setP.setLayout(new GridLayout(types.length, 1));

		for (Object o : types) {

			JRadioButton b = new JRadioButton(o.toString());
			if(o.equals(defValue))
				b.setSelected(true);

			setP.add(b);
			bg.add(b);
			b.addChangeListener( ae -> {
				if(b.isSelected())
					listener.accept(o);
			});
		}
		return setP;
	}

	public static void fixWinSliderUI(JSlider s) {

		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			s.setUI(new BasicSliderUI(s){
				protected Dimension getThumbSize() {

					Dimension old = super.getThumbSize();

					return new Dimension(old.width*2, old.height);
				}
			});
		}
	}

	public static JPanel createLabelField(String preText, String postText, JSlider s) {
		JPanel p = new JPanel();

		JSpinner spin = new JSpinner(new SpinnerNumberModel(s.getValue(), s.getMinimum(), s.getMaximum(), 1));
		spin.addChangeListener(ce -> {s.setValue((Integer) spin.getValue());});
		p.add(new JLabel(preText));
		p.add(spin);
		p.add(new JLabel(postText));

		return p;
	}

	public static JLabel getLabel(Object obj, Field f) {
		//return new JLabel(StringUtil.deCamel(f.getName()));
		return f.getType().isAssignableFrom(Boolean.TYPE) ? null : new JLabel(StringUtil.deCamel(f.getName())+": ");
	}

	public static JComponent getComponent(Object obj, Field f, Runnable r) {
		Class<?> c = f.getType();

		try {
			if (c.isAssignableFrom(Boolean.TYPE))  {
				JCheckBox box = new JCheckBox(StringUtil.deCamel(f.getName()), f.getBoolean(obj));
				box.addActionListener(ae -> {
					try {
						f.set(obj, box.isSelected());
						r.run();
					} catch (Exception e) {}
				});
				return box;
			}

			else if (c.isAssignableFrom(Float.TYPE)) {
				JSlider slider = new JSlider(0, Integer.MAX_VALUE, (int) (f.getFloat(obj) * Integer.MAX_VALUE));
				slider.addChangeListener(ce -> {
					try {
						f.set(obj, slider.getValue()/(float) slider.getMaximum());
						r.run();
					} catch (Exception e) {}
				});
				return slider;
			}

			else if (c.isAssignableFrom(Double.TYPE)) {
				JSpinner spin = new JSpinner(new SpinnerNumberModel(f.getDouble(obj), 0, Double.MAX_VALUE, .25));
				((JSpinner.DefaultEditor)spin.getEditor()).getTextField().setColumns(8);
				spin.addChangeListener(ce -> {
					try {
						f.set(obj, (double) spin.getValue());
						r.run();
					} catch (Exception e) {}
				});
				return spin;
			}

			else if (c.isAssignableFrom(Integer.TYPE)) {
				JSpinner spin = new JSpinner(new SpinnerNumberModel(f.getInt(obj), 0, Integer.MAX_VALUE, 1));
				((JSpinner.DefaultEditor)spin.getEditor()).getTextField().setColumns(8);
				spin.addChangeListener(ce -> {
					try {
						f.set(obj, (int) spin.getValue());
						r.run();
					} catch (Exception e) {}
				});
				return spin;
			}

			else if (c.isAssignableFrom(String.class)) {
				JTextArea area = new JTextArea(2,2);
				area.setText((String) f.get(obj));
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				area.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent de) {
						try {
							f.set(obj, area.getText());
							r.run();
						} catch (Exception e) {}
					}
					@Override
					public void removeUpdate(DocumentEvent de) {
						try {
							f.set(obj, area.getText());
							r.run();
						} catch (Exception e) {}
					}
					public void changedUpdate(DocumentEvent de) {}
				});
				JScrollPane jsp = new JScrollPane(area);
				jsp.setBorder(new JTextField().getBorder());
				return jsp;
			}

			else if(c.isAssignableFrom(Font.class)) {
				JComboBox<String> box = new JComboBox<String>() {
					private static final long serialVersionUID = 2971823475235944187L;
					@Override
					public Dimension getPreferredSize() {
						Dimension ps = super.getPreferredSize();
						return new Dimension(Math.min(ps.width, 40), ps.height);
					}
				};

				for(Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {

					box.addItem(font.getFontName());
				}
				if(f.get(obj) != null)
					box.setSelectedItem(((Font)f.get(obj)).getFontName());
				box.addActionListener(ae -> {
					try {
						f.set(obj, new Font((String)box.getSelectedItem(), Font.PLAIN, f.get(obj) == null ? 12 : ((Font)f.get(obj)).getSize()));
						r.run();
					} catch (Exception e) {}
				});
				return box;
			}

			else if(c.isAssignableFrom(Color.class)) {
				JButton colorButton = new JButton("Select color");
				JColorChooser chooser = new JColorChooser();
				fixOsxColorChooser(chooser);
				JFrame frame = new JFrame("Select " + f.getName() +" color");
				frame.setContentPane(chooser);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

				if(f.get(obj) != null)
					chooser.setColor((Color) f.get(obj));

				chooser.getSelectionModel().addChangeListener(ce -> {
					try {
						f.set(obj, chooser.getColor());
						r.run();
					} catch (Exception e) {}
				});
				colorButton.addActionListener(ae -> {
					if (!frame.isVisible()) {
						frame.setVisible(true);
					}
					else {
						frame.setLocationRelativeTo(null);
						frame.toFront();
					}
				});
				return colorButton;
			}

			else if (c.isEnum()) {
				JComboBox<String> box = new JComboBox<String>();
				HashMap<String, Object> map = new HashMap<String, Object>();

				for(Object constant : c.getEnumConstants()) {
					String name = StringUtil.deCap(constant.toString());
					box.addItem(name);
					map.put(name, constant);
				}
				box.setSelectedItem(StringUtil.deCap(f.get(obj).toString()));
				box.addActionListener(ae -> {
					try {
						f.set(obj, map.get(box.getSelectedItem()));
						r.run();
					} catch (Exception e) {}
				});
				return box;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void fixOsxColorChooser(JColorChooser chooser) {

		if(!UIManager.getLookAndFeel().getName().equals("Mac OS X"))
			return;

		AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
		for(JPanel p : panels) {
			if(p!=null) {
				p.setOpaque(false);
				((JComponent) p.getParent()).setOpaque(false);
				for(Component c : p.getComponents()) {
					((JComponent) c).setBorder(null);
					((JComponent) c).setOpaque(false);
				}
			}
		}
	}
	
	public static void centerText(JTextPane pane) {
		StyledDocument doc = pane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}

	public static AbstractColorChooserPanel findPanel(JColorChooser chooser, String name) {
		AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
		for (int i = 0; i < panels.length; i++) {
			String clsName = panels[i].getClass().getName();
			if (clsName.equals(name)) {
				return panels[i];
			}
		}
		return null;
	}
	
	public static GridBagConstraints createGBC() {
		GridBagConstraints gbc	= new GridBagConstraints(); 
		gbc.weightx				= 1;
		gbc.weighty				= 0;
		gbc.gridx				= 0;
		gbc.gridy				= 0;
		gbc.gridwidth 			= 1;
		gbc.anchor 				= GridBagConstraints.CENTER;
		gbc.fill				= GridBagConstraints.NONE;
		
		return gbc;
	}
	
	public static Component createDummyComponent() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return panel;
	}
	
	public static JPanel wrapMacFancy(JPanel content) {
		JPanel panel = new JPanel(new BorderLayout()) {

			private static final long serialVersionUID = 941275105778632480L;

			@Override
			public void paintComponent(Graphics g) {
				Color border = new Color(64, 64, 64, 64);
				Color bg = new Color(128, 128, 128, 64);

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(bg);
				g2.fillRoundRect(1, 1, this.getWidth()-2, this.getHeight()-2, 10, 10);
				g2.setColor(border);
				g2.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, 10, 10);
			}
		};
		
		panel.setOpaque(false);
		content.setOpaque(false);
		panel.add(content, BorderLayout.CENTER);
		
		return panel;
	}
}
