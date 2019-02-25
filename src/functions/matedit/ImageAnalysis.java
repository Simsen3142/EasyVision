package functions.matedit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageAnalysis {
	
	/**
	 * @param mat
	 * @return the average bgr color
	 */
	public static double[] getAverageColor(Mat mat) {
		Size s = new Size(10, 10);
		mat=mat.clone();
		Imgproc.resize(mat, mat, s);
	
		double[] bgrColor=new double[] {0,0,0};
		double numPixels=0;

		for(int x=0;x<mat.cols();x++) {
			for(int y=0;y<mat.rows();y++) {
				try {
					for(int i=0;i<3;i++) {
							bgrColor[i]+=mat.get(x, y)[i];
					}
					numPixels++;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(int i=0;i<3;i++) {
			bgrColor[i]/=numPixels;
		}
		return bgrColor;
	}
	
	public static double getDifference(Color c1, Color c2) {
		float[] hsb1=Color.RGBtoHSB(c1.getBlue(),c1.getGreen(),c1.getRed(), null);
		float[] hsb2=Color.RGBtoHSB(c2.getBlue(),c2.getGreen(),c2.getRed(), null);

		double difference=Math.abs(hsb1[0]-hsb2[0])
						+ Math.abs(hsb1[1]-hsb2[1])
						+ Math.abs(hsb1[2]-hsb2[2]);
		
		return difference;
	}
	
	public static double getBrightnessDifference(Color c1, Color c2) {
		float[] hsb1=Color.RGBtoHSB(c1.getBlue(),c1.getGreen(),c1.getRed(), null);
		float[] hsb2=Color.RGBtoHSB(c2.getBlue(),c2.getGreen(),c2.getRed(), null);

		double difference=Math.abs(hsb1[2]-hsb2[2]);
		
		return difference;
	}
	
	public static double[] getAverageColorOfPixelsInPolygon(Mat mat, Point...ps){
		Point[] points=getPixelCoordsInsidePolygon(ps);
		Mat testMat=new Mat(10,points.length/10,16);
		
		int i=0;
		for(int x=0;x<points.length/10;x++) {
			for(int y=0;y<10;y++) {
				testMat.put(y, x, 
						mat.get((int)points[i].y,(int)points[i].x));
				i++;
			}
		}

		return getAverageColor(testMat);
	}
	
	public static Point[] getPixelCoordsInsidePolygon(Point... points) {
		Rect rect = getRectangle(points);
		List<Point> ret=new ArrayList<Point>();
        for (int x = rect.x; x <= rect.x + rect.width; x++) {
            for (int y = rect.y; y <= rect.y + rect.height; y++) {
                if (inPoly(x, y, points))
                	ret.add(new Point(x, y));
            }
        }
	    return ret.toArray(new Point[ret.size()]);
	}
	
	private static Rect getRectangle(Point[] points) {
	    int x = -1, y = -1, width = -1, height = -1;
	    for (Point p:points) {
	        if (p.x < x || x == -1)
	            x = (int) p.x;
	        if (p.y < y || y == -1)
	            y = (int) p.y;


	        if (p.x > width || width == -1)
	            width = (int)p.x;
	        if (p.y > height || height == -1)
	            height = (int)p.y;


	    }
	    return new Rect(x, y, width-x, height-y);
	}
	
	public static boolean inPoly(int x, int y, Point[] points) {
	  int i, j;
	  boolean c=false;
	  for (i = 0, j = points.length-1; i < points.length; j = i++) {
	    if ( ((points[i].y>y) != (points[j].y>y)) &&
	     (x < (points[j].x-points[i].x) * (y-points[i].y) / (points[j].y-points[i].y) + points[i].x) )
	       c = !c;
	  }
	  return c;
	}
}
