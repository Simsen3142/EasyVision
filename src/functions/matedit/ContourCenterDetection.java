package functions.matedit;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.RectangleParameterGroup;

public class ContourCenterDetection extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;

	public ContourCenterDetection(Boolean empty) {}
	
	public ContourCenterDetection() {
		super(
			new RectangleParameterGroup("rect", 
				new DoubleParameter("x", 0,0,100),
				new DoubleParameter("y", 0,0,100),
				new DoubleParameter("width", 100,0,100),
				new DoubleParameter("height", 100 ,0,100)
			),
			new DoubleParameter("outputX", 0,false),
			new DoubleParameter("outputY", 0,false)
		);
	}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		
		int width=matOut.width();
		int height=matOut.height();
		
		List<MatOfPoint> contours=new ArrayList<>();
		
		Imgproc.findContours(matOut, contours, new Mat(), 0, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Rect bestRect = null;
		double maxArea=0;
		for(MatOfPoint contour:contours) {
			Rect r=Imgproc.boundingRect(contour);
			double a=r.area();
			if(bestRect==null || a>maxArea) {
				bestRect=r;
				maxArea=a;
			}
		}
		//crop
		Rect cropRect=((RectangleParameterGroup)getAllParameters().get("rect")).getRect(width, height);
		if(bestRect!=null) {
			Point p=new Point(bestRect.x+bestRect.width/2,bestRect.y+bestRect.height/2);
			if(p.x<cropRect.x) {
				p.x=cropRect.x;
			}		
			if(p.x>cropRect.x+cropRect.width){
				p.x=cropRect.x+cropRect.width;
			}
			if(p.y<cropRect.y) {
				p.y=cropRect.y;
			}
			if(p.y>cropRect.y+cropRect.height){
				p.y=cropRect.y+cropRect.height;
			}
			
			Point center=new Point(cropRect.x+cropRect.width/2,cropRect.y+cropRect.height/2);
			
			double xdist=p.x-center.x;
			double outdx=xdist/(cropRect.width/2);
			DoubleParameter outX=(DoubleParameter) getParameter("outputX");
			outX.setValue(constrain(outdx,-1,1));
			Imgproc.line(matOut, new Point(p.x,0), new Point(p.x,height), new Scalar(200),5);
			
			double ydist=p.y-center.y;
			double outdy=ydist/(cropRect.height/2);
			DoubleParameter outY=(DoubleParameter) getParameter("outputY");
			outY.setValue(constrain(outdy,-1,1));
			Imgproc.line(matOut, new Point(0,p.y), new Point(width,p.y), new Scalar(200),5);
			getMats().put("input",matOut.clone());
		}
		matOut= new Mat(matOut, cropRect);
		
		return matOut;
	}
	
	private double constrain(double v, double min, double max) {
		if(v<min) {
			v=min;
		}else if(v>max) {
			v=max;
		}
		
		return v;
	}
	
	
}
