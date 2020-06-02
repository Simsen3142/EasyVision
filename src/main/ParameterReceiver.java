package main;

import java.util.Map;

import functions.UniqueFunction;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public interface ParameterReceiver extends UniqueFunction {
	public void onParameterReceived(Map<String,ParameterObject> parameters, ParameterizedObject parameterizedObjectNonSerializable);
}
