package functions.parameterreceiver;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;

public class MathematicalFunction extends ParameterRepresenter<DoubleParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4087522834383131484L;
	private int id=System.identityHashCode(this);

	public MathematicalFunction(){
		super(new StringParameter("mathFunction", "x"),
				new DoubleParameter("min", -10000),
				new DoubleParameter("max", 10000),
				new StringParameter("paramname", "x"));
	}
	
	public Parameter<?> getRepresentationParameter(){
		return new DoubleParameter("output", 0, false);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		DoubleParameter param=getFirstFittingParameter(parameters, DoubleParameter.class, getStringVal("paramname"));
		IntegerParameter param2=getFirstFittingParameter(parameters, IntegerParameter.class, getStringVal("paramname"));
		Double d=null;
		if(param != null) {
			d=param.getValue();
		}else if(param2!=null){
			d=param2.getValue().doubleValue();
		}
		if(d!=null) {
			ScriptEngineManager mgr = new ScriptEngineManager();
		    ScriptEngine engine = mgr.getEngineByName("JavaScript");
		    String foo = getStringVal("mathFunction");
		    foo=foo.replace("x", d.toString());
		    try {
		    	System.out.println(engine.eval(foo));
		    	System.out.println(engine.eval(foo).getClass());
				double x=(double) engine.eval(foo);
				if(x<getDoubleVal("min")) {
					x=getDoubleVal("min");
				}
				if(x>getDoubleVal("max")) {
					x=getDoubleVal("max");
				}
				((DoubleParameter)getParameter("output")).setValue(x);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}else {
			((DoubleParameter) getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
}
