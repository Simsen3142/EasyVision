package parameters.components;

import java.awt.Color;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.EnumParameter;
import javax.swing.JComboBox;

public class ParameterEnumPanel extends JPanel {

	private static final long serialVersionUID = 7539825920627280536L;
	private EnumParameter parameter;
	private static JComboBox<?> comboBox;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(comboBox!=null)
			comboBox.setForeground(c);
	}
	
	public ParameterEnumPanel(EnumParameter parameter) {
		this.parameter=parameter;
		setLayout(new MigLayout("", "[grow]", "[]"));
		comboBox = new JComboBox<>(parameter.getEnumname().getDeclaringClass().getEnumConstants());
		add(comboBox, "cell 0 0,growx,aligny center");
		initialize();
		
	}
	
	public static Enum<?> getSelected() {
		return (Enum<?>)comboBox.getSelectedItem();
	}
	
	private void initialize() {
		setOpaque(false);
		this.setValue(parameter.getValue());
	}
	
	public void setValue(Enum<?> val) {
		parameter.setValue(val);
	}
	
	public Enum<?> getValue() {
		return parameter.getValue();
	}
}
