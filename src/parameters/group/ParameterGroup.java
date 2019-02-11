package parameters.group;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import parameters.Parameter;
import parameters.ParameterObject;
import parameters.components.ParameterGroupPanel;

public class ParameterGroup extends ParameterObject{
	protected ArrayList<ParameterObject> parameters=new ArrayList<>();

	/**
	 * @return the parameters
	 */
	public ArrayList<ParameterObject> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<ParameterObject> parameters) {
		this.parameters = parameters;
	}
	
	public ParameterGroup(String name, ParameterObject...parameters) {
		this.name=name;
		for(ParameterObject parameter:parameters) {
			parameter.setParamGroup(this);
			this.parameters.add(parameter);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParameterGroup))
			return false;
		ParameterGroup other = (ParameterGroup) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	@Override
	public JComponent getComponent() {
		return new ParameterGroupPanel(this);
	}
}
