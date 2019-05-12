package functions.matedit.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import functions.parameterreceiver.SoundPlayer;
import main.ParameterReceiver;
import parameters.DoubleParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class RatioChecker extends MultiMatEditFunction{
	
	@Override
	public int getNrFunctionInputs() {
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		return 2;
	}
	
	public RatioChecker() {
		super(
			new DoubleParameter("maxHeightPerWith", 0,0,100),
			new DoubleParameter("maxWidthPerHeight", 10,0,100)
		);
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat pic=matsIn.get(0).clone();
		Mat contourPic=matsIn.get(1).clone();
		
		
		List<MatOfPoint> contours=new ArrayList<>();
		List<MatOfPoint> contourOk=new ArrayList<>();
		List<MatOfPoint> contourNotok=new ArrayList<>();
		Imgproc.findContours(contourPic, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(MatOfPoint contour:contours) {
			Rect rect=Imgproc.boundingRect(contour);
			double height=rect.height;
			double width=rect.width;
			
			double widthPerHeight=width/height;
			double heightPerWidth=height/width;
			
			boolean inBounce=heightPerWidth>=getDoubleVal("maxHeightPerWith") && 
					widthPerHeight>=getDoubleVal("maxWidthPerHeight");
			Scalar c=inBounce?new Scalar(0,255,0):new Scalar(0,0,255);
			drawRect(pic,c,3,rect);
			Point center=new Point(rect.x+rect.width/2, rect.y+rect.height/2);
			Imgproc.putText(pic, Math.round(width)+" - "+ Math.round(height), center, Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 0));
			
			if(inBounce) {
				contourOk.add(contour);
			}else {
				contourNotok.add(contour);
				
				for(SoundPlayer soundPlayer:getParameterReceiverWhichExtend(SoundPlayer.class)) {
					Map<String, ParameterObject> paramO=new HashMap<String, ParameterObject>();
					paramO.put("playsound", null);
					soundPlayer.onParameterReceived(paramO);
				}
			}
		}
		
//		Imgproc.drawContours(pic, contourOk, -1, new Scalar(0,255,0),3);
//		Imgproc.drawContours(pic, contourNotok, -1, new Scalar(0,0,255),3);
		
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
