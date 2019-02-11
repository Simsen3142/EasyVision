package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterBooleanCheckboxPanel;

public class BooleanParameter extends Parameter<Boolean> {

	public BooleanParameter(String name, Boolean value) {
		super(name, value);
	}

	@Override
	public JComponent getComponent() {
		return new ParameterBooleanCheckboxPanel(this);
	}
}
