package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;

public class LongParameter extends NumberParameter<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1263465815981153589L;

	public LongParameter(String name, long value) {
		super(name, value);
	}
	
	public LongParameter(String name, long value, boolean editable) {
		super(name, value, editable);
	}

	public LongParameter(String name, long value, long min, long max) {
		super(name, value,min,max);
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterNumberSliderPanel(this);
	}
}
