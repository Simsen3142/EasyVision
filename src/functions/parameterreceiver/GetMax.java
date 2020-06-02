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
import parameters.DoubleParameter;
import parameters.Parameter;
import parameters.ParameterObject;

public class GetMax extends MultiParameterReceiver<DoubleParameter> implements ParameterReceiver,RepresentationIcon {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7430068273107733157L;

	public GetMax(Boolean empty) {}
	
	public GetMax() {
	}

	@Override
	public DoubleParameter onParametersReceived(List<Map<String, ParameterObject>> parameters) {
		double max=Double.MIN_VALUE;
		
		for(Map<String, ParameterObject> params:parameters) {
			DoubleParameter dp=getFirstFittingParameter(params, DoubleParameter.class);
			if(dp!=null)
				if(dp.getValue()>max) {
					max=dp.getValue();
				}
		}

		
		return new DoubleParameter("output", max);
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/and.png");
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
