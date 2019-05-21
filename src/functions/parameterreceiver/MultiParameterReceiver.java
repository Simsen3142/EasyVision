package functions.parameterreceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public abstract class MultiParameterReceiver<T extends Parameter<?>> extends ParameterRepresenter<T> {
	
	private int id=System.identityHashCode(this);
	private transient Map<ParameterizedObject,Object[]> paramObjects;
	private transient boolean loading=false;
	private long loadingStarted;
	private boolean loaded=false;
	private transient final int time2load=2000;
	private transient List<ParameterizedObject> objectsToReceiveFrom;
	
	@Override
	public void recalculateId() {
		this.id*=Math.random();
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public MultiParameterReceiver() {
		super();
		Parameter<?> param=getParameter("paramname");
		param.setValue2("output");
	}
	
	
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		long time=System.currentTimeMillis();
		
		if(paramObjects==null) {
			paramObjects=new HashMap<ParameterizedObject, Object[]>();
		}
		
		if(!loaded) {
			if(!loading) {
				loading=true;
				loadingStarted=time;
			}
			
		}
		
		{
			ParameterObject po=parameters.get("output");
			
			Parameter<?> p = null;
			if(po == null || po instanceof Parameter<?>) {
				p=(Parameter<?>)po;
				if(p==null) {
					p=getFirstFittingParameter(parameters,BooleanParameter.class,"-");
					if(p==null) {
						p=getFirstFittingParameter(parameters,Parameter.class);
					}
				}
			}
			
			System.out.println("\t"+sender);
			System.out.println("\t"+p);
			
			
			if(p != null) {
				paramObjects.put(sender, new Object[] {p,time});
			}else {
				return;
			}
		}
		
		if(loadingStarted+time2load<=time) {
			loading=false;
			loaded=true;
		}
		
		List<ParameterizedObject> toRemove=new ArrayList<>();
		for(ParameterizedObject po:paramObjects.keySet()) {
			Object[] o=paramObjects.get(po);
			long starttime=(long) o[1];
			if(starttime+time2load<time) {
				toRemove.add(po);
			}
		}
		
		for(ParameterizedObject po:toRemove) {
			paramObjects.remove(po);
			if(objectsToReceiveFrom!=null && objectsToReceiveFrom.contains(po)) {
				objectsToReceiveFrom.remove(po);
			}
		}
		
		if(loaded) {
			if(objectsToReceiveFrom==null || objectsToReceiveFrom.isEmpty()) {
				objectsToReceiveFrom=new ArrayList<>(paramObjects.keySet());
			}
			
			objectsToReceiveFrom.remove(sender);
			
			if(objectsToReceiveFrom.isEmpty()) {
				List<Parameter<?>> paramVals=new ArrayList<>();
				for(Object[] os:paramObjects.values()) {
					paramVals.add((Parameter<?>) os[0]);
				}
				
				setParameterValue(onParametersReceived(paramVals).getValue());
				sendParameters();
			}
		}
	}
	
	public abstract T onParametersReceived(List<Parameter<?>> parameters);
	

}
