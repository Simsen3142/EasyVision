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

import com.google.common.annotations.Beta;

import database.ImageHandler;
import parameters.BooleanParameter;
import parameters.DoubleParameter;
import parameters.group.ParameterGroup;
import parameters.group.RectangleParameterGroup;

public class ContourDistanceDetection extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;

	public ContourDistanceDetection(Boolean empty) {}
	
	public ContourDistanceDetection() {
		super(
			new RectangleParameterGroup("rect", 
				new DoubleParameter("x", 0,0,100),
				new DoubleParameter("y", 0,0,100),
				new DoubleParameter("width", 100,0,100),
				new DoubleParameter("height", 100 ,0,100)
			),
			new BooleanParameter("inPercent", true),
			new ParameterGroup("output",
				new DoubleParameter("left", 0,false),
				new DoubleParameter("right", 0,false),
				new DoubleParameter("up", 0,false),
				new DoubleParameter("down", 0,false),
				new DoubleParameter("distance", 0,false)
			)

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
		
		RectangleParameterGroup rectGr=((RectangleParameterGroup)getAllParameters().get("rect"));
		
		if(bestRect!=null) {
			Rect rectX=rectGr.getRect(width, height);
			double dist[]=new double[4];
			
			dist[0]=rectX.x-(bestRect.x+bestRect.width);
			dist[1]=bestRect.x-(rectX.x+rectX.width);
			dist[2]=rectX.y-(bestRect.y+bestRect.height);
			dist[3]=bestRect.y-(rectX.y+rectX.height);

			for(int i=0;i<dist.length;i++) {
				dist[i]=constrain(dist[i], 0, 100000);
			}
			
			Point p=new Point(bestRect.x+bestRect.width/2,bestRect.y+bestRect.height/2);
			
			if(dist[0]>0)
				Imgproc.line(matOut, new Point(rectX.x,p.y), new Point(bestRect.x+bestRect.width,p.y), new Scalar(50),5);
			if(dist[1]>0)
				Imgproc.line(matOut, new Point(rectX.x+rectX.width,p.y), new Point(bestRect.x,p.y), new Scalar(100),5);
			if(dist[2]>0)				
				Imgproc.line(matOut, new Point(p.x,rectX.y), new Point(p.x,bestRect.height+bestRect.y), new Scalar(150),5);
			if(dist[3]>0)				
				Imgproc.line(matOut, new Point(p.x,rectX.y+rectX.height), new Point(p.x,bestRect.y), new Scalar(200),5);

			if(getBoolVal("inPercent")) {
				dist[0]=rectX.x>0?dist[0]/(double)rectX.x:0;
				dist[1]=dist[1]/(double)(width-(rectX.x+rectX.width));
				dist[2]=rectX.y>0?dist[2]/(double)rectX.y:0;
				dist[3]=dist[3]/(double)(height-(rectX.y+rectX.height));
				for(int i=0;i<dist.length;i++) {
					dist[i]=constrain(dist[i], 0, 100000);
				}
			}
			
			((DoubleParameter)getParameter("output_left")).setValue(dist[0]);
			((DoubleParameter)getParameter("output_right")).setValue(dist[1]);
			((DoubleParameter)getParameter("output_up")).setValue(dist[2]);
			((DoubleParameter)getParameter("output_down")).setValue(dist[3]);
			
			double distance=Math.sqrt(dist[0]*dist[0]+dist[1]*dist[1]+dist[2]*dist[2]+dist[3]*dist[3]);
			distance=distance/Math.sqrt(2);
			distance=constrain(distance, 0, 1);
			((DoubleParameter)getParameter("output_distance")).setValue(distance);
		}else {
			((DoubleParameter)getParameter("output_distance")).setValue(1);
		}

		
//		//crop
//		if(bestRect!=null) {
//			Point p=new Point(bestRect.x+bestRect.width/2,bestRect.y+bestRect.height/2);
//			if(p.x<cropRect.x) {
//				p.x=cropRect.x;
//			}		
//			if(p.x>cropRect.x+cropRect.width){
//				p.x=cropRect.x+cropRect.width;
//			}
//			if(p.y<cropRect.y) {
//				p.y=cropRect.y;
//			}
//			if(p.y>cropRect.y+cropRect.height){
//				p.y=cropRect.y+cropRect.height;
//			}
//			
//			Point center=new Point(cropRect.x+cropRect.width/2,cropRect.y+cropRect.height/2);
//			
//			double xdist=p.x-center.x;
//			double outdx=xdist/(cropRect.width/2);
//			DoubleParameter outX=(DoubleParameter) getParameter("outputX");
//			outX.setValue(constrain(outdx,-1,1));
//			Imgproc.line(matOut, new Point(p.x,0), new Point(p.x,height), new Scalar(200),5);
//			
//			double ydist=p.y-center.y;
//			double outdy=ydist/(cropRect.height/2);
//			DoubleParameter outY=(DoubleParameter) getParameter("outputY");
//			outY.setValue(constrain(outdy,-1,1));
//			Imgproc.line(matOut, new Point(0,p.y), new Point(width,p.y), new Scalar(200),5);
		rectGr.getMatReceiver().onReceive(matOut.clone(), this);
//		}
		
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
