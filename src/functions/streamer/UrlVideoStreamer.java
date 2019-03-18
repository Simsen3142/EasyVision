package functions.streamer;

import java.awt.Image;

import database.ImageHandler;
import parameters.IntegerParameter;
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
		this.addParameters(new StringParameter("url",""));
	}
	
	public UrlVideoStreamer(Boolean empty) {}
	
	@Override
	public MatStreamer start() {
		getParameter("url").setOnChange((newVal)->{
			super.setResource(newVal);
			super.stop();
			super.start();
			return null;
		});
		super.setResource(getParameter("url").getValue());

		System.out.println(getResource());

		return super.start();
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/urlstreamer.png");
		return img;
	}
}
