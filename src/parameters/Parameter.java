package parameters;

import java.util.function.Function;

import parameters.group.ParameterGroup;

public abstract class Parameter<type> extends ParameterObject{
	protected type value;
	protected Function<type, Void> onChange;
	
	public Parameter<type> setOnChange(Function<type, Void> onChange) {
		this.onChange=onChange;
		return this;
	}
	
	public void resetOnChange() {
		onChange=null;
	}
	
	/**
	 * @return the value
	 */
	public type getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(type value) {
		this.value=value;
		if(onChange!=null)
			onChange.apply(value);
	}

	/**
	 * @param name
	 * @param value
	 * @param type
	 */
	public Parameter(String name, type value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Parameter))
			return false;
		Parameter other = (Parameter) obj;
		if (getFullName() == null) {
			if (other.getFullName() != null)
				return false;
		} else if (!getFullName().equals(other.getFullName()))
			return false;
		return true;
	}
}
