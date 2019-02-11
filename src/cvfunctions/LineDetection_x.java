package cvfunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.*;

import main.MainFrame;
import parameters.*;
import parameters.group.*;
import parameters.group.ColorParameterGroup.ColorType;

public class LineDetection_x extends MatEditFunction {

	private int width;
	private int height;

	private int error;
	private int angle;
	private boolean sqrDetected;
	private int sqrError;
	private boolean crossingDetected = false;

	@Override
	public Mat apply(Mat matIn) {
		width = matIn.cols();
		height = matIn.rows();

		getMats().put("input", matIn);
		Mat procMat = matIn.clone();
		getMats().put("procMat", procMat);

		Mat matOut = matIn.clone();
		getMats().put("output", matOut);

		Imgproc.cvtColor(procMat, procMat, Imgproc.COLOR_BGR2HSV);
		Mat line = new Mat();
		getMats().put("line", line);
		Scalar lnMin = new Scalar(getDoubleVal("line_min_h"), getDoubleVal("line_min_s"), getDoubleVal("line_min_v"));
		Scalar lnMax = new Scalar(getDoubleVal("line_max_h"), getDoubleVal("line_max_s"), getDoubleVal("line_max_v"));
		Core.inRange(procMat, lnMin, lnMax, line);
		Mat squares = new Mat();
		getMats().put("squares", squares);
		Scalar sqrMin = new Scalar(getDoubleVal("sqr_min_h"), getDoubleVal("sqr_min_s"), getDoubleVal("sqr_min_v"));
		Scalar sqrMax = new Scalar(getDoubleVal("sqr_max_h"), getDoubleVal("sqr_max_s"), getDoubleVal("sqr_max_v"));
		Core.inRange(procMat, sqrMin, sqrMax, squares);

//		Scalar lnMin=new Scalar(0,0,0);
//		Scalar lnMax=new Scalar(255,255,50);
//		Scalar sqrMin=new Scalar(25,39,43);
//		Scalar sqrMax=new Scalar(95,255,204);

		removeNoiseLine(line);
		removeNoiseSqr(squares);

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

		if (sqrDetected) {
			boolean bs[] = processSquare(bestSqr, line);

			String output = "TURN ";
			if (bs[2]) {
				output += "RIGHT";
			}
			if (bs[3]) {
				output += "LEFT";
			}
			Imgproc.putText(matOut, output, new Point(15, 15), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 0, 255));
		}

		if (!sqrDetected) {
			crossingDetected = checkIfCrossing(line, matOut);
			if (crossingDetected)
				removeNoiseLineCrossing(line);
		}

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

