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
	
	
}
