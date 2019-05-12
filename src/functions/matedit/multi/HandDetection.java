package functions.matedit.multi;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;

public class HandDetection extends MultiMatEditFunction {

	private static volatile Image img;
	
	public HandDetection(Boolean empty) {
		
	}
	

	public HandDetection() {
		
	}

	@Override
	public int getNrFunctionInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat pic=matsIn.get(0).clone();
		Mat matHand=matsIn.get(1).clone();
		
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(matHand, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);		
		
		if(contours.size()<1)
			return pic;
		
		MatOfPoint contour=contours.get(0);

		Imgproc.drawContours(pic, contours, -1, new Scalar(0,0,0),3);
		
		MatOfInt hull = new MatOfInt();
		MatOfInt4 defect = new MatOfInt4();
		
		Imgproc.convexHull(contour, hull);
        Imgproc.convexityDefects(contour,hull,defect);
        
        Point[] hullpoints =new Point[hull.rows()];
        for(int i=0; i < hull.rows(); i++){
            int index = (int)hull.get(i, 0)[0];
            hullpoints[i] = new Point(contour.get(index, 0)[0], contour.get(index, 0)[1]);
        }
        
        MatOfPoint hullmop=new MatOfPoint();
        hullmop.fromArray(hullpoints);
        
        int fingerCount = 1;
        try {
			for (int j = 0; j < defect.toList().size() - 3; j += 4) {
	//			//store the depth of the defect
				float depth = defect.toList().get(j + 3) / 255;
				if (depth > 1) {
					// store indexes of start, end, and far points
					int startid = defect.toList().get(j);
					// store the point on the contour as a Point object
					Point startPt = contour.toList().get(startid);
					int endid = defect.toList().get(j + 1);
					Point endPt = contour.toList().get(endid);
					int farid = defect.toList().get(j + 2);
					Point farPt = contour.toList().get(farid);
	
					if (isFinger(defect, contour, j)) {
	//
						if (fingerCount < 5)
							fingerCount++;
//						System.out.println("Distance from start to far: " + String.format("%,.2f", distanceFormula(startPt, farPt)));
//						System.out.println("Distance from end to far:   " + String.format("%,.2f",distanceFormula(endPt, farPt)));
//						System.out.println("Angle of defect:            " + String.format("%,.2f",getAngle(startPt, endPt, farPt)));
//						// draw line from start to end
						Imgproc.line(pic, startPt, endPt, new Scalar(255, 255, 255), 2);
						// draw line from start to far point
						Imgproc.line(pic, startPt, farPt, new Scalar(255, 255, 255), 2);
						// draw line from end to far point
						Imgproc.line(pic, endPt, farPt, new Scalar(255, 255, 255), 2);
						// draw circle around far point
						Scalar c;
						switch(fingerCount) {
							default:
								c=new Scalar(0,0,0);
								break;
							case 1:
								c=new Scalar(255,0,0);
								break;
							case 2:
								c=new Scalar(255,255,0);
								break;
							case 3:
								c=new Scalar(0,255,0);
								break;
							case 4:
								c=new Scalar(0,255,255);
								break;
							case 5:
								c=new Scalar(0,0,255);
								break;
						}
						Imgproc.circle(pic, farPt, 4, c, 5);
					} else {
						// draw line from start to end
						Imgproc.line(pic, startPt, endPt, new Scalar(255, 0, 0), 2);
						// draw line from start to far point
						Imgproc.line(pic, startPt, farPt, new Scalar(255, 0, 0), 2);
						// draw line from end to far point
						Imgproc.line(pic, endPt, farPt, new Scalar(255, 0, 0), 2);
						// draw circle around far point
						Imgproc.circle(pic, farPt, 4, new Scalar(255, 0, 0), 2);
	
					}
				}
			}
        }catch (Exception e) {
        	e.printStackTrace();
		}
		
		String text= fingerCount + " finger(s) detected";
		Imgproc.putText(pic, text, new Point(15, 15), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 255));

		
		
		
		return pic;
	}
	
	private boolean isFinger(MatOfInt4 defect, MatOfPoint contour, int j) {
		Rect boundingRect = Imgproc.boundingRect(contour);
		int tolerance = boundingRect.height / 5;
		double angleTol = 95;
		// store indexes of start, end, and far points
		int startid = defect.toList().get(j);
		// store the point on the contour as a Point object
		Point startPt = contour.toList().get(startid);
		int endid = defect.toList().get(j + 1);
		Point endPt = contour.toList().get(endid);
		int farid = defect.toList().get(j + 2);
		Point farPt = contour.toList().get(farid);

		if (distanceFormula(startPt, farPt) > tolerance && distanceFormula(endPt, farPt) > tolerance
				&& getAngle(startPt, endPt, farPt) < angleTol
				&& endPt.y <= (boundingRect.y + boundingRect.height - boundingRect.height / 4)
				&& startPt.y <= (boundingRect.y + boundingRect.height - boundingRect.height / 4))
			return true;

		return false;
	}
	
	private double getAngle(Point start, Point end, Point far) {
		double a = distanceFormula(start, far);
		double b = distanceFormula(end, far);
		double c = distanceFormula(start, end);
		double angle = Math.acos((a * a + b * b - c * c) / (2 * a * b));
		angle = angle * 180 / Math.PI;
		return angle;
	}

	private double distanceFormula(Point start, Point end) {
		return Math.sqrt(Math.abs(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2)));
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/hand.png");
	}

}
