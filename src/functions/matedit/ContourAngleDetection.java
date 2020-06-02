package functions.matedit;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.ParameterGroup;

public class ContourAngleDetection extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;

	public ContourAngleDetection(Boolean empty) {}
	
	public ContourAngleDetection() {
		super(
			new ParameterGroup("output", 
				new DoubleParameter("angle", 0,false)
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
		MatOfPoint bestContour=null;
		double maxArea=0;
		for(MatOfPoint contour:contours) {
			Rect r=Imgproc.boundingRect(contour);
			double a=r.area();
			if(bestRect==null || a>maxArea) {
				bestRect=r;
				maxArea=a;
				bestContour=contour;
			}
		}
		
		if(bestContour!=null) {
			RotatedRect rect =Imgproc.minAreaRect(new MatOfPoint2f(bestContour.toArray()));
			((DoubleParameter)getParameter("output_angle")).setValue(rect.angle);
			System.out.println(rect.size.height);
			System.out.println(rect.size.width);
		}
		
		return matOut;
	}
}
