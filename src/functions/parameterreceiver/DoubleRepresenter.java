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

public class DoubleRepresenter extends ParameterRepresenter<DoubleParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5726912838014691971L;
	private int id=System.identityHashCode(this);

	public DoubleRepresenter(){
		super();
	}
	
	public Parameter<?> getRepresentationParameter(){
		return new DoubleParameter("output", 0, false);
	}



	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		DoubleParameter param=getFirstFittingParameter(parameters, DoubleParameter.class, getStringVal("paramname"));
		if(param != null) {
			((DoubleParameter)getParameter("output")).setValue(param.getValue());
			((StringParameter)getParameter("outputName")).setValue(param.getFullName());
		}else {
			((DoubleParameter)getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
}
