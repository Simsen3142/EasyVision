package functions.parameterreceiver;

import java.util.Map;

import main.ParameterReceiver;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.Parameter;

public class RobotControl extends ParameterizedObject implements ParameterReceiver{
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters) {
		Parameter param1=((Parameter)parameters.get("output_angle"));
		Parameter param3=((Parameter)parameters.get("output_turn"));
		Parameter param2=((Parameter)parameters.get("output_error"));
		
		System.out.println(param1.getName()+"\t"+param1.getValue());
		System.out.println(param2.getName()+"\t"+param2.getValue());
		System.out.println(param3.getName()+"\t"+param3.getValue());
	}
}
