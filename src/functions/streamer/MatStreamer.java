package functions.streamer;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import functions.RepresentationIcon;
import functions.Startable;
import main.MatSender;
import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.group.ParameterGroup;

public abstract class MatStreamer extends MatSender implements RepresentationIcon, Startable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6910412345708314L;
	private transient Thread streamThread;
	private Object resource;
//	private transient static Set<MatStreamer> videoStreamers = Collections.synchronizedSet(new HashSet<MatStreamer>());

	public boolean isStarted() {
		return streamThread!=null && streamThread.isAlive();
	}
	
	/**
	 * @return the resource
	 */
	public Object getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Object resource) {
		this.resource = resource;
	}

	/**
	 * @return the streamThread
	 */
	public Thread getStreamThread() {
		return streamThread;
	}

	/**
	 * @param t the streamThread to set
	 */
	public void setStreamThread(Thread t) {
		this.streamThread = t;
	}
	
	protected MatStreamer() {}
	
	public MatStreamer(Object resource) {
		super(
			new ParameterGroup("size",
				new BooleanParameter("change", false),
				new IntegerParameter("width", 200, 50, 5000),
				new IntegerParameter("height", 180, 50, 5000)
			),
			new BooleanParameter("color", true)
		);
		this.resource=resource;
		initStreamThread();
	}
	
	protected MatStreamer(ParameterObject...parameters) {
		super(parameters);
		initStreamThread();
	}
	
	public MatStreamer(String resource) {
		this((Object)resource);
	}
	
	public MatStreamer(int resource) {
		this((Object)resource);
	}
	
	public void start() {
//		if(videoStreamers.add(this)) {
			if(streamThread==null || !streamThread.isAlive() || streamThread.isInterrupted()) {
				new Thread(()->{
					if(streamThread!=null) {
						while(streamThread.isAlive()) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					initStreamThread();
					streamThread.start();
				}).start();
			}
			return;
//		}else {
//			return getVideoStreamer(this);
//		}
	}
	
	@Override
	protected void sendMat(Mat mat) {
		if(!getBoolVal("color")) {
			if(mat.channels()>1)
				Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		}
		super.sendMat(mat);
	}
	
	@Override
	public void stop() {
//		videoStreamers.remove(this);
		if(streamThread!=null&&streamThread.isAlive()) {
			streamThread.interrupt();
		}
	}
	
//	public MatStreamer getVideoStreamer(MatStreamer streamer) {
//		for(MatStreamer str:videoStreamers) {
//			if(str.equals2(streamer)) {
//				return str;
//			}
//		}
//		
//		return null;
//	}
	
	protected abstract void initStreamThread();
	

	public boolean equals2(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MatStreamer))
			return false;
		MatStreamer other = (MatStreamer) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			if(img!=null)
				onReceive.apply(img);
		}).start();
	}
}