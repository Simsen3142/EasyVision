package functions.parameterreceiver;

import java.awt.Image;
import java.awt.image.RescaleOp;
import java.util.Map;
import java.util.function.Function;

import communication.messages.ArmMessage;
import communication.messages.JSONMessage;
import communication.messages.MotorMessage;
import communication.messages.UltraSonicMessage;
import communication.messages.MotorMessage.MotorStepsDoneMessage;
import communication.messages.UltraSonicMessage.UltraSonicResponse;
import communication.serial.SerialHandler;
import database.ImageHandler;
import functions.RepresentationIcon;
import functions.Startable;
import functions.UniqueFunction;
import main.ParameterReceiver;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.StringParameter;
import parameters.group.ParameterGroup;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.IntegerParameter;

public class RobotControl extends ParameterizedObject implements ParameterReceiver, Startable, RepresentationIcon{
	
	public static enum ControlState{
		LN_DEFAULT, LN_TURN_L, LN_TURN_R,
		LN_OBSTACLE_TURN1,LN_OBSTACLE_DRIVESIDE1, LN_OBSTACLE_TURN2, LN_OBSTACLE_DRIVEFWD, LN_OBSTACLE_TURN3, LN_OBSTACLE_DRIVESIDE2, LN_OBSTACLE_TURN4//,
		
//		RESC_SEARCH, RESC_PICKUP, RESC_RELEASE, RESC_TAKEBACK
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7935195310384504522L;
	private static volatile Image img;
	private transient SerialHandler serialHandler;
	private transient long lastTimeSent=0;
	private transient long lastTimeCheckUltraSonic=0;
	private transient ControlThread cThread;
	private transient OnReceiveFunction onReceiveFunction;
	private volatile transient ControlState state=ControlState.LN_DEFAULT;
	private static boolean DOUSESERIAL=false;
	private volatile transient int[] stepsPSec=new int[2];
	private transient volatile String lastSent="}";
	private int id=System.identityHashCode(this);
	
//	private volatile int ballRadius;
//	private volatile int ballErrorY;
	
	public RobotControl(Boolean empty) {}
	
	public RobotControl() {
		super(
			new BooleanParameter("useserial",DOUSESERIAL),
			new ParameterGroup("robotparams",
				new IntegerParameter("maxstepspersec",200,1,10000)
			),
			new ParameterGroup("send",
				new IntegerParameter("timetillsend",200,0,2000)
			),
			new ParameterGroup("line", 
				new DoubleParameter("kderror",3,0.1,100), //steps per pixel error
				new DoubleParameter("kdangle",2,0.1,100) //steps per degree angle
			),
//			new ParameterGroup("balls", 
//				new DoubleParameter("kderror", 4,0.1,100),
//				new IntegerParameter("ballradius4pickup", 50,10,500)
//			),
			new ParameterGroup("controldata",
				new IntegerParameter("steps4fullstepperturn", 200,1,1000),
				new IntegerParameter("steps4fullrobotturn", 2300,1,50000),
				new DoubleParameter("tireDiameterMM", 68.8,10,500)
			),
			new ParameterGroup("ultrasonic",
				new IntegerParameter("timetillsend",1000,0,10000),
				new DoubleParameter("distance4obstacleCM",40,0,500),
				new ParameterGroup("controls",
					new BooleanParameter("dodgeleft",true),
					new IntegerParameter("distanceToDriveSideCM",50,0,200),
					new IntegerParameter("distanceToDriveForwardCM",150,0,200)
					
//					new IntegerParameter("distancetoball4pickup", 3,0,20)
				)
			)
		);
	}
	
	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		if(state==null)
			state=ControlState.LN_DEFAULT;
		//DATA FROM LINEDETECTION############################################################################################
//		if(state.ordinal()<ControlState.RESC_SEARCH.ordinal())
		{
			IntegerParameter error=((IntegerParameter)parameters.get("output_lnerrorx"));
			
			if(error!=null) {
				IntegerParameter angle=((IntegerParameter)parameters.get("output_angle"));
				StringParameter turn=((StringParameter)parameters.get("output_turn"));
	
				int[] motorSpeed=handleMotorControlLine(error.getValue(),angle.getValue(),turn.getValue());
				
				if(turn.getValue().toLowerCase().contains("left")) {
					state=ControlState.LN_TURN_L;
				}else if(turn.getValue().toLowerCase().contains(("right"))){
					state=ControlState.LN_TURN_R;
				}else {
					stepsPSec=motorSpeed;
				}
			}
		}
		//DATA FROM LINEDETECTION############################################################################################
		
