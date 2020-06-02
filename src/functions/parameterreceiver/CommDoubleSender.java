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

public class CommDoubleSender extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6795502668594288334L;
	private int id=System.identityHashCode(this);
	
	double lastd=0;


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
	
	public CommDoubleSender(Boolean empty) {
		
	}
	
	public CommDoubleSender() {
		super(
			new ConnectionParameterGroup("connection"),
			new StringParameter("prefix", ""),
			new StringParameter("suffix", ""),
			new BooleanParameter("newline",true)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		double d=getFirstFittingParameter(parameters, DoubleParameter.class).getValue();
		ConnectionParameterGroup paramgr=((ConnectionParameterGroup)getParameter2("connection"));
		TwoWayConnection<?> com=paramgr.getConnection();
		if(com!=null&&com.isConnected() && d!=lastd) {
			lastd=d;
			String s=getStringVal("prefix")+d+getStringVal("suffix")+(getBoolVal("newline")?"\n":"");
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
		if (!(obj instanceof CommDoubleSender))
			return false;
		CommDoubleSender other = (CommDoubleSender) obj;
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
