package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;
import java.util.function.Function;

import database.ImageHandler;
import functions.RepresentationIcon;
import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class LogicalNot extends ParameterRepresenter<BooleanParameter> implements RepresentationIcon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5388256632277781874L;

	public LogicalNot(Boolean empty) {}
	
	public LogicalNot() {
	}
	
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters, ParameterizedObject sender) {
		BooleanParameter param=getFirstFittingParameter(parameters, BooleanParameter.class, getStringVal("paramname"));
		
		if(param!=null && param.getValue()!=null) {
			((BooleanParameter)getParameter("output")).setValue(!param.getValue());
		}else {
			((BooleanParameter)getParameter("output")).setValue(null);
		}
		sendParameters();
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/not.png");
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}

	@Override
	public Parameter<?> getRepresentationParameter() {
		return new BooleanParameter("output", null, false);
	}

}