		//DATA FROM BALLDETECTION############################################################################################
//		else {
//			IntegerParameter errorX=((IntegerParameter)parameters.get("output_xerror"));
//			if(errorX!=null) {
//				IntegerParameter errorY=((IntegerParameter)parameters.get("output_yerror"));
//				IntegerParameter radius=((IntegerParameter)parameters.get("output_radius"));
//				ballRadius=radius.getValue();
//				ballErrorY=errorY.getValue();
//				
//				int[] motorSpeed=handleMotorControlBall(errorX.getValue(),radius.getValue());
//				stepsPSec=motorSpeed;
//			}
//		}
		//DATA FROM BALLDETECTION############################################################################################
		
		// kreis! mittelpunkt
		
		// rechteck! 
	}
	
	private int getMaxSpeed() {
		return getIntVal("robotparams_maxstepspersec");
	}
	
//	private int[] handleMotorControlBall(Integer xError, Integer radius) {
//		int[] ret=new int[2];
//		
//		double kderror=getDoubleVal("balls_kderror");
//		int maxstepspersec=getMaxSpeed();
//		double d1=xError*kderror;
//
//		int val1=maxstepspersec,val2=maxstepspersec;
//		if(d1<0) {
//			val2+=d1;
//			
//		}else {
//			val1-=d1;
//		}
//		
//		val1=constraint(val1,-maxstepspersec,maxstepspersec);
//		val2=constraint(val2,-maxstepspersec,maxstepspersec);
//		
//		ret[0]=val1;
//		ret[1]=val2;
//
//		return ret;
//	}
	
	/**
	 * @param error
	 * @param angle
	 * @param turn
	 * @return the speed for the left and the right motor
	 */
	private int[] handleMotorControlLine(int error, int angle, String turn) {
		int[] ret=new int[2];
		
		double kderror=getDoubleVal("line_kderror");
		double kdangle=getDoubleVal("line_kdangle");
		int maxstepspersec=getMaxSpeed();
				
		double d1=error*kderror+angle*kdangle;
		
		int val1=maxstepspersec,val2=maxstepspersec;
		if(d1<0) {
			val2+=d1;
			
		}else {
			val1-=d1;
		}
		
		val1=constraint(val1,-maxstepspersec,maxstepspersec);
		val2=constraint(val2,-maxstepspersec,maxstepspersec);
		
		ret[0]=val1;
		ret[1]=val2;

		return ret;
	}
	
