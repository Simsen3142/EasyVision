package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class DoubleParameter extends NumberParameter<Double> {

	public DoubleParameter(String name, double value) {
		super(name, value);
	}
	
	public DoubleParameter(String name, double value, double min, double max) {
		super(name, value,min,max);
	}

	@Override
	public JComponent getComponent() {
		return new ParameterNumberSliderPanel(this);
	}

}
