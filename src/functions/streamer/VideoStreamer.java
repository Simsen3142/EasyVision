package functions.streamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import main.MatSender;
import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public abstract class VideoStreamer extends MatStreamer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -691043732851439207L;
	private transient VideoCapture camera;
	private transient Mat mat;
	
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
		super(resource);
	}
	
	public VideoStreamer(String resource) {
		this((Object)resource);
	}
	
	public VideoStreamer(int resource) {
		this((Object)resource);
	}
	
	public VideoStreamer() {
	}
	
	protected void initCamera() {
		if(getResource() instanceof Integer) {
			this.camera=new VideoCapture((Integer)getResource());
		}
		else if(getResource() instanceof String) {
			this.camera=new VideoCapture((String)getResource());
		}
	}
	
	@Override
	public MatStreamer start() {
		if(camera==null || !camera.isOpened())
			initCamera();
		
		return super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		if(getStreamThread()!=null&&getStreamThread().isAlive())
			camera.release();

	}
	
	protected void initStreamThread() {
		setStreamThread(new Thread(new Runnable() {
			@Override
			public void run() {
				mat = new Mat();
				
//				if(getBoolVal("size_change")) {
//					camera.set(3, getIntVal("size_width"));
//					camera.set(4, getIntVal("size_height"));
//				}
				if(camera==null) {
					initCamera();
				}
				if (!camera.isOpened()) {
					System.out.println("Error");
				} else {
					while (!getStreamThread().isInterrupted()) {
						try {
							if (camera.read(mat)) {
								if(getBoolVal("size_change")) {
									int width=getIntVal("size_width");
									int height=getIntVal("size_height");
									Imgproc.resize(mat, mat, new Size(width,height));
								}
								if(mat.rows()>0) {
									try {
										sendMat(mat);
										sendParameters();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								System.gc();
							}else {
								System.out.println("Camera not available");
								break;
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}));
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
}