	private int constraint(int val, int min, int max) {
		if(val<min)
			val=min;
		else if(val>max)
			val=max;
		
		return val;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/robotcontrol.png");
		return img;
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
	
	public class ControlThread extends Thread {
		
		@Override
		public void run() {
			while(!isInterrupted()) {
				handleControl();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
		
		private void handleControl() {
			long now=System.currentTimeMillis();
			
			if(state==null) {
				state=ControlState.LN_DEFAULT;
			}
			
			if(serialHandler.isConnected() || !DOUSESERIAL) {
				switch(state) {
					case LN_DEFAULT:{
						if(now-lastTimeSent>getIntVal("send_timetillsend")) {
							lastTimeSent=now;
							
							setSpeed(stepsPSec[0], stepsPSec[1]);
						}
						
						if(now-lastTimeCheckUltraSonic>getIntVal("ultrasonic_timetillsend")) {
							lastTimeCheckUltraSonic=now;
							
							writeSerial(new UltraSonicMessage.UltraSonicRequest());
						}
						break;
					}case LN_TURN_L:{
						long time=turnByDegree(-45,true);
						doSleep(time+100);
						state=ControlState.LN_DEFAULT;
						setSpeed(100, 100);
						doSleep(1000);
						break;
					}case LN_TURN_R:{
						long time=turnByDegree(45,true);
						doSleep(time+100);
						state=ControlState.LN_DEFAULT;
						setSpeed(100, 100);
						doSleep(1000);
						break;
					}
//					}case RESC_SEARCH:{
//						if(now-lastTimeSent>getIntVal("send_timetillsend")) {
//							lastTimeSent=now;
//							
//							int radiusEnd=getIntVal("balls_ballradius4pickup");
//							state=ControlState.RESC_PICKUP;
//
//							if(ballRadius>=radiusEnd) {
//								sendStop();
//								state=ControlState.RESC_PICKUP;
//							}
//							
//							
//							setSpeed(stepsPSec[0], stepsPSec[1]);
//						}
//						break;
//						
//					}case RESC_PICKUP:{
//						sendStop();
//						writeSerial(new ArmMessage.PickUpMessage());
//						doSleep(7000);
//						state=ControlState.RESC_TAKEBACK;
//						break;
//					}case RESC_TAKEBACK:{
//						//TODO: testen
//						sendStart();
//					}case RESC_RELEASE:{
//						sendStop();
//						writeSerial(new ArmMessage.ReleaseMessage());
//						doSleep(7000);
//						
//						int steps=200;
//						long t=setStepsToDoNSpeed(-getMaxSpeed(), -getMaxSpeed(), steps, steps, true);
//						doSleep(t+100);
//						
//						t=turnByDegree(180, true);
//						doSleep(t+100);
//
//						state=ControlState.RESC_SEARCH;
//						break;
//					}
						
				}
					
			}else {
				if(now-lastTimeSent>getIntVal("send_timetillsend")) {
					lastTimeSent=now;
					System.out.println("NOT CONNECTED");
				}
			}
		}
	}
	
	private void sendAgain() {
		writeSerial(lastSent);
	}
	
	private void writeSerial(String s) {
		lastSent=s;
		if(!lastSent.contains("}")) {
			lastSent="}";
		}
		
		
		DOUSESERIAL=getBoolVal("useserial");
		if(DOUSESERIAL)
			serialHandler.sendMessageToAll(s);
		else
			System.out.println("SENDING: "+s);
	}
	
	private void writeSerial(JSONMessage msg) {
		String text=JSONMessage.fromMessageToJSON(msg);
		writeSerial(text);
	}
	
	private void sendStop() {
		writeSerial(new MotorMessage.MotorStopMessage());
	}
	
	private void sendStart() {
		writeSerial(new MotorMessage.MotorStartMessage());
	}
	
	private void setSpeed(int m1, int m2) {
		writeSerial(new MotorMessage.MotorFrequencyMessage(m1, m2));
	}
	
	private void setStepsToDoWithSpeed(int s1, int s2, int m1, int m2) {
		setStepsToDoNSpeed(s1,s2,m1,m2,false);
	}
			
	
	private long setStepsToDoNSpeed(int speed1, int speed2, int steps1, int steps2, boolean calcTime) {
		setSpeed(speed1,speed2);
		setStepsToDo(steps1,steps2);
		
		if(calcTime) {
			long c1=1000*steps1/speed1;
			long c2=1000*steps2/speed2;
			
			
			if(c1>c2)
				return c1;
			else
				return c2;
		}
		
		return 0;
	}
	
	private void setStepsToDo(int m1, int m2) {
		writeSerial(new MotorMessage.MotorStepMessage(m1, m2));
	}
	
	private void turnByDegree(int degree) {
		turnByDegree(degree,false);
	}
	
	private long turnByDegree(int degree, boolean calcTime) {
		if(degree==0)
			return 0;
		

		int steps4turn=getIntVal("controldata_steps4fullrobotturn");
		
		int speed=getMaxSpeed()/2;
		if(degree<0) {
			speed*=-1;
			degree*=-1;
		}
		int steps=steps4turn*degree/360;
		
		setSpeed(-speed,speed);
		
		return 1000*steps/100;
		
	}
	
	private double getWheelCircumference() {
		double diameter=getDoubleVal("controldata_tireDiameterMM");
		return diameter*Math.PI;
	}
	
	private int cmToSteps(double cm) {
		int steps4Turn=getIntVal("controldata_steps4fullstepperturn");
		double circumference=getWheelCircumference()/10;
		
		return (int) (steps4Turn*cm/circumference);
	}
	
	private class OnReceiveFunction implements Function<String, Void> {
		@Override
		public Void apply(String t) {
			JSONMessage msg=JSONMessage.fromJSONtoMessage(t, JSONMessage.class);
			if(msg.getType()>=10&&msg.getType()<20) {
				switch(msg.getType()) {
					case 14:{
						MotorStepsDoneMessage mmsg=(MotorStepsDoneMessage)JSONMessage.fromJSONtoMessage(t, MotorStepsDoneMessage.class);
						if(mmsg.isDone()) {
							switch(state) {
								case LN_OBSTACLE_TURN1:{
									state=ControlState.LN_OBSTACLE_DRIVESIDE1;
									int steps=cmToSteps(getIntVal("ultrasonic_controls_distanceToDriveSideCM"));
									performDodgeStep((x)->setStepsToDoNSpeed(getMaxSpeed(), getMaxSpeed(), steps, steps, true));
									break;
								}
								case LN_OBSTACLE_DRIVESIDE1:{
									state=ControlState.LN_OBSTACLE_TURN2;
									int degree=getBoolVal("ultrasonic_controls_dodgeleft")?-90:90;
									performDodgeStep((x)->turnByDegree(degree,true));
									break;
								}
								case LN_OBSTACLE_TURN2:{
									state=ControlState.LN_OBSTACLE_DRIVEFWD;
									int steps=cmToSteps(getIntVal("ultrasonic_controls_distanceToDriveForwardCM"));
									performDodgeStep((x)->setStepsToDoNSpeed(getMaxSpeed(), getMaxSpeed(), steps, steps, true));
									break;
								}
								case LN_OBSTACLE_DRIVEFWD:{
									state=ControlState.LN_OBSTACLE_TURN3;
									int degree=getBoolVal("ultrasonic_controls_dodgeleft")?-90:90;
									performDodgeStep((x)->turnByDegree(degree,true));
									break;
								}
								case LN_OBSTACLE_TURN3:{
									state=ControlState.LN_OBSTACLE_DRIVESIDE2;
									int steps=cmToSteps(getIntVal("ultrasonic_controls_distanceToDriveSideCM"));
									performDodgeStep((x)->setStepsToDoNSpeed(getMaxSpeed(), getMaxSpeed(), steps, steps, true));
									break;
								}
								case LN_OBSTACLE_DRIVESIDE2:{
									state=ControlState.LN_OBSTACLE_TURN4;
									int degree=getBoolVal("ultrasonic_controls_dodgeleft")?90:-90;
									performDodgeStep((x)->turnByDegree(degree,true));
									break;
								}
								case LN_OBSTACLE_TURN4:{
									state=ControlState.LN_DEFAULT;
									sendStart();
									break;
								}
								default:
									break;
							}
						}else {
							new Thread(()->{
								doSleep(100);
								writeSerial(new MotorMessage.MotorStepsDoneMessage(true));
							}).start();
						}
						
						break;
					}
				}
			}
			//ULTRASONIC############################################################################################
			else if(msg.getType()>=30&&msg.getType()<39) { 
				switch(msg.getType()) {
					case 31:{
						UltraSonicResponse usmsg=(UltraSonicResponse)JSONMessage.fromJSONtoMessage(t, UltraSonicResponse.class);
						double distance=usmsg.getDistance();
						
						if(distance<getDoubleVal("ultrasonic_distance4obstacleCM")) {
							state=ControlState.LN_OBSTACLE_TURN1;
							int degree=getBoolVal("ultrasonic_controls_dodgeleft")?90:-90;
							
							performDodgeStep((x)->turnByDegree(degree,true));
						}
						break;
					}
				}
			}
			//ULTRASONIC############################################################################################
			if(msg.getType()>=50&&msg.getType()<60) {
				switch(msg.getType()) {
					case 50: //RESEND MESSAGE
						sendAgain();
						break;
				}
			}

			
			return null;
		}
		
		private void performDodgeStep(Function<Void, Long> movefct) {
			new Thread(()->{
				System.out.println(state.name());
				doSleep(movefct.apply(null)+100);
				onReceiveFunction.apply(JSONMessage.fromMessageToJSON(new MotorStepsDoneMessage(true))); //NO INTENTION 4 KEEPING THIS THAT BAD
//				writeSerial(new MotorMessage.MotorStepsDoneMessage(true)); TODO: Uberlegen, ob mit Comm kommuniziert werden soll, oder auf Zeit vertraut wird
			}).start();
		}
		
	}
	
	private void doSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		
		state=ControlState.LN_DEFAULT;
		if(serialHandler==null) {
			serialHandler=SerialHandler.getInstance();
		}
		
		if(cThread==null || !cThread.isAlive()) {
			cThread=new ControlThread();
			cThread.start();
			
			if(onReceiveFunction==null)
				onReceiveFunction=new OnReceiveFunction();
//			serialHandler.getSerialComm().addOnReceive(onReceiveFunction);
		}
		
		sendStart();
	}
	
	@Override
	public void stop() {
		sendStop();

		if(cThread!=null && cThread.isAlive()) {
			cThread.interrupt();
//			serialHandler.getSerialComm().removeOnReceive(onReceiveFunction);
		}
	}

	@Override
	public boolean isStarted() {
		return cThread!=null && cThread.isAlive();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
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
		if (!(obj instanceof RobotControl))
			return false;
		RobotControl other = (RobotControl) obj;
		if (id != other.id)
			return false;
		return true;
	}

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
	
	
	
	
}
