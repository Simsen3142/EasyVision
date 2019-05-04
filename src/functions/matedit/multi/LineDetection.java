package functions.matedit.multi;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.*;
import org.opencv.imgproc.*;

import database.ImageHandler;
import functions.matedit.ExtractBlack;
import functions.matedit.MatEditFunction;
import main.MatMapReceiver;
import main.MatReceiver;
import main.MatSender;
import parameters.*;
import parameters.group.*;
import parameters.group.ColorParameterGroup.ColorType;
import view.MatEditFunctionMatsPanel;
import view.PanelFrame;

public class LineDetection extends MultiMatEditFunction {

	private static final long serialVersionUID = -34843237520925562L;
	private static volatile Image img;
	private int width;
	private int height;

	private int error;
	private int angle;
	private boolean sqrDetected;
	private int sqrError;
	private boolean crossingDetected = false;
	private boolean tooFar = false;
	
	@Override
	public int getNrFunctionInputs() {
		return 1;
	}
	
	@Override
	public int getNrMatInputs() {
		return 3;
	}
	
	@Override
	public Mat apply(Map<Integer, Mat> matsIn) {
		Mat matMain=matsIn.get(0);
		Mat line=matsIn.get(1);
		Mat squares=matsIn.get(2);
		
		width = matMain.cols();
		height = matMain.rows();

		getMats().put("input", matMain);
		Mat procMat = matMain;
		getMats().put("procMat", procMat);

		Mat matOut = matMain.clone();
		getMats().put("output", matOut);

//		line = new Mat();
//		line=ExtractBlack.apply(procMat, getDoubleVal("line_min"), getDoubleVal("line_max"));
		getMats().put("line", line);

		Imgproc.cvtColor(procMat, procMat, Imgproc.COLOR_BGR2HSV);
		
//		squares = new Mat();
		getMats().put("squares", squares);
//		Scalar sqrMin = new Scalar(getDoubleVal("sqr_min_h"), getDoubleVal("sqr_min_s"), getDoubleVal("sqr_min_v"));
//		Scalar sqrMax = new Scalar(getDoubleVal("sqr_max_h"), getDoubleVal("sqr_max_s"), getDoubleVal("sqr_max_v"));
//		Core.inRange(procMat, sqrMin, sqrMax, squares);

//		Scalar lnMin=new Scalar(0,0,0);
//		Scalar lnMax=new Scalar(255,255,50);
//		Scalar sqrMin=new Scalar(25,39,43);
//		Scalar sqrMax=new Scalar(95,255,204);

		ArrayList<MatOfPoint> contoursSquare = new ArrayList<>();
		Imgproc.findContours(squares, contoursSquare, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(matOut, contoursSquare, -1, new Scalar(0, 255, 255), 1);

		Rect bestSqr = null;
		for (int i = 0; i < contoursSquare.size(); i++) {
			bestSqr = processSquareContour(matOut, contoursSquare.get(i), bestSqr, line);
		}

		if (bestSqr != null) {
			sqrDetected = true;
			drawRect(matOut, new Scalar(255, 255, 0), 1, new Point(bestSqr.x, bestSqr.y),
					new Point(bestSqr.x + bestSqr.width, bestSqr.y),
					new Point(bestSqr.x + bestSqr.width, bestSqr.y + bestSqr.height),
					new Point(bestSqr.x, bestSqr.y + bestSqr.height));
			Point sqrCenter = new Point(bestSqr.x + bestSqr.width / 2, bestSqr.y + bestSqr.height / 2);
			sqrError = (int) (sqrCenter.x - width / 2);
		} else {
			sqrDetected = false;
		}

		String turnOutput="";
		if (sqrDetected) {
			boolean bs[] = processSquare(bestSqr, line);

			turnOutput = "TURN ";
			if (bs[2]) {
				turnOutput += "RIGHT";
			}
			if (bs[3]) {
				turnOutput += "LEFT";
			}
			Imgproc.putText(matOut, turnOutput, new Point(15, 15), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 255));
		}
		((StringParameter)getParameter("output_turn")).setValue(turnOutput);

		if (!sqrDetected) {
			crossingDetected = checkIfCrossing(line);
			if (crossingDetected) {
				MatSender sender=getMatSenderByIndex(3);
				if(sender instanceof MatEditFunction) {
					line=((MatEditFunction)sender).performFunction(line);
				}
			}
		}
		
		tooFar=checkIfTooFar(line);

		ArrayList<MatOfPoint> contoursLine = new ArrayList<>();
		Imgproc.findContours(line, contoursLine, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(matOut, contoursLine, -1, new Scalar(255, 0, 0), 1);

		RotatedRect biggestRect = null;
		MatOfPoint bestContour = null;
		for (int i = 0; i < contoursLine.size(); i++) {
			final RotatedRect rectBefore = biggestRect;
			biggestRect = processLineContour(matOut, contoursLine.get(i), biggestRect);
			if (rectBefore != biggestRect)
				bestContour = contoursLine.get(i);
		}

		if (biggestRect != null) {
			Point[] corner = new Point[4];
			biggestRect.points(corner);
			drawRect(matOut, new Scalar(0, 255, 0), 1, corner);

			Point center = biggestRect.center;
			error = (int) (center.x - width / 2);
			angle = (int) biggestRect.angle;
			if (angle < -45) {
				angle += 90;
			}
			if (biggestRect.size.width < biggestRect.size.height && angle > 0) {
				angle = (90 - angle) * -1;
			}
			if (biggestRect.size.width > biggestRect.size.height && angle < 0) {
				angle += 90;
			}

			Imgproc.line(matOut, center, new Point(width / 2, biggestRect.center.y), new Scalar(255, 255, 255), 1);
			Imgproc.putText(matOut, error + "", new Point(biggestRect.center.x, biggestRect.center.y - 15),
					Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255, 255, 255));
			int degrees = 90 - angle;
			int x = (int) (center.x + 100 / Math.tan(Math.toRadians(degrees)));
			int y = (int) (center.y - 100);
			Imgproc.line(matOut, center, new Point(x, y), new Scalar(0, 0, 255), 1);
			Imgproc.putText(matOut, angle + "deg", new Point(biggestRect.center.x, biggestRect.center.y + 15),
					Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 255));
		}
		
		
		IntegerParameter paramError=(IntegerParameter)getParameter("output_lnerrorx");
		IntegerParameter paramAngle=(IntegerParameter)getParameter("output_angle");
		
