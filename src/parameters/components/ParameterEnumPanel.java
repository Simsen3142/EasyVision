package parameters.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.EnumParameter;
import javax.swing.JComboBox;

public class ParameterEnumPanel extends JPanel {

	private static final long serialVersionUID = 7539825920627280536L;
	private EnumParameter parameter;
	private JComboBox<? extends Enum<?>> comboBox;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(comboBox!=null)
			comboBox.setForeground(c);
	}
	
	public ParameterEnumPanel(EnumParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	public Enum<?> getSelected() {
		return (Enum<?>)comboBox.getSelectedItem();
	}
	
	private void initialize() {
		setOpaque(false);
		this.setValue(parameter.getValue());
		setLayout(new MigLayout("", "[grow]", "[]"));
		Enum<?>[] enumerations= parameter.getValue().getDeclaringClass().getEnumConstants();
		comboBox = new JComboBox<>(enumerations);
		comboBox.setSelectedItem(parameter.getValue());
		comboBox.addActionListener(new ComboBoxActionListener());
		add(comboBox, "cell 0 0,growx,aligny center");
	}
	
	public void setValue(Enum<?> val) {
		parameter.setValue(val);
	}
	
	public Enum<?> getValue() {
		return parameter.getValue();
	}
	
	private class ComboBoxActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parameter.setValue((Enum<?>)comboBox.getSelectedItem());
			
			System.out.println("VALUE SET TO "+parameter.getValue());
		}
	}
}
