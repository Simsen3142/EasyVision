package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterEnumPanel;

public class EnumParameter extends Parameter<Enum <?>> {

	private static final long serialVersionUID = -9101119584054390925L;

	private Enum<?> enumname;

	public EnumParameter(String name, Enum<?> value) {
		super(name, value);
		setEnumname(value);
	}

	public Enum<?> getEnumname() {
		return enumname;
	}

	private void setEnumname(Enum<?> enumname) {
		this.enumname = enumname;
	}
	
	@Override
	protected JComponent getEditComponent() {
		return new ParameterEnumPanel(this);
	}

}
