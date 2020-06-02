package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.IntegerParameter;

public class EdgeDetection extends MatEditFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3937043096717142438L;
	private static volatile Image img;

	public EdgeDetection() {
		super(
			new DoubleParameter("threshold1", 150,0,5000),
			new DoubleParameter("threshold2", 200,0,5000),
			new IntegerParameter("apertureSize", 3,3,7),
			new BooleanParameter("withcolor", true)
		);
	}
	
	public EdgeDetection(Boolean empty) {}

	@Override
	protected Mat apply(Mat matIn) {
		Mat grayImage=matIn.clone();
    	Mat detectedEdges=new Mat();
    	
    	if(grayImage.channels()>1)
    		Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_BGR2GRAY);
    	Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, getDoubleVal("threshold1"), getDoubleVal("threshold2"),getIntVal("apertureSize"), true);
        
        if(getBoolVal("withcolor")) {
	        Mat dest = new Mat();
	        Core.add(dest, Scalar.all(0), dest);
	        matIn.copyTo(dest, detectedEdges);
	        return dest;
        }
        return detectedEdges;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/kantenerkennung.png");
		return img;
	}
}
