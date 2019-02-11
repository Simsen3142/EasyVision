package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class IntegerParameter extends NumberParameter<Integer> {

	public IntegerParameter(String name, int value) {
		super(name, value);
	}
	
	public IntegerParameter(String name, int value, int min, int max) {
		super(name, value,min,max);
	}
	
	@Override
	public JComponent getComponent() {
		return new ParameterNumberSliderPanel(this);
	}	
}
