package main;

import java.util.Map;

import parameters.ParameterObject;

public interface ParameterReceiver {
	public void onParameterReceived(Map<String,ParameterObject> parameters);
}
