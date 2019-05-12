package functions.matedit;

import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import parameters.DoubleParameter;
import parameters.group.ParameterGroup;

public class ContourSelection extends MatEditFunction {
	
	private transient int width;
	private transient int height;
	
	public ContourSelection() {
		super(
			new ParameterGroup("preferences", 
				new DoubleParameter("area",1,-100,100),
				new DoubleParameter("bottomgap",1,-100,100)
			)
		);
	}

	@Override
	protected Mat apply(Mat matIn) {
		
		Mat matProc=matIn=matIn.clone();
		width=matProc.cols();
		height=matProc.rows();
		
		Mat matOut=new Mat(matProc.rows(),matProc.cols(),CvType.CV_8U,Scalar.all(0));
		
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(matProc, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfPoint bestContour=selectBestContour(contours);
		
		if(bestContour!=null) {
			ArrayList<MatOfPoint> c=new ArrayList<>();
			c.add(bestContour);
			Imgproc.drawContours(matOut,c, -1, new Scalar(255), -1);
		}
		
		
		return matOut;
	}
	
	private MatOfPoint selectBestContour(ArrayList<MatOfPoint> contours) {
		MatOfPoint bestContour = null;
		double bestValue=0;
		
		double picarea=width*height;
		
		for(MatOfPoint contour:contours) {
			double val=getContourValue(contour,picarea,width,height);
			if(val>bestValue) {
				bestValue=val;
				bestContour=contour;
			}
		}
		
		return bestContour;
	}
	
	private double getContourValue(MatOfPoint contour, double areatotal, int width, int height) {
		Rect rect=Imgproc.boundingRect(contour);

		double area = rect.height * rect.width;
		double areaPrefs=getDoubleVal("preferences_area");
		if(area<10) {
			return 0;
		}
		
		double bottomdist = height - (rect.y + rect.height);
        double bottomPrefs=getDoubleVal("preferences_bottomgap");

		double ret = (area / areatotal) * areaPrefs + bottomPrefs*(1 - bottomdist / (double) height);
		return ret;
	}
	

}
