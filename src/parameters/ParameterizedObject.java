package parameters;

import java.awt.Component;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import diagramming.components.FunctionPanel;
import main.ParameterReceiver;
import parameters.group.ParameterGroup;
import view.ParameterChangeDialog;

public class ParameterizedObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 681876050920298547L;
//	protected Set<ParameterGroup> paramGroups=new LinkedHashSet<ParameterGroup>();
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
//				paramGroups.add(pg);
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
				rcvr.onParameterReceived(allParameters,this);
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
	
	public Map<String,ParameterObject> getAllParameters(){
		return allParameters;
	}
	
	public Map<String,Parameter<?>> getParameters(){
		return parameters;
	}
	
//	public Set<ParameterGroup> getParametesrGroups(){
//		return paramGroups;
//	}
	
	public <T extends ParameterObject> List<T> getParameterWhichExtend(Class<T> c){
		return getParameterWhichExtend(c,getAllParameters().values());
	}
	
	public static <T extends ParameterObject> List<T> getParameterWhichExtend(Class<T> c, Collection<? extends ParameterObject> params) {
		List<T> ret=new ArrayList<>();
		for(ParameterObject param:params) {
			if(c.isAssignableFrom(param.getClass())) {
				ret.add((T)param);
			}
		}
		
		return ret;
	}
	
	public static <R extends Parameter<?>> R getFirstFittingParameter(Map<String, ParameterObject> parameters, Class<R> clss) {
		return getFirstFittingParameter(parameters,clss,"output");
	}
	
	@SuppressWarnings("unchecked")
	public static <R extends Parameter<?>> R getFirstFittingParameter(Map<String, ParameterObject> parameters, Class<R> clss, String expectedName) {
		ParameterObject param=parameters.get(expectedName);
		if(param!=null && clss.isAssignableFrom(param.getClass())) {
			return (R)param;
		} else {
			List<Parameter<?>> params=(List<Parameter<?>>) getParameterWhichExtend(clss, parameters.values());
			for(ParameterObject p:params) {
				if(!((Parameter<?>) p).isEditable()) {
					return (R) p;
				}
			}
			
			for(ParameterObject p:params) {
				return (R) p;
			}
		}
		
		return null;
	}

	
	public <T extends ParameterReceiver> List<T> getParameterReceiverWhichExtend(Class<T> c) {
		List<T> ret=new ArrayList<>();
		for(ParameterReceiver paramRec:getParamReceivers()) {
			if(c.isAssignableFrom(paramRec.getClass())) {
				ret.add((T)paramRec);
			}
		}
		
		return ret;
	}
	
	public void showParameterChangeDialog() {
		ParameterChangeDialog dlg=new ParameterChangeDialog(this);
		dlg.setVisible(true);
	}

	public void stop() {
	}
}
