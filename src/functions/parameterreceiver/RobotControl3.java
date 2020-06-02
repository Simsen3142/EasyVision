package functions.parameterreceiver;

import java.awt.Image;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import communication.bluetooth.BluetoothHandler;
import communication.messages.JSONMessage;
import communication.messages.MotorMessage;
import database.ImageHandler;
import functions.RepresentationIcon;
import functions.Startable;
import main.ParameterReceiver;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.StringParameter;
import parameters.group.ParameterGroup;

public class RobotControl3 extends MultiParameterReceiver<Parameter<?>> implements ParameterReceiver, RepresentationIcon, Startable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3258356714886282169L;
	private int id=System.identityHashCode(this);
	private boolean started;
	private transient String lastCommand="";
	
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
	
	public RobotControl3(Boolean empty) {
		
	}
	
	public RobotControl3() {
		super(
			new ParameterGroup("gear", 
				new ParameterGroup("g1", 
					new IntegerParameter("min", 0,-255,255),
					new IntegerParameter("max", 0,-255,255)
				),
				new ParameterGroup("g2", 
					new IntegerParameter("min", -100,-255,255),
					new IntegerParameter("max", -60,-255,255)
				),
				new ParameterGroup("g3", 
					new IntegerParameter("min", 80,-255,255),
					new IntegerParameter("max", 100,-255,255)
				),
				new ParameterGroup("g4", 
					new IntegerParameter("min", 120,-255,255),
					new IntegerParameter("max", 150,-255,255)
				),
				new ParameterGroup("g5", 
					new IntegerParameter("min", 160,-255,255),
					new IntegerParameter("max", 180,-255,255)
				)
			)
		);
	}

	@Override
	public Parameter<?> onParametersReceived(List<Map<String, ParameterObject>> parameterMaps) {
		DoubleParameter xDist = null;
		DoubleParameter yDist = null;
		IntegerParameter amt  = null;
		
		for(Map<String, ParameterObject> parameterMap:parameterMaps) {
			try {
				if(xDist==null)
					xDist=(DoubleParameter) parameterMap.get("outputX");
			}catch (Exception e) {
			}
			try {
				if(yDist==null)
					yDist=(DoubleParameter) parameterMap.get("outputY");
			}catch (Exception e) {
			}
			try {
				if(amt==null)
					amt=(IntegerParameter) parameterMap.get("output");
			}catch (Exception e) {
			}
		}
		
		if(xDist!=null && yDist!=null && isStarted()) {
			int fingers=amt==null?1:amt.getValue();
			double xd=xDist.getValue();
			double yd=(yDist.getValue()+1)/2.0;
			BluetoothHandler bt=BluetoothHandler.getInstance();
			if(bt.isConnected()) {
				if(fingers<1 || fingers>5) {
					sendMotorStopMessage(bt);
				}else{
					//BACK
					int min=((IntegerParameter)getParameter("gear_g"+fingers+"_min")).getValue();
					int max=((IntegerParameter)getParameter("gear_g"+fingers+"_max")).getValue();
					double diff=max-min;
					
					int m1;
					int m2;
					if(min<=0 && max<=0) {
						yd=1-yd;
					}
					m1= max-(int)(diff*yd);
					

					
					m2=m1;
					
					if(xd<0) {
						m1=(int)(m1*(1.0+xd));
					}else {
						m2=(int)(m2*(1.0-xd));
					}
					sendMotorDriveMessage(bt,m2,m1);
				}
			}
		}
		sendParameters();
		return new StringParameter("command", lastCommand);
	}
	
	public void sendMotorDriveMessage(BluetoothHandler bt, int m1, int m2) {
		String s=JSONMessage.fromMessageToJSON(new MotorMessage.MotorFrequencyMessage(m1,m2));
		System.out.println("SENDING: "+s);
		lastCommand=s;
		bt.sendMessage(s);
	}
	
	public void sendMotorStopMessage(BluetoothHandler bt) {
		String s=JSONMessage.fromMessageToJSON(new MotorMessage.MotorStopMessage());
		System.out.println("SENDING: "+s);
		bt.sendMessage(s);
		lastCommand=s;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RobotControl3))
			return false;
		RobotControl3 other = (RobotControl3) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/robotcontrol.png");
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
		return new StringParameter("command", "",false);
	}
	
	@Override
	public void stop() {
		if(BluetoothHandler.getInstance().isConnected())
			sendMotorStopMessage(BluetoothHandler.getInstance());
		started=false;
	}
	@Override
	public void start() {
		started=true;		
	}
	@Override
	public boolean isStarted() {
		return started;
	}
	
}
