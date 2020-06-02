package functions.parameterreceiver;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import com.google.common.reflect.Parameter;

import database.ImageHandler;
import diagramming.components.ParameterReceiverPanel;
import functions.RepresentationIcon;
import main.MatReceiver;
import main.MatSender;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.CollectionParameter;
import parameters.DoubleParameter;
import parameters.FileParameter;
import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.group.ParameterGroup;
import parameters.group.RectangleParameterGroup;
import sensors.lidar.LidarMessage;

public class CoordinateInRectChecker extends ParameterRepresenter<BooleanParameter> implements ParameterReceiver, MatReceiver, RepresentationIcon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4442826170242664136L;
	private int id=System.identityHashCode(this);
	private static double radius=12000;
	
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
	
	public CoordinateInRectChecker(Boolean empty) {
		
	}
	
	public CoordinateInRectChecker() {
		super(new RectangleParameterGroup("rect", 
			new DoubleParameter("x", 0,-radius,radius),
			new DoubleParameter("y", 0,-radius,radius),
			new DoubleParameter("width", radius*2,0,radius*2),
			new DoubleParameter("height", radius*2 ,0,radius*2)
			),
			new ParameterGroup("output", 
				new BooleanParameter("inside", false)
			)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		CollectionParameter param = getFirstFittingParameter(parameters, CollectionParameter.class);
		if (param != null && param.getValue() != null) {
			RectangleParameterGroup rectGr=((RectangleParameterGroup)getAllParameters().get("rect"));
			Rect rect=rectGr.getRect((int)radius*2, (int)radius*2);
			rect=new Rect((int)(rect.x-radius),(int)(rect.y-radius), rect.width, rect.height);
			boolean found=false;
			ArrayList<LidarMessage> msgs=new ArrayList<LidarMessage>((Collection<LidarMessage>)param.getValue());
			for(LidarMessage msg:msgs) {
				if(checkIfIn(msg,rect)) {
					found=true;
					break;
				}
			}
			((BooleanParameter)getParameter("output_inside")).setValue(found);
			sendParameters();
		}
	}
	
	private boolean checkIfIn(LidarMessage msg, Rect rect) {
		int x=rect.x;
		int y=rect.y;
		return ((msg.getX()>=x) && msg.getX()<=(x+rect.width))
				&& ((msg.getY()>=y) && msg.getY()<=(y+rect.height));
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
		if (!(obj instanceof CoordinateInRectChecker))
			return false;
		CoordinateInRectChecker other = (CoordinateInRectChecker) obj;
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
		return new BooleanParameter("output_inside", null,false);
	}
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		RectangleParameterGroup rectGr=((RectangleParameterGroup)getAllParameters().get("rect"));
		rectGr.getMatReceiver().onReceive(matIn, sender);
	}
}
