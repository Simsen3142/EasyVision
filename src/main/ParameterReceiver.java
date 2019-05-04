package main;

import java.util.Map;

import functions.UniqueFunction;
import parameters.ParameterObject;

public interface ParameterReceiver extends UniqueFunction {
	public void onParameterReceived(Map<String,ParameterObject> parameters);
}
