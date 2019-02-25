package functions.streamer;

import java.io.File;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import parameters.BooleanParameter;

public class FileVideoStreamer extends VideoStreamer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4240705333246586616L;

	public FileVideoStreamer(File file) {
		super(file.getAbsolutePath());
		this.addParameters(new BooleanParameter("endless", true));
	}	
	
	@Override
	protected void initStreamThread() {
		initCamera();

		this.setStreamThread(new Thread(new Runnable() {
			@Override
			public void run() {
				Mat frame = new Mat();
				
				if(getBoolVal("size_change")) {
					getCamera().set(3, getIntVal("size_width"));
					getCamera().set(4, getIntVal("size_height"));
				}
				
				if (!getCamera().isOpened()) {
					System.out.println("Error");
				} else {
					while (!getStreamThread().isInterrupted()) {
						try {
							if (getCamera().read(frame)) {
								if(getBoolVal("size_change")) {
									int width=getIntVal("size_width");
									int height=getIntVal("size_height");
									Imgproc.resize(frame, frame, new Size(width,height));
								}
								try {
									sendMat(frame);
									sendParameters();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else {
								if(getBoolVal("endless")) {
									stop();
									start();
									break;
								}
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
}