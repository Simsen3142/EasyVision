package parameters;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.ParameterReceiver;
import parameters.group.ParameterGroup;
import view.ParameterChangeDialog;

public class ParameterizedObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 681876050920298547L;
	protected Set<ParameterGroup> paramGroups=new LinkedHashSet<ParameterGroup>();
	protected Map<String,Parameter<?>> parameters=new HashMap<String,Parameter<?>>();
	protected Map<String,ParameterObject> allParameters=new LinkedHashMap<String,ParameterObject>();
	private List<ParameterReceiver> paramReceivers=new ArrayList<>();
	
	public ParameterizedObject(ParameterObject...parameters) {
		addParameters(parameters);
	}
	
	protected void addParameters(ParameterObject...parameters) {
		addParameters(Arrays.asList(parameters));
	}
	
	protected void addParameters(Collection<ParameterObject> parameters) {
		for(ParameterObject param:parameters) {
			this.allParameters.put(param.getFullName(),param);
			if(param instanceof Parameter) {
				Parameter<?> parameter=(Parameter<?>)param;
				this.parameters.put(parameter.getFullName(),parameter);
			}else if(param instanceof ParameterGroup) {
				ParameterGroup pg=(ParameterGroup)param;
				paramGroups.add(pg);
				addParameters(pg.getParameters());
			}
		}
	}
	
	public void addParameterReceiver(ParameterReceiver rcvr) {
		if(!paramReceivers.contains(rcvr))
			paramReceivers.add(rcvr);
	}
	
	/**
	 * @return the paramReceivers
	 */
	public List<ParameterReceiver> getParamReceivers() {
		return paramReceivers;
	}

	public void removeParamterReceiver(ParameterReceiver rcvr) {
		paramReceivers.remove(rcvr);
	}
	
	public void clearParameterReceivers() {
		paramReceivers.clear();
	}
	
	public void sendParameters() {
		for(ParameterReceiver rcvr:paramReceivers) {
			if(rcvr!=null)
				rcvr.onParameterReceived(allParameters);
		}
	}
	
	public Parameter<?> getParameter(String paramFullName) {
		return parameters.get(paramFullName);
	}
	
	public double getDoubleVal(String paramName) {
		return ((NumberParameter<?>)(getParameter(paramName))).getValue().doubleValue();
	}
	
	public File getFileVal(String paramName) {
		return ((FileParameter)(getParameter(paramName))).getValue();
	}
	
	public int getIntVal(String paramName) {
		return ((IntegerParameter)(getParameter(paramName))).getValue();
	}
	
	public String getStringVal(String paramName) {
		return ((StringParameter)(getParameter(paramName))).getValue();
	}
	
	public boolean getBoolVal(String paramName) {
		return ((BooleanParameter)(getParameter(paramName))).getValue();
	}
	
	public Enum<?> getEnumVal(String paramName) {
		return ((EnumParameter)(getParameter(paramName))).getValue();
	}
	
	public Map<String,ParameterObject> getParameters(){
		return allParameters;
	}
	
	public Map<String,Parameter<?>> getAllParameters(){
		return parameters;
	}
	
	public Set<ParameterGroup> getParameterGroups(){
		return paramGroups;
	}
	
	public void showParameterChangeDialog() {
		ParameterChangeDialog dlg=new ParameterChangeDialog(this);
		dlg.setVisible(true);
	}
}
