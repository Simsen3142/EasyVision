package recording;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import main.MatSender;
import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class VideoStreamer extends MatSender {
	private transient Thread streamThread;
	private transient VideoCapture camera;
	private Object resource;
	private static Set<VideoStreamer> videoStreamers =Collections.synchronizedSet(new HashSet<VideoStreamer>());
	
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
	
	/**
	 * @return the camera
	 */
	public VideoCapture getCamera() {
		return camera;
	}

	/**
	 * @param camera the camera to set
	 */
	public void setCamera(VideoCapture camera) {
		this.camera = camera;
	}

	public VideoStreamer(Object resource) {
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
	
	public VideoStreamer(String resource) {
		this((Object)resource);
	}
	
	public VideoStreamer(int resource) {
		this((Object)resource);
	}
	
	protected void initCamera() {
		if(resource instanceof Integer) {
			this.camera=new VideoCapture((Integer)resource);
		}
		else if(resource instanceof String) {
			this.camera=new VideoCapture((String)resource);
		}
	}
	
	public VideoStreamer start() {
		if(camera==null || !camera.isOpened())
			initCamera();
		
		if(videoStreamers.add(this)) {
			if(streamThread==null || !streamThread.isAlive() || streamThread.isInterrupted()) {
				initStreamThread();
				streamThread.start();
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
			camera.release();
			resetFps();
		}
	}
	
	public VideoStreamer getVideoStreamer(VideoStreamer streamer) {
		for(VideoStreamer str:videoStreamers) {
			if(str.equals(streamer)) {
				return str;
			}
		}
		
		return null;
	}
	
	protected void initStreamThread() {
		this.streamThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Mat frame = new Mat();
				
				if(getBoolVal("size_change")) {
					camera.set(3, getIntVal("size_width"));
					camera.set(4, getIntVal("size_height"));
				}
				
				if (!camera.isOpened()) {
					System.out.println("Error");
				} else {
					while (!streamThread.isInterrupted()) {
						if (camera.read(frame)) {
							if(getBoolVal("size_change")) {
								int width=getIntVal("size_width");
								int height=getIntVal("size_height");
								Imgproc.resize(frame, frame, new Size(width,height));
							}
							try {
								sendMat(frame);
								registerFrameForFPSCalculation();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else {
							System.out.println("Camera not available");
							break;
						}
					}
				}
			}
		});
	}
	
	public static List<Integer> getAvailableCameras(){
		int lastFound=0;
		List<Integer> ret=new ArrayList<>();
		for(int i=0;i<lastFound+10;i++) {
			VideoCapture cam=new VideoCapture(i);
			if(cam.isOpened()) {
				cam.release();
				lastFound=i;
				ret.add(i);
			}
		}
		
		return ret;
	}

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
		if (!(obj instanceof VideoStreamer))
			return false;
		VideoStreamer other = (VideoStreamer) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
	
	
}