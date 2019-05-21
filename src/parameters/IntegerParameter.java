package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class IntegerParameter extends NumberParameter<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3015981151052692898L;

	public IntegerParameter(String name, Integer value) {
		super(name, value);
	}
	
	public IntegerParameter(String name, Integer value, boolean editable) {
		super(name, value, editable);
	}
	
	public IntegerParameter(String name, int value, int min, int max) {
		super(name, value,min,max);
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterNumberSliderPanel(this);
	}	
}
