package functions.parameterreceiver;

import java.util.List;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;

public class LogicalOr extends MultiParameterReceiver<BooleanParameter> implements ParameterReceiver {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -530271305000414492L;


	@Override
	public BooleanParameter onParametersReceived(List<Parameter<?>> parameters) {
		boolean ret=false;

		for(BooleanParameter boolParam:getParameterWhichExtend(BooleanParameter.class, parameters)) {
			if(boolParam.getValue()!=null && boolParam.getValue()==true) {
				ret=true;
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
