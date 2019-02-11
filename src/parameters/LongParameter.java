package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class LongParameter extends NumberParameter<Long> {

	public LongParameter(String name, long value) {
		super(name, value);
	}

	public LongParameter(String name, long value, long min, long max) {
		super(name, value,min,max);
	}
	
	@Override
	public JComponent getComponent() {
		return new ParameterNumberSliderPanel(this);
	}
}
