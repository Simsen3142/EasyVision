package functions.streamer;

import java.awt.Image;

import database.ImageHandler;
import parameters.StringParameter;

public class UrlVideoStreamer extends VideoStreamer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8288408131440849859L;
	private static volatile Image img;

	public UrlVideoStreamer() {
		this("http://192.168.1.46:8080/video");
	}
	
	public UrlVideoStreamer(String url) {
		super(url);
		discardOldFrames=true;
		this.addParameters(new StringParameter("url",url));
	}
	
	public UrlVideoStreamer(Boolean empty) {}
	
	@Override
	public void start() {
		getParameter("url").setOnChange((newVal)->{
			super.setResource(newVal);
			super.stop();
			super.start();
			return null;
		});
		super.setResource(getParameter("url").getValue());

		System.out.println(getResource());
		super.start();
		return;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/urlstreamer.png");
		return img;
	}
}