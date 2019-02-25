package components;

import java.awt.Component;

import javax.swing.*;

public class ClassListCellRenderer extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// I know DefaultListCellRenderer always returns a JLabel
		// super setups up all the defaults
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		// "value" is whatever object you put into the list, you can use it however you
		// want here

		// I'm going to prefix the label text to demonstrate the point
		Class<?> c=(Class<?>)value;
		label.setText(c.getSimpleName());

		return label;
	}
}