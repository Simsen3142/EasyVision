package functions.parameterreceiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ParameterReceiver;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;

public abstract class ParameterRepresenter<T extends Parameter<?>> extends ParameterizedObject implements ParameterReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5726912838014691971L;
	private int id=System.identityHashCode(this);
	
	public ParameterRepresenter(ParameterObject...parameterObjects) {
		super(parameterObjects);
		Parameter<?> param=getRepresentationParameter();
		Parameter<?> paramName=getNameParameter();
		addParameters(param,paramName);
	}

	public ParameterRepresenter(){
		super();
		Parameter<?> param=getRepresentationParameter();
		param.setName("output");
		StringParameter paramName=new StringParameter("paramname", "");
		addParameters(param,getNameParameter(),paramName);
	}
	
	
	/**
	 * @return the parameter
	 */
	public T getParameter() {
		return (T) getParameter(getRepresentationParameter().getFullName());
	}
	/**
	 * @param parameter the parameter to set
	 */
	public void setParameterValue(Object value) {
		getParameter().setValue2(value);
	}
	
	@Override
	public void recalculateId() {
		this.id*=Math.random();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParameterRepresenter))
			return false;
		ParameterRepresenter<?> other = (ParameterRepresenter<?>) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public abstract Parameter<?> getRepresentationParameter();
	
	public StringParameter getNameParameter() {
		return new StringParameter("outputname","",false);
	}
}
