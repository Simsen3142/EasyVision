package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;
import java.util.function.Function;

import communication.TwoWayConnection;
import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;
import database.ImageHandler;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.SerialPortParameter;
import parameters.StringParameter;
import parameters.group.ConnectionParameterGroup;

public class CommBooleanSender extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6795502668594288334L;
	private int id=System.identityHashCode(this);
	
	private boolean lastb=true;


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
	
	public CommBooleanSender(Boolean empty) {
		
	}
	
	public CommBooleanSender() {
		super(
			new ConnectionParameterGroup("connection"),
			new StringParameter("prefix", ""),
			new StringParameter("suffix", ""),
			new BooleanParameter("newline",true)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		boolean b=getFirstFittingParameter(parameters, BooleanParameter.class).getValue();
		ConnectionParameterGroup paramgr=((ConnectionParameterGroup)getParameter2("connection"));
		TwoWayConnection<?> com=paramgr.getConnection();
		if(com!=null&&com.isConnected() && lastb!=b) {
			lastb=b;
			String s=getStringVal("prefix")+(b?"1":"0")+getStringVal("suffix")+(getBoolVal("newline")?"\n":"");
			com.sendText(s);
		}
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
		if (!(obj instanceof CommBooleanSender))
			return false;
		CommBooleanSender other = (CommBooleanSender) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return null;
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
}
