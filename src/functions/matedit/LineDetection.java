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
import net.sourceforge.tess4j.Tesseract;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.EnumParameter;
import parameters.IntegerParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;

public class LineDetection extends MatEditFunction {

	private static final long serialVersionUID = 6444814149620645758L;

	public LineDetection(Boolean empty) {}
	
	public LineDetection() {
		super(
			new DoubleParameter("rho", 20,1,100),
			new IntegerParameter("threshold", 188,1,1000),
			new DoubleParameter("minLength", 137,0,1000),
			new DoubleParameter("maxLineGap", 7,0,1000),
			new BooleanParameter("drawLines", true),
			new ParameterGroup("line",
				new DoubleParameter("centerx", -1,-1,1000),
				new DoubleParameter("centery", -1,-1,1000)
			),
			new ParameterGroup("output", 
				new DoubleParameter("angle",0,false)
			)
		);	
	}
	
	
	private Mat findLines(Mat frame){
		Mat grayFrame = new Mat();
		Mat found = new Mat();
		double x = 0.0;
	    double y = 0.0;
	    int r = 0;
		// convert the frame in gray scale
	    if(frame.channels()>1) {
	    	Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
	    }else {
	    	grayFrame=frame.clone();
		}
		// noise reduction
		Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(), 2,2);
		// detected circles		
		Imgproc.HoughLines(grayFrame, found,getDoubleVal("rho"), Math.PI/20, getIntVal("threshold"));
		Imgproc.HoughLinesP(grayFrame, found, getDoubleVal("rho"), Math.PI/180, getIntVal("threshold"), 
				getDoubleVal("minLength"), getDoubleVal("maxLineGap"));
		//Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0 );

		
		getMats().put("gray",grayFrame);
		getMats().put("found",found);
		
//		for (int i = 0; i < found.rows(); i++) {
//			double[] data = found.get(i, 0);
//			
//			x = data[0];
//			y = data[1];
//			r = (int) data[2];
//
//			Point center = new Point(x, y);
//			// circle center
//			Imgproc.line(frame, center, 5, new Scalar(0, 255, 0), -1);
//			// circle outline
//			Imgproc.circle(frame, center, r, new Scalar(0, 0, 255), 3);
//			
//			IntegerParameter xError=(IntegerParameter)getParameter("output_xerror");
//			IntegerParameter yError=(IntegerParameter)getParameter("output_yerror");
//			IntegerParameter radius=(IntegerParameter)getParameter("output_radius");
//			
//			xError.setValue(x-(frame.width()/2));
//			yError.setValue(y-(frame.height()/2));
//			radius.setValue(r);
//		}
    	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_GRAY2BGR);

    	double centerX=getDoubleVal("line_centerx");
    	double centerY=getDoubleVal("line_centery");
    	centerX=centerX>0?centerX:frame.width()/2;
		centerY=centerY>0?centerY:frame.height()/2;
    	
    	double sumAngles=0;
    	boolean firstNegative=false;
    	
    	double left=0;
    	double up=0;
    	
		for (int i = 0; i < found.rows(); i++) {
			double[] l = found.get(i, 0);
			if(getBoolVal("drawLines")) {
				Imgproc.line(frame, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 1, Imgproc.LINE_AA,
						0);
			}

			double dx=l[0]-l[2];
			double dy=l[1]-l[3];
			
			double angle=Math.atan(dy/dx);
			if(i==0 && angle<0)
				firstNegative=true;
			if(firstNegative) {
				if(angle>Math.PI/4) {
					angle-=Math.PI;
				}
			}else {
				if(angle<-Math.PI/4) {
					angle+=Math.PI;
				}
			}
			left+=2*centerX-l[0]-l[2];
			up-=2*centerY-l[1]-l[3];
			
			sumAngles+=angle;
//			System.out.println("ANGLE: "+angle*180/Math.PI);
		}
		
		
		double avgAngle=sumAngles/found.rows();
		Imgproc.putText(frame,"Angle: "+avgAngle*180/Math.PI, new Point(5,5), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0));
//		System.out.println("ANGLE: "+avgAngle*180/Math.PI);
		
		if(Math.abs(left)>Math.abs(up)) {
			if(left>0) {
				avgAngle+=Math.PI;
				System.out.println("L1");
			}else
				System.out.println("L2");
		}else {
			if(up>0 && firstNegative) {
				System.out.println("U");
				avgAngle+=Math.PI;
			}else if(up<0 && !firstNegative) {
				avgAngle+=Math.PI;
			}
			
			System.out.println(firstNegative);
		}
		
//		avgAngle-=Math.PI/4;
		
		if(avgAngle<0) {
			avgAngle+=2*Math.PI;
		}
		
		double lh=200;
		double ly=Math.sin(avgAngle)*lh;
		double lx=Math.cos(avgAngle)*lh;

		Imgproc.line(frame, new Point(centerX,centerY), new Point(centerX+lx, centerY+ly), 
				new Scalar(0, 255, 0), 2, Imgproc.LINE_AA,0);
		
		((DoubleParameter)getParameter("output_angle")).setValue(avgAngle);
		
		return frame;
	}
	

	@Override
	protected Mat apply(Mat matIn) {
		Mat matout = findLines(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/formerkennung.png");
	}
	
	
}



