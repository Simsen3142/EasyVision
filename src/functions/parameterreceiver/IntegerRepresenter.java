package functions.parameterreceiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;

public class IntegerRepresenter extends ParameterRepresenter<IntegerParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5726912838014691971L;
	private int id=System.identityHashCode(this);

	public IntegerRepresenter(){
		super();
	}
	
	public Parameter<?> getRepresentationParameter(){
		return new IntegerParameter("output", null, false);
	}



	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		IntegerParameter param=getFirstFittingParameter(parameters, IntegerParameter.class, getStringVal("paramname"));
		if(param != null) {
			((IntegerParameter)getParameter("output")).setValue(param.getValue());
		}else {
			((IntegerParameter)getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
}
