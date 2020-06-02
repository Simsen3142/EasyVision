package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class DoubleParameter extends NumberParameter<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2144654837832041278L;
	private boolean isPercent=false;
	
	public boolean isPercent() {
		return isPercent;
	}

	public DoubleParameter(String name, double value) {
		super(name, value);
	}
	
	public DoubleParameter(String name, double value, boolean editable) {
		super(name, value, editable);
	}
	
	public DoubleParameter(String name, double value, double min, double max) {
		super(name, value,min,max);
	}
	
	public DoubleParameter(String name, double value, double min, double max, boolean isPercent) {
		super(name, value,min,max);
		this.isPercent=isPercent;
	}
	
	@Override
	public Double getValue() {
		try {
			return value.doubleValue();
		}catch (ClassCastException e) {
			if(isPercent)
				return value.doubleValue()/100;
			return value.doubleValue();
		}
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterNumberSliderPanel(this);
	}

}
