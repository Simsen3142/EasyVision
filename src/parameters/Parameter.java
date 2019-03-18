package parameters;

import java.util.function.Function;

import javax.swing.JComponent;

import parameters.components.ParameterNoneditablePanel;

public abstract class Parameter<type> extends ParameterObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6348280596086762694L;
	protected type value;
	protected transient Function<type, Void> onChange;
	protected boolean editable=true;
	
	public Parameter<type> setOnChange(Function<type, Void> onChange) {
		if(this.onChange!=onChange)
			this.onChange=onChange;
		return this;
	}
	
	public void resetOnChange() {
		onChange=null;
	}
	
	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
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
		if(onChange!=null) {
			onChange.apply(value);
			System.out.println("ONCHANGE");
		}
	}
	
	/**
	 * @param name
	 * @param value
	 * @param type
	 * @param editable
	 */
	public Parameter(String name, type value, boolean editable) {
		this.name = name;
		this.value = value;
		this.editable=editable;
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
	public final JComponent getComponent() {
		if(editable) {
			return getEditComponent();
		}else {
			return new ParameterNoneditablePanel(this);
		}
	}
	
	protected abstract JComponent getEditComponent();
	
	

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
		Parameter<?> other = (Parameter<?>) obj;
		if (getFullName() == null) {
			if (other.getFullName() != null)
				return false;
		} else if (!getFullName().equals(other.getFullName()))
			return false;
		return true;
	}
}
