package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterNumberSliderPanel;
import parameters.components.ParameterNumberSliderPanel.Type;

public class BinaryIntParameter extends IntegerParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5531718219442935968L;

	public BinaryIntParameter(String name, int value) {
		super(name, value);
	}
	
	public BinaryIntParameter(String name, int value, boolean editable) {
		super(name, value, editable);
	}
	
	public BinaryIntParameter(String name, int value, int min, int max) {
		super(name, value,min,max);
	}
	
	public String getBinaryValue() {
		return Integer.toBinaryString(getValue().intValue());
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterNumberSliderPanel(this,Type.BINARY);
	}
	
	
}
