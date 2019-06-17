package functions.parameterreceiver;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import database.ImageHandler;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;

public class LogicalOr extends MultiParameterReceiver<BooleanParameter> implements ParameterReceiver, RepresentationIcon {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -530271305000414492L;


	public LogicalOr(Boolean empty) {}
	
	public LogicalOr() {
	}
	
	@Override
	public BooleanParameter onParametersReceived(List<Map<String, ParameterObject>> parameters) {
		boolean ret=false;
		
		List<Parameter<?>> param=new ArrayList<>();
		
		for(Map<String, ParameterObject> params:parameters) {
			BooleanParameter bp=getFirstFittingParameter(params, BooleanParameter.class);
			if(bp!=null)
				param.add(bp);
		}

		for(BooleanParameter boolParam:getParameterWhichExtend(BooleanParameter.class, param)) {
			if(boolParam.getValue()!=null && boolParam.getValue()==true) {
				ret=true;
				break;
			}
		}
		
		return new BooleanParameter("output", ret);
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/or.png");
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
		return new BooleanParameter("output", false, false);
	}

}
