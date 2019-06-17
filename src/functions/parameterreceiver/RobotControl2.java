package functions.parameterreceiver;

import java.awt.Image;
import java.io.File;
import java.util.Map;
import java.util.function.Function;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import com.google.common.reflect.Parameter;

import bluetooth.BluetoothHandler;
import database.ImageHandler;
import diagramming.components.ParameterReceiverPanel;
import functions.RepresentationIcon;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class RobotControl2 extends ParameterizedObject implements ParameterReceiver, RepresentationIcon {
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
	
	public RobotControl2(Boolean empty) {
		
	}
	
	public RobotControl2() {
		super(new FileParameter("soundfile", new File("res/sounds/alarm1.wav")));
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		BluetoothHandler bt=BluetoothHandler.getInstance();
		if(bt.isConnected()) {
			bt.sendMessage("TEST");
			System.out.println("TESTING");
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
		if (!(obj instanceof RobotControl2))
			return false;
		RobotControl2 other = (RobotControl2) obj;
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
}
