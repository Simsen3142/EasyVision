package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class DoubleParameter extends NumberParameter<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2144654837832041278L;

	public DoubleParameter(String name, double value) {
		super(name, value);
	}
	
	public DoubleParameter(String name, double value, boolean editable) {
		super(name, value, editable);
	}
	
	public DoubleParameter(String name, double value, double min, double max) {
		super(name, value,min,max);
	}

	@Override
	public JComponent getEditComponent() {
		return new ParameterNumberSliderPanel(this);
	}

}
