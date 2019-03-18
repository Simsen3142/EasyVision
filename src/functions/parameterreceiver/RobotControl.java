package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;

import database.ImageHandler;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.Parameter;

public class RobotControl extends ParameterizedObject implements ParameterReceiver, RepresentationIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7935195310384504522L;
	private static volatile Image img;

	public RobotControl(Boolean empty) {}
	
	public RobotControl() {
		super();
	}
	
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters) {
		Parameter param1=((Parameter)parameters.get("output_angle"));
		Parameter param3=((Parameter)parameters.get("output_turn"));
		Parameter param2=((Parameter)parameters.get("output_error"));
		
		System.out.println(param1.getName()+"\t"+param1.getValue());
		System.out.println(param2.getName()+"\t"+param2.getValue());
		System.out.println(param3.getName()+"\t"+param3.getValue());
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/robotcontrol.png");
		return img;
	}
}
