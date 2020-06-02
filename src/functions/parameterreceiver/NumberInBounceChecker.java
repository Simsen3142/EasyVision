package functions.parameterreceiver;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import database.ImageHandler;
import diagramming.components.ParameterReceiverPanel;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class NumberInBounceChecker extends ParameterRepresenter<BooleanParameter> implements ParameterReceiver, RepresentationIcon {
	private int id=System.identityHashCode(this);
	
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
	
	public NumberInBounceChecker(Boolean empty) {
		
	}
	
	public NumberInBounceChecker() {
		super(new IntegerParameter("mincount",0,-100000,100000),
				new IntegerParameter("maxcount",0,-100000,100000)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		try {
			NumberParameter<?> countp=getFirstFittingParameter(parameters, NumberParameter.class);
			double c=countp.getValue().doubleValue();
			
			boolean inBounce=c>=getIntVal("mincount") && c <= getIntVal("maxcount");
			((Parameter<?>)getParameter("output")).setValue2(inBounce);
			
			sendParameters();
		}catch (Exception e) {
			e.printStackTrace();
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
		if (!(obj instanceof NumberInBounceChecker))
			return false;
		NumberInBounceChecker other = (NumberInBounceChecker) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public Image getRepresentationImage() {
		return null;//ImageHandler.getImage("res/icons/speaker.png");
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
	@Override
	public parameters.Parameter<?> getRepresentationParameter() {
		return new BooleanParameter("output", null,false);
	}
}