		return matOut;
	}

	public LineDetection_x() {
		super(new ParameterGroup("line",
				new ColorParameterGroup("min", ColorType.HSV, new DoubleParameter("h", 0, 0, 255),
						new DoubleParameter("s", 0, 0, 255), new DoubleParameter("v", 0, 0, 255)),
				new ColorParameterGroup("max", ColorType.HSV, new DoubleParameter("h", 255, 0, 255),
						new DoubleParameter("s", 255, 0, 255), new DoubleParameter("v", 50, 0, 255))),
				new ParameterGroup("sqr",
						new ColorParameterGroup("min", ColorType.HSV, new DoubleParameter("h", 25, 0, 255),
								new DoubleParameter("s", 39, 0, 255), new DoubleParameter("v", 43, 0, 255)),
						new ColorParameterGroup("max", ColorType.HSV, new DoubleParameter("h", 95, 0, 255),
								new DoubleParameter("s", 255, 0, 255), new DoubleParameter("v", 204, 0, 255))),
				new ParameterGroup("noise", new IntegerParameter("erosion", 2, 0, 100),
						new IntegerParameter("dilation", 3, 0, 100)));
	}

	private void removeNoiseLine(Mat line) {
		int erosion_size = getIntVal("noise_erosion");
		int dilation_size = getIntVal("noise_dilation");
		removeNoise(line, erosion_size, crossingDetected ? 20 * erosion_size : erosion_size, dilation_size,
				dilation_size);
	}

	private void removeNoiseSqr(Mat sqr) {
		int erosion_size = getIntVal("noise_erosion");
		int dilation_size = getIntVal("noise_dilation");
		removeNoise(sqr, erosion_size, erosion_size, dilation_size, dilation_size);
	}

	private void removeNoiseLineCrossing(Mat line) {
		int erosion_size = getIntVal("noise_erosion");
		int dilation_size = getIntVal("noise_dilation");
		removeNoise(line, 0, 20 * erosion_size, 0, dilation_size);
	}

	private void removeNoise(Mat matIn, int erosionX, int erosionY, int dilationX, int dilationY) {
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosionX + 1, 2 * erosionY + 1));
		Imgproc.erode(matIn, matIn, element);
		element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilationX + 1, 2 * dilationY + 1));
		Imgproc.dilate(matIn, matIn, element);
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

	private boolean checkIfCrossing(Mat line, Mat matIn) {
		int crossingSize = width / 5;

		int heightDivision=100;
		final int heightIncrease = (height > heightDivision ? (height / heightDivision) : 1)*-1;
		final int widthIncrease = width > 50 ? (width / 50) : 1;
		
		List<Integer> widths=new ArrayList<>();
		
		int multi4crossing=3;

		int[] lastNormal = null;
		int beginX=0;
		int xLength = 0;
		boolean lastFound = false;
		List<int[]> lastBeginnings = new ArrayList<>();
		List<int[]> currentBeginnings = new ArrayList<>();
		boolean lineContinues = false;
		boolean foundSomething=false;
		
		
		int smallerCount=0;
		int smallerAmtNeeded=3;
		for (int y = height-1; y >0; y += heightIncrease) {
			for (int x = 0; x < width; x += widthIncrease) {
				boolean pixelFilled = line.get(y, x)[0] != 0;
				if (pixelFilled) {
					if (!lastFound) {
						lastFound = true;
						beginX=x;
					}
					xLength += widthIncrease;
				} else if (lastFound) {
					int[] beginning=new int[] { beginX, xLength };
					int[] foundBeginning=new int[2];
					lineContinues = checkIfLineContinues(beginning, lastBeginnings, widthIncrease,foundBeginning);
					if(lineContinues)
						foundSomething=true;
					if(!foundSomething)
						lineContinues=true;
					
					if (lineContinues) {
						currentBeginnings.add(beginning);
						if(lastNormal!=null) {
							if(foundBeginning[1]<1.5*lastNormal[1]) {
								widths.add(beginning[1]);
								int hysterese=(int) ((double)lastNormal[1]*1);
								int min=lastNormal[0]-hysterese;
								int max=lastNormal[0]+hysterese;
								
								Imgproc.line(matIn, new Point(beginning[0],y),  new Point(beginning[0]+beginning[1],y), new Scalar(255,255,255),1);
								
								if(min < foundBeginning[0] && max > foundBeginning[0]) {
									if(++smallerCount>=smallerAmtNeeded) {
										Imgproc.line(matIn, new Point(beginning[0],y),  new Point(beginning[0]+beginning[1],y), new Scalar(0,255,255),1);
										System.out.println("KREUZUNG!");
										return true;
									}
								}else {
									lastNormal=null;
								}
							}else {
								Imgproc.line(matIn, new Point(beginning[0],y),  new Point(beginning[0]+beginning[1],y), new Scalar(255,100,100),1);
							}
						} else {
							double avgWidth=getListAverage(widths);
							if(beginning[1]>multi4crossing*avgWidth && avgWidth>0) {
								Imgproc.line(matIn, new Point(beginning[0],y),  new Point(beginning[0]+beginning[1],y), new Scalar(100,255,100),1);
								lastNormal=foundBeginning;
								smallerCount=0;
								System.out.println("KREUZUNG?");
							} else {
								widths.add(beginning[1]);
								Imgproc.line(matIn, new Point(beginning[0],y),  new Point(beginning[0]+beginning[1],y), new Scalar(0,0,0),1);
							}
						}
					}
//					if(xLength>crossingSize)
					lastFound = false;
					xLength = 0;
				}
			}
			if (lastFound) {
				int[] beginning=new int[] { beginX, xLength };
				int[] foundBeginning=new int[2];
				lineContinues = checkIfLineContinues(beginning, lastBeginnings, widthIncrease,foundBeginning);
				if(lineContinues)
					foundSomething=true;
				if(!foundSomething)
					lineContinues=true;
				
				if (lineContinues) {
					currentBeginnings.add(beginning);
					if(lastNormal!=null) {
						if(foundBeginning[1]<1.5*lastNormal[1]) {
							widths.add(beginning[1]);
							int hysterese=(int) ((double)lastNormal[1]*1);
							int min=lastNormal[0]-hysterese;
							int max=lastNormal[0]+hysterese;
							if(min < foundBeginning[0] && max > foundBeginning[0]) {
								if(++smallerCount>=smallerAmtNeeded) {
									System.out.println("KREUZUNG!");
									return true;
								}
							}else {
								lastNormal=null;
							}
						} else {
							double avgWidth=getListAverage(widths);
							if(beginning[1]>multi4crossing*avgWidth && avgWidth>0) {
								lastNormal=foundBeginning;
								smallerCount=0;
								System.out.println("KREUZUNG?");
							} else {
								widths.add(beginning[1]);
							}
						}
					}
				}			
			}

			lastBeginnings.clear();
			lastBeginnings.addAll(currentBeginnings);
			currentBeginnings.clear();
			lastFound = false;
			xLength = 0;
		}
		return false;
	}

	private double getListAverage(List<Integer> numbers) {
		return numbers.stream().mapToInt(val -> val).average().orElse(0.0);

	}

	private boolean checkIfLineContinues(int[] beginning, List<int[]> lastBeginnings, int widthIncrease) {
		return checkIfLineContinues(beginning, lastBeginnings, widthIncrease, null);
	}

	private boolean checkIfLineContinues(int[] beginning, List<int[]> lastBeginnings, int widthIncrease,
			int[] foundBeginning) {
		for (int[] lbeginning : lastBeginnings) {
			int lx1 = lbeginning[0];
			int lx2 = lbeginning[0] + lbeginning[1];

			int x1 = beginning[0];
			int x2 = x1 + beginning[1];

			int hysterese = 3 * widthIncrease;
			if ((lx1 > x1 - hysterese && lx1 < x2 + hysterese) || (x1 > lx1 - hysterese && x1 < lx2 + hysterese)) {
				if (foundBeginning != null) {
					foundBeginning[0] = lbeginning[0];
					foundBeginning[1] = lbeginning[1];
				}
				return true;
			}
		}
		return false;
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
