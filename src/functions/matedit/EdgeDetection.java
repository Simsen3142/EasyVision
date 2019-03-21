package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;

public class EdgeDetection extends MatEditFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3937043096717142438L;
	private static volatile Image img;
	public boolean withColor = true;

	public EdgeDetection() {
		this.withColor = true;
	}
	
	public EdgeDetection(Boolean empty) {}

	@Override
	public Mat apply(Mat matIn) {
		Mat grayImage=matIn.clone();
    	Mat detectedEdges=matIn.clone();
    	
		Imgproc.cvtColor(matIn, grayImage, Imgproc.COLOR_BGR2GRAY);
    	Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, 150, 200,3, true);
        
        if(withColor) {
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