		if(tooFar) {
			int errorNew=error<0?-100000:100000;
			paramError.setValue(errorNew);
			paramAngle.setValue(0);
		}else {
			paramError.setValue(error);
			paramAngle.setValue(angle);
		}
		
		line=null;
		squares=null;
		return matOut;
	}
	
	public LineDetection(Boolean empty) {}
	
	public LineDetection() {
		super(
			new ParameterGroup("toofar", 
				new DoubleParameter("uppercent", 70, 0, 100),
				new DoubleParameter("percentnotwhite", 1, 0, 100)
			),	
			new ParameterGroup("crossing", 
				new DoubleParameter("sidepercent", 10, 0, 100),
				new DoubleParameter("percentwhite", 2, 0, 100)
			),	
			new ParameterGroup("output", 
				new IntegerParameter("lnerrorx",0,false),
				new IntegerParameter("angle",0,false),
				new StringParameter("turn","",false)
			)
		);
	}

	private RotatedRect processLineContour(Mat matIn, MatOfPoint contour, RotatedRect bestFittingRect) {
		MatOfPoint2f contour_x = new MatOfPoint2f(contour.toArray());

		RotatedRect rect = Imgproc.minAreaRect(contour_x);
		double rectRating = generateRectRating(rect);
		double bestFittingRectRating = generateRectRating(bestFittingRect);
		if (bestFittingRect == null || rectRating > bestFittingRectRating) {
			return rect;
		}
		return bestFittingRect;
	}

	private double generateRectRating(RotatedRect rect) {
		if (rect == null)
			return 0;
		double area = rect.size.height * rect.size.width;
		double maxArea = width * height;

		Rect boundingRect = rect.boundingRect();
		double lowerDistance = height - (boundingRect.y + boundingRect.height);

		int middle = width / 2;
		double xDistance;
		if (boundingRect.x > middle) {
			xDistance = boundingRect.x - middle;
		} else {
			int right = boundingRect.x + boundingRect.width;
			if (right >= middle) {
				xDistance = 0;
			} else {
				xDistance = middle - right;
			}
		}
		double ret = (area / maxArea) * 5 + (1 - lowerDistance / (double) height)
				+ (1 - xDistance / (double) width) / 1.5;

		return ret;
	}

	private Rect processSquareContour(Mat matIn, MatOfPoint contour, Rect lowestRect, Mat line) {
		Rect sqr = Imgproc.boundingRect(contour);

		double sqrRating = generateSqrRating(sqr, line);
		double bestFittingSqrRating = generateSqrRating(lowestRect, line);

		if (sqrRating > bestFittingSqrRating) {
			return sqr;
		}
		return lowestRect;
	}

	private double generateSqrRating(Rect sqr, Mat line) {
		if (sqr == null)
			return 0;

		boolean bs[] = processSquare(sqr, line);

		if (!bs[0])
			return 0;

		double area = sqr.height * sqr.width;
		double maxArea = width * height;

		double lowerDistance = height - (sqr.y + sqr.height);

		double ret = (area / maxArea) * 7 + (1 - lowerDistance / (double) height);
		return ret;
	}

	/**
	 * @param sqr
	 * @param line
	 * @return Returns an array of boolean[]{up,down,left,right} which indicate,
	 *         whether or not die line is next to the square side
	 */
	private boolean[] processSquare(Rect sqr, Mat line) {
		int checkBoundX = width / 50;
		if (checkBoundX < 1) {
			checkBoundX = 1;
		}

		int checkBoundY = height / 50;
		if (checkBoundY < 1) {
			checkBoundY = 1;
		}

		boolean up = false;
		boolean down = false;
		boolean left = false;
		boolean right = false;

		int[] coordUp = new int[] { sqr.x + sqr.width / 2, sqr.y };
		int[] coordDown = new int[] { sqr.x + sqr.width / 2, sqr.y + sqr.height };
		int[] coordLeft = new int[] { sqr.x, sqr.y + sqr.height / 2 };
		int[] coordRight = new int[] { sqr.x + sqr.width, sqr.y + sqr.height / 2 };

		up = checkIfFilledInLine(line, coordUp, -checkBoundY, true);
		down = checkIfFilledInLine(line, coordDown, checkBoundY, true);
		left = checkIfFilledInLine(line, coordLeft, -checkBoundX, false);
		right = checkIfFilledInLine(line, coordRight, checkBoundX, false);

		return new boolean[] { up, down, left, right };
	}

	private boolean checkIfFilledInLine(Mat line, int[] startCoord, int checkBound, boolean checkRow) {
		for (int i = 1; i <= Math.abs(checkBound); i++) {
			int i1 = checkBound > 0 ? i : -i;
			int row = startCoord[1] + (checkRow ? i1 : 0);
			int column = startCoord[0] + (!checkRow ? i1 : 0);
			if (row < 0 || column < 0 || row > height - 1 || column > width - 1) {
				break;
			}
			boolean pixelFilled = line.get(row, column)[0] != 0;
			if (pixelFilled) {
				return true;
			}
		}
		return false;
	}

	private void drawRect(Mat matIn, Scalar color, int thickness, Point... point) {
		for (int j = 0; j < 4; j++) {
			Imgproc.line(matIn, point[j], point[(j + 1) % 4], color, thickness);
		}
	}
	
	private boolean checkIfCrossing(Mat line) {
		double sidepercentcrossing=getDoubleVal("crossing_sidepercent")/100;
		double percentWhite4Crossing=getDoubleVal("crossing_percentwhite")/100;
		
		int rectWidth=(int) (line.width()*sidepercentcrossing);
		int rectHeight=(int) (line.height()*sidepercentcrossing);
		
		Rect rL=new Rect(0, 0, rectWidth, line.height());
		Rect rR=new Rect(line.width()-rectWidth, 0, rectWidth, line.height());
//		Rect rU=new Rect(0, 0, line.width(), rectHeight);
		Mat mL=new Mat(line,rL);
		Mat mR=new Mat(line,rR);
//		Mat mU=new Mat(line,rU);
		
		
//		boolean left=getPercentOfWhite(mL)>percentWhite4Crossing;
//		boolean right=getPercentOfWhite(mR)>percentWhite4Crossing;
//		boolean up=getPercentOfWhite(mU)>percentWhite4Crossing;
		
		
		return getPercentOfWhite(mL)>percentWhite4Crossing && getPercentOfWhite(mR)>percentWhite4Crossing;
	}
	
	private boolean checkIfTooFar(Mat line) {
		double uppercentTooFar = getDoubleVal("toofar_uppercent")/100;
		double percentnotWhite4Toofar =  getDoubleVal("toofar_percentnotwhite")/100;
		
		int rectHeight=(int) (line.height()*uppercentTooFar);
		Rect r=new Rect(0, 0, line.width(), rectHeight);
		Mat m=new Mat(line,r);
		
		return getPercentOfWhite(m)<percentnotWhite4Toofar;
	}
	
	private double getPercentOfWhite(Mat matIn) {
		int sumWhite=0;
		int sumBlack=0;
		
		int stepRow=matIn.rows()/100;
		if(stepRow<1)
			stepRow=1;
		int stepCols=matIn.cols()/100;
		if(stepCols<1)
			stepCols=1;
		
		for(int row=0;row<matIn.rows();row+=stepRow) {
			for(int col=0;col<matIn.cols();col+=stepCols) {
				if(matIn.get(row, col)[0]==255) {
					sumWhite++;
				}else {
					sumBlack++;
				}
			}
		}
		
		return (double)sumWhite/(double)(sumWhite+sumBlack);
	}

	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/linien.png");
		return img;
	}

	

//	private void editLineContour(MatOfPoint line, double minX, double maxX) {
//		int size = line.rows();
//		ArrayList<int[]> coords = new ArrayList<>();
//		for (int i = 0; i < size; i++) {
//			double[] point = line.get(i, 0);
//			int x = (int) point[0];
//			int y = (int) point[1];
//			coords.add(new int[] { x, y, i });
//		}
//
//		coords.sort(new CoordsYComparator());
//		
//		for(int[] is:coords) {
//			int x=is[0];
//			int y=is[1];
//			int pos=is[2];
//			
//			if(x>maxX)
//				line.row(pos).setTo(new Scalar(maxX,y));
//			if(x<minX)
//				line.row(pos).setTo(new Scalar(minX,y));
//		}
//	}
//	
//	private class CoordsYComparator implements Comparator<int[]>{
//		@Override
//		public int compare(int[] i1, int[] i2) {
//			if(i1[1]!=i2[1])
//				return Integer.compare(i1[1], i2[1]);
//			else
//				return Integer.compare(i1[0], i2[0]);
//		}
//	}

}
