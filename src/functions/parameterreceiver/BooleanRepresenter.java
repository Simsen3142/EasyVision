package functions.parameterreceiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;

public class BooleanRepresenter extends ParameterRepresenter<BooleanParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5726912838014691971L;
	private int id=System.identityHashCode(this);

	public BooleanRepresenter(){
		super();
		
	}
	
	
	
	public Parameter<?> getRepresentationParameter(){
		return new BooleanParameter("output", false, false);
	}



	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters, ParameterizedObject sender) {
		BooleanParameter param=getFirstFittingParameter(parameters, BooleanParameter.class, getStringVal("paramname"));
		if(param != null) {
			((BooleanParameter)getParameter("output")).setValue(param.getValue());
		}else {
			((BooleanParameter)getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
}
