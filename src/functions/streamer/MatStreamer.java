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
import main.MatSender;
import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public abstract class MatStreamer extends MatSender implements RepresentationIcon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6910437341294708314L;
	private transient Thread streamThread;
	private Object resource;
	private transient static Set<MatStreamer> videoStreamers = Collections.synchronizedSet(new HashSet<MatStreamer>());
	private transient Mat mat;

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
	 * @param streamThread the streamThread to set
	 */
	public void setStreamThread(Thread streamThread) {
		this.streamThread = streamThread;
	}
	
	protected MatStreamer() {}
	
	public MatStreamer(Object resource) {
		super(
			new ParameterGroup("size",
				new BooleanParameter("change", false),
				new IntegerParameter("width", 200, 150, 1920),
				new IntegerParameter("height", 180, 150, 1080)
			)
		);
		this.resource=resource;
		initStreamThread();
	}
	
	public MatStreamer(String resource) {
		this((Object)resource);
	}
	
	public MatStreamer(int resource) {
		this((Object)resource);
	}
	
	public MatStreamer start() {
		if(videoStreamers.add(this)) {
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
			return this;
		}else {
			return getVideoStreamer(this);
		}
	}
	
	@Override
	public void stop() {
		videoStreamers.remove(this);
		if(streamThread!=null&&streamThread.isAlive()) {
			streamThread.interrupt();
		}
	}
	
	public MatStreamer getVideoStreamer(MatStreamer streamer) {
		for(MatStreamer str:videoStreamers) {
			if(str.equals(streamer)) {
				return str;
			}
		}
		
		return null;
	}
	
	protected abstract void initStreamThread();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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