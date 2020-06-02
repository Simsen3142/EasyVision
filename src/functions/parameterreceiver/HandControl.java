package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;
import java.util.function.Function;

import communication.serial.SerialHandler;
import database.ImageHandler;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class HandControl extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6904881506669829035L;

	private int id=System.identityHashCode(this);
	
	private transient volatile boolean alreadyPlaying=false;
	int lastamt=0;


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
	
	public HandControl(Boolean empty) {
		
	}
	
	public HandControl() {
		super();
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		int amt=getFirstFittingParameter(parameters, IntegerParameter.class).getValue();
		if(		SerialHandler.getInstance().isConnected() && amt!=lastamt) {
			lastamt=amt;
			SerialHandler.getInstance().sendMessageToAll(amt+"\n");
			System.out.println("SENDING: "+amt);
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
		if (!(obj instanceof HandControl))
			return false;
		HandControl other = (HandControl) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/speaker.png");
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
}
