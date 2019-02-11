package parameters;

public abstract class NumberParameter<type extends Number> extends Parameter<type> {
	protected type minValue=(type)new Integer(0);
	protected type maxValue=(type)new Integer(255);
	
	public NumberParameter(String name, type value) {
		super(name, value);
	}
	
	public NumberParameter(String name, type value, type min, type max) {
		super(name, value);
		setBounds(min, max);
	}

	/**
	 * @return the maxValue
	 */
	public type getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(type maxValue) {
		if(maxValue.doubleValue()>value.doubleValue())
			this.maxValue = maxValue;
	}

	/**
	 * @return the minValue
	 */
	public type getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(type minValue) {
		if(minValue.doubleValue()<value.doubleValue())
			this.minValue = minValue;
	}
	
	public void setBounds(type min, type max) {
		setMinValue(min);
		setMaxValue(max);
	}
	
	@Override
	public void setValue(Number value) {
		if(minValue!=null && maxValue!=null) {
			if(value.doubleValue()>maxValue.doubleValue()) {
				value=maxValue;
			}else if(value.doubleValue()<minValue.doubleValue()) {
				value=minValue;
			}
		}
		super.setValue((type)value);
	}
}
