package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;

import arduino.ArduinoHandler;
import arduino.csv.CsvConverter;
import arduino.messages.JSONParser;
import arduino.messages.MotorMessage;
import arduino.serial.TwoWaySerialComm;
import database.ImageHandler;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;
import parameters.group.ParameterGroup;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.Parameter;

public class RobotControl extends ParameterizedObject implements ParameterReceiver, RepresentationIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7935195310384504522L;
	private static volatile Image img;
	private transient ArduinoHandler arduinoHandler;
	private transient long lastTimeSent=0;
	public RobotControl(Boolean empty) {}
	
	public RobotControl() {
		super(
			new ParameterGroup("robotparams",
				new IntegerParameter("maxstepspersec",2000,1,10000)
			),
			new ParameterGroup("send",
				new IntegerParameter("timetillsend",100,0,2000),
				new DoubleParameter("kderror",1,0.1,100), //steps per pixel error
				new DoubleParameter("kdangle",1,0.1,100) //steps per degree angle
			)
		);
	}
	
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters) {
		if(arduinoHandler==null)
			arduinoHandler=ArduinoHandler.getInstance();
		
		String txt=JSONParser.toJSON(new MotorMessage.MotorFrequencyMessage(300, 200));
		System.out.println(txt);
		
		if(arduinoHandler.getSerialComm().isConnected()) {
			IntegerParameter param1=((IntegerParameter)parameters.get("output_error"));
			IntegerParameter param2=((IntegerParameter)parameters.get("output_angle"));
			StringParameter param3=((StringParameter)parameters.get("output_turn"));

			int[] motorSpeed=handleMotorControl(param1.getValue(),param2.getValue(),param3.getValue());
			String text=JSONParser.toJSON(new MotorMessage.MotorFrequencyMessage(motorSpeed[0], motorSpeed[1]));
			
			long now=System.currentTimeMillis();
			
			if(now-lastTimeSent>getIntVal("send_timetillsend")) {
				lastTimeSent=now;
				arduinoHandler.getSerialComm().getWriter().doWrite(text);
			}
		}
		
		// kreis! mittelpunkt
		
		// rechteck! 
	}
	
	/**
	 * @param error
	 * @param angle
	 * @param turn
	 * @return the speed for the left and the right motor
	 */
	private int[] handleMotorControl(int error, int angle, String turn) {
		int[] ret=new int[2];
		
		double kderror=getDoubleVal("send_kderror");
		double kdangle=getDoubleVal("send_kdangle");
		int maxstepspersec=getIntVal("robotparams_maxstepspersec");
				
		double d1=error*kderror+angle*kdangle;
		
		int val1=maxstepspersec,val2=maxstepspersec;
		if(d1<0) {
			val1+=d1;
		}else {
			val2-=d1;
		}
		
		ret[1]=val1;
		ret[0]=val2;

		return ret;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/robotcontrol.png");
		return img;
	}
}
