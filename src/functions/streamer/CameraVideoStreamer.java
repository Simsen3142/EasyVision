package functions.streamer;

import java.awt.Image;

import database.ImageHandler;
import parameters.IntegerParameter;

public class CameraVideoStreamer extends VideoStreamer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8056824878126427507L;
	private static volatile Image img;

	public CameraVideoStreamer() {
		this(0);
	}
	
	public CameraVideoStreamer(Boolean empty) {}
	
	public CameraVideoStreamer(int cameraNr) {
		super(cameraNr);
		this.addParameters(new IntegerParameter("cameranr",0,0,63));
	}
	
	@Override
	public void start() {
		getParameter("cameranr").setOnChange((newVal)->{
			super.setResource(newVal);
			super.stop();
			super.start();
			return null;
		});
		
		super.start();
		return;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/videostreamer.png");
		return img;
	}
}
