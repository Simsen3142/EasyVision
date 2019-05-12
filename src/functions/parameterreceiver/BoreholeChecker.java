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

import com.google.common.reflect.Parameter;

import database.ImageHandler;
import diagramming.components.ParameterReceiverPanel;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.FileParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class BoreholeChecker extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {
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
	
	public BoreholeChecker(Boolean empty) {
		
	}
	
	public BoreholeChecker() {
		super(new IntegerParameter("mincount",3,0,20),
				new IntegerParameter("maxcount",3,0,20)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters) {
		try {
			IntegerParameter countp=(IntegerParameter) parameters.get("count");
			int c=countp.getValue();
			
			System.out.println(c);
			
			if(c<getIntVal("mincount") || c > getIntVal("maxcount")){
				for(SoundPlayer soundPlayer:getParameterReceiverWhichExtend(SoundPlayer.class)) {
					Map<String, ParameterObject> paramO=new HashMap<String, ParameterObject>();
					paramO.put("playsound", null);
					soundPlayer.onParameterReceived(paramO);
				}
			}
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
		if (!(obj instanceof BoreholeChecker))
			return false;
		BoreholeChecker other = (BoreholeChecker) obj;
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
}
