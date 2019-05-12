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
import parameters.EnumParameter;
import parameters.IntegerParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;

public class ShapeDetection extends MatEditFunction {

	private static final long serialVersionUID = 7427096678751354797L;


	public ShapeDetection(Boolean empty) {}
	
	public ShapeDetection() {
		super(
			new EnumParameter("shape", Shape.CIRCLE),
			new ParameterGroup("output", 
				new IntegerParameter("xpos",0,false),
				new IntegerParameter("ypos",0,false),
				new IntegerParameter("diameter",0,false)
			)
		);	
	}
	
	
	public Mat findChosenShape(Mat frame) {
		Shape s = (Shape) getEnumVal("shape");;
		switch(s) {
			case CIRCLE: 
				findCircles(frame);
				return frame;				
			case RECTANGLE:
				//findRectangles(frame);
				return frame;
			case TRIANGLE:
				//findTriangles(frame);
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
		
		getMats().put("gray",grayFrame);
		getMats().put("found",found);
		
		for (int i = 0; i < found.rows(); i++) {
			double[] data = found.get(i, 0);
			
			x = data[0];
			y = data[1];
			r = (int) data[2];

			Point center = new Point(x, y);
			// circle center
			Imgproc.circle(frame, center, 5, new Scalar(0, 255, 0), -1);
			// circle outline
			Imgproc.circle(frame, center, r, new Scalar(0, 0, 255), 3);
		}
	}
	
	private void findRectangles(Mat frame) {
		rectOrTriangle(frame);
	}
	
	private void findTriangles(Mat frame) {
		rectOrTriangle(frame);
	}
	
	private void rectOrTriangle(Mat frame) {
        Mat grayframe = new Mat();
        Mat edges = new Mat();

        Imgproc.cvtColor(frame, grayframe, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(grayframe, grayframe);
        Imgproc.GaussianBlur(grayframe, grayframe, new Size(5, 5), 0, 0, Core.BORDER_DEFAULT);

        Imgproc.Canny(grayframe, grayframe, 50, 100);
        Imgproc.threshold(grayframe, grayframe, 0, 255, Imgproc.THRESH_BINARY);
        
        getMats().put("gray",grayframe);
        
        int threshold = 100;
		Imgproc.Canny(grayframe, edges, threshold, threshold*3);
        
		List<MatOfPoint> contours = null;
		Mat hierarchy = new Mat();
		Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();

		// count edges
		for (int i = 0; i >= 0; i = (int) hierarchy.get(0, i)[0]) {  
			MatOfPoint contour = contours.get(i);
			Rect rect = Imgproc.boundingRect(contour);
		    double contourArea = Imgproc.contourArea(contour);
		    matOfPoint2f.fromList(contour.toList());
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
		    long total = approxCurve.total();
		    if (total == 3) { // is triangle
		        // draw triangle
		    }
		    if (total >= 4 && total <= 6) {
		        List<Double> cos = new ArrayList<>();
		        Point[] points = approxCurve.toArray();
		        for (int j = 2; j < total + 1; j++) {
		            cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
		        }
		        Collections.sort(cos);
		        Double minCos = cos.get(0);
		        Double maxCos = cos.get(cos.size() - 1);
		        boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
		        boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
		    
		        if (isRect) {
		            double ratio = Math.abs(1 - (double) rect.width / rect.height);
		            Imgproc.rectangle(edges, points[0], points[3], new Scalar(0, 255, 0));
		            getMats().put("rect",edges);
		        }
		    }
		}
	}
	
	private static double angle(Point pt1, Point pt2, Point pt0) {
	    double dx1 = pt1.x - pt0.x;
	    double dy1 = pt1.y - pt0.y;
	    double dx2 = pt2.x - pt0.x;
	    double dy2 = pt2.y - pt0.y;
	    return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
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



