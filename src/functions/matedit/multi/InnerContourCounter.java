package functions.matedit.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import parameters.IntegerParameter;
import parameters.group.ColorParameterGroup;
import parameters.group.ColorParameterGroup.ColorType;

public class InnerContourCounter extends MultiMatEditFunction {

	public InnerContourCounter() {
		super(new IntegerParameter("count", 0,false),
				new ColorParameterGroup("markcolor", ColorType.HSV));
		}
	
	@Override
	public int getNrFunctionInputs() {
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		return 2;
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat pic = matsIn.get(0).clone();
		Mat contourPic = matsIn.get(1).clone();

		List<MatOfPoint> contours = new ArrayList<>();

		Mat hierarchy = new Mat();
		Imgproc.findContours(contourPic, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		int count=0;

		Scalar c=((ColorParameterGroup)getAllParameters().get("markcolor")).getColorOpencv();
		
		for (int i = 0; i < contours.size();i++) {
			if(contours.size()<=i)
				break;
			
			double[] contourInfo = hierarchy.get(0, i);
			Rect r = Imgproc.boundingRect(contours.get(i));
			if (contourInfo[2] < 0) {
				drawRect(pic,c,2,r);
				count++;
				Point center=new Point(r.x+r.width/2, r.y+r.height/2);
				Imgproc.putText(pic, count+"", center, Core.FONT_HERSHEY_PLAIN, 1, c);

			}
		}
		
		Imgproc.putText(pic,"Anzahl: " +count, new Point(20,20), Core.FONT_HERSHEY_PLAIN, 1, c);

		
		((IntegerParameter)getParameter("count")).setValue(count);

		return pic;
	}
	
	private void drawRect(Mat matIn, Scalar color, int thickness, RotatedRect rect) {
		Point[] corner = new Point[4];
		rect.points(corner);
		drawRect(matIn, color, thickness, corner);
	}
	
	private void drawRect(Mat matIn, Scalar color, int thickness, Rect rect) {
		drawRect(matIn, color, thickness, new Point(rect.x, rect.y),
				new Point(rect.x + rect.width, rect.y),
				new Point(rect.x + rect.width, rect.y + rect.height),
				new Point(rect.x, rect.y + rect.height));	
	}
	
	private void drawRect(Mat matIn, Scalar color, int thickness, Point... point) {
		for (int j = 0; j < 4; j++) {
			Imgproc.line(matIn, point[j], point[(j + 1) % 4], color, thickness);
		}
	}

}
