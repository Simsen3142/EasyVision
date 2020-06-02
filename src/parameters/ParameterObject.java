package parameters;

import java.io.Serializable;

import javax.swing.JComponent;

import parameters.group.ParameterGroup;

public abstract class ParameterObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1001197668755353585L;
	protected String name;
	protected ParameterGroup paramGroup=null;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the paramGroup
	 */
	public ParameterGroup getParamGroup() {
		return paramGroup;
	}

	/**
	 * @param paramGroup the paramGroup to set
	 */
	public void setParamGroup(ParameterGroup paramGroup) {
		this.paramGroup = paramGroup;
	}
	
	public String getFullName() {
		return ((paramGroup!=null)?paramGroup.getFullName()+"_":"")+name;
	}
	
	public abstract JComponent getComponent(ParameterizedObject po);

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
		if (getClass() != obj.getClass())
			return false;
		ParameterObject other = (ParameterObject) obj;
		if (getFullName() == null) {
			if (other.getFullName() != null)
				return false;
		} else if (!getFullName().equals(other.getFullName()))
			return false;
		return true;
	}
}
