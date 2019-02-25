package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterBooleanCheckboxPanel;

public class BooleanParameter extends Parameter<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6936628844222413144L;

	public BooleanParameter(String name, Boolean value) {
		super(name, value);
	}
	
	public BooleanParameter(String name, Boolean value, boolean editable) {
		super(name, value, editable);
	}

	@Override
	public JComponent getEditComponent() {
		return new ParameterBooleanCheckboxPanel(this);
	}
}
