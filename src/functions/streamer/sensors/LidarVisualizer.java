package functions.streamer.sensors;

import java.awt.Image;
import java.util.Collection;
import java.util.Map;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import functions.Startable;
import functions.streamer.MatStreamer;
import main.ParameterReceiver;
import parameters.CollectionParameter;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import sensors.lidar.LidarMessage;

public class LidarVisualizer extends MatStreamer implements ParameterReceiver, Startable {

	private static final long serialVersionUID = -2583190889427396344L;
	private volatile transient Mat mat;
	// 12m radius => diameter=24m, resolution 10cm => 24m/10cm=240
	private transient Collection<LidarMessage> msgs;

	public LidarVisualizer(Boolean empty) {
	}

	public LidarVisualizer() {
		super(new DoubleParameter("radius",6000,100,12000),
				new DoubleParameter("mmPerPixel", 1,1,100),
				new IntegerParameter("pointRadius", 10,1,1000));
	}
	
	public double getResolution() {
		return getDoubleVal("mmPerPixel");
	}

	@Override
	protected void initStreamThread() {
		this.setStreamThread(new Thread(() -> {
			while (!getStreamThread().isInterrupted()) {
				try {
					mat = generateMat();
					if (mat != null && msgs != null) {
						for (LidarMessage msg : msgs) {
							addDataPoint(mat, msg);
						}
						sendMat(mat);
						sendParameters();
						Thread.sleep(100);
						System.gc();
					}
				}catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
	}

	private void addDataPoint(Mat mat, LidarMessage msg) {
		int[] p = LidarMessageToCoordinates(msg);
		int color = (int) ((msg.getQuality() - 10) / 5 * 255.0);
		Imgproc.circle(mat, new Point(p[0], p[1]), mmToPixel(getIntVal("pointRadius")), new Scalar(color), -1);
	}

	private int[] LidarMessageToCoordinates(LidarMessage msg) {
		int xc = (int) ((msg.getX() + getDoubleVal("radius")) / getResolution());
		int yc = (int) ((msg.getY() + getDoubleVal("radius")) / getResolution());

		return new int[] { xc, yc };
	}

	private Mat generateMat() {
		int size=(int) (getDoubleVal("radius") * 2/getResolution());

		Mat mat = new Mat(size, size, CvType.CV_8U);
		Point c = new Point(mat.width() / 2, mat.height() / 2);
		int l = mmToPixel(200);
		Imgproc.line(mat, new Point(c.x - l, c.y), new Point(c.x + l, c.y), new Scalar(255), mmToPixel(getIntVal("pointRadius")));
		Imgproc.line(mat, new Point(c.x, c.y - l), new Point(c.x, c.y + l), new Scalar(255), mmToPixel(getIntVal("pointRadius")));
		return mat;
	}
	
	private int mmToPixel(double mm) {
		int px=(int)(mm/getResolution());
		if(px<1) {
			px=1;
		}
		return px;
	}

	@Override
	public Image getRepresentationImage() {
		return null;
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,
			ParameterizedObject parameterizedObjectNonSerializable) {
		if(isStarted()) {
			CollectionParameter param = getFirstFittingParameter(parameters, CollectionParameter.class);
			if (param != null && param.getValue() != null) {
				msgs = (Collection<LidarMessage>) param.getValue();
			}
		}
	}

}
