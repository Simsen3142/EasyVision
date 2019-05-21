package functions.parameterreceiver;

import java.util.Map;

import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class LogicalNot extends ParameterRepresenter<BooleanParameter>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5388256632277781874L;

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters, ParameterizedObject sender) {
		BooleanParameter param=getFirstFittingParameter(parameters, BooleanParameter.class, getStringVal("paramname"));
		
		if(param!=null && param.getValue()!=null) {
			((BooleanParameter)getParameter("output")).setValue(!param.getValue());
		}else {
			((BooleanParameter)getParameter("output")).setValue(null);
		}
		
		sendParameters();
	}

	@Override
	public Parameter<?> getRepresentationParameter() {
		return new BooleanParameter("output", null, false);
	}

}
