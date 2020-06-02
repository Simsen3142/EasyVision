package functions.matedit;

import java.awt.Image;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import database.ImageHandler;
import enums.Shape;
import parameters.DoubleParameter;
import parameters.EnumParameter;
import parameters.IntegerParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;

public class CircleDetection extends MatEditFunction {

	private static final long serialVersionUID = 7427096678751354797L;


	public CircleDetection(Boolean empty) {}
	
	public CircleDetection() {
		super(
			new DoubleParameter("dp", 1,0,100),
			new DoubleParameter("mindist", 37,1,200),
			new DoubleParameter("param1", 110,1,300),
			new DoubleParameter("param2", 40,1,200),
			new IntegerParameter("minradius", 30,0,1000),
			new IntegerParameter("maxradius", 200,0,1000),
			new ParameterGroup("output", 
				new DoubleParameter("xerror",0,false),
				new DoubleParameter("yerror",0,false),
				new DoubleParameter("radius",0,false)
			)
		);	
	}
	
	
	private Mat findCircles(Mat frame){
		Mat grayFrame = new Mat();
		MatOfRect found = new MatOfRect();
		double x = 0.0;
	    double y = 0.0;
	    double r = 0;
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// noise reduction
		Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(), 2,2);
		// detected circles		
		Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, getDoubleVal("dp"), getDoubleVal("mindist"), 
				getDoubleVal("param1"), getDoubleVal("param2"), getIntVal("minradius"), getIntVal("maxradius") );
		//Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0 );

		
		getMats().put("gray",grayFrame);
		getMats().put("found",found);
		
		for (int i = 0; i < found.rows(); i++) {
			double[] data = found.get(i, 0);
			
			x = data[0];
			y = data[1];
			r = data[2];

			Point center = new Point(x, y);
			// circle center
			Imgproc.circle(frame, center, 5, new Scalar(0, 255, 0), -1);
			// circle outline
			Imgproc.circle(frame, center, (int) r, new Scalar(0, 0, 255), 3);
			
			DoubleParameter xError=(DoubleParameter)getParameter("output_xerror");
			DoubleParameter yError=(DoubleParameter)getParameter("output_yerror");
			DoubleParameter radius=(DoubleParameter)getParameter("output_radius");
			
			xError.setValue(x-(frame.width()/2));
			yError.setValue(y-(frame.height()/2));
			radius.setValue(r);
		}
		
		return frame;
	}
	

	@Override
	protected Mat apply(Mat matIn) {
		Mat matout = findCircles(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/formerkennung.png");
	}
	
	
}



