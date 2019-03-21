package functions.matedit;

import java.awt.Image;
import java.util.Vector;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import database.ImageHandler;
import enums.Shape;
import parameters.EnumParameter;

public class ShapeDetection extends MatEditFunction {

	private static final long serialVersionUID = 7427096678751354797L;


	public ShapeDetection(Boolean empty) {}
	
	public ShapeDetection() {
		super(
			new EnumParameter("shape", Shape.CIRCLE)
		);	
	}
	
	
	public Mat findChosenShape(Mat frame) {
		Shape s = (Shape) getEnumVal("shape");;
		switch(s) {
			case CIRCLE: 
				findCircles(frame);
				return frame;				
			case RECTANGLE:
				findRectangles(frame);
				return frame;
			default: 
				return frame;
		}
			
	}
	
	
	private void findCircles(Mat frame){
		Mat grayFrame = new Mat();
		MatOfRect found = new MatOfRect();
		double x = 0.0;
	    double y = 0.0;
	    int r = 0;
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// noise reduction
		Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(), 2,2);
		// detected circles
		Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0 );
		
		Vector<Mat> circlesList = new Vector<Mat>();
		for (int i = 0; i < circlesList.size(); i++) {
			System.out.println(i);
		}

		Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0);

		getMats().put("gray",grayFrame);
		getMats().put("found",found);
		
		for (int i = 0; i < found.rows(); i++) {
			double[] data = found.get(i, 0);
			
			for (int j = 0; j < data.length; j++) {
				x = data[0];
				y = data[1];
				r = (int) data[2];
			}

			Point center = new Point(x, y);
			// circle center
			Imgproc.circle(frame, center, 5, new Scalar(0, 255, 0), -1);
			// circle outline
			Imgproc.circle(frame, center, r, new Scalar(0, 0, 255), 3);
		}
	}
	
	private static void findRectangles(Mat frame) {
		
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matout = findChosenShape(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/formerkennung.png");
	}
}



