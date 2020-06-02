package functions.parameterreceiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;

public class StringRepresenter extends ParameterRepresenter<StringParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5726912838014691971L;
	private int id=System.identityHashCode(this);

	public StringRepresenter(){
		super();
	}
	
	public Parameter<?> getRepresentationParameter(){
		return new StringParameter("output", "", false);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		StringParameter param=getFirstFittingParameter(parameters, StringParameter.class, getStringVal("paramname"));
		if(param != null) {
			((StringParameter)getParameter("output")).setValue(param.getValue());
			((StringParameter)getParameter("outputName")).setValue(param.getFullName());
		}else {
			((StringParameter)getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
}
