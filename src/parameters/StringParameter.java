package parameters;

import javax.swing.JComponent;

import parameters.components.ParameterStringPanel;

public class StringParameter extends Parameter<String> {
	private int maxLength=-1;
	private int minLength=-1;
	
	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @param minLength the minLength to set
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public StringParameter(String name, String value) {
		super(name, value);
	}
	
	public StringParameter(String name, String value, int minLength, int maxLength) {
		super(name, value.substring(0, maxLength));
		this.maxLength=maxLength;
		this.minLength=minLength;
	}

	@Override
	public JComponent getComponent() {
		return new ParameterStringPanel(this);
	}
}
