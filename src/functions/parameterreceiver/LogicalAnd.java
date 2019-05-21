package functions.parameterreceiver;

import java.util.List;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;

public class LogicalAnd extends MultiParameterReceiver<BooleanParameter> implements ParameterReceiver {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7430068273107733157L;


	@Override
	public BooleanParameter onParametersReceived(List<Parameter<?>> parameters) {
		boolean ret=true;

		for(BooleanParameter boolParam:getParameterWhichExtend(BooleanParameter.class, parameters)) {
			if(boolParam.getValue()==null || boolParam.getValue()!=true) {
				ret=false;
				break;
			}
		}
		
		return new BooleanParameter("output", ret);
	}
	
	
	@Override
	public Parameter<?> getRepresentationParameter() {
		return new BooleanParameter("output", false, false);
	}

}
