package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterEnumPanel;

public class EnumParameter extends Parameter<Enum <?>> {

	private static final long serialVersionUID = -9101119584054390925L;

	public EnumParameter(String name, Enum<?> value) {
		super(name, value);
	}

	
	@Override
	public JComponent getEditComponent() {
		return new ParameterEnumPanel(this);
	}

}
