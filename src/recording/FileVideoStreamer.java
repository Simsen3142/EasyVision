package recording;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import parameters.BooleanParameter;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class FileVideoStreamer extends VideoStreamer {

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
						if (getCamera().read(frame)) {
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
							if(getBoolVal("endless")) {
								stop();
								start();
								break;
							}
							System.out.println("Camera not available");
							break;
						}
					}
				}
			}
		}));
	}
}