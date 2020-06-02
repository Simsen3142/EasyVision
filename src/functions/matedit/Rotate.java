package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.ParameterGroup;

public class Rotate extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public Rotate(Boolean empty) {
	}
	
	public Rotate() {
		super(
//			new ParameterGroup("p1", 
//				new DoubleParameter("a1", 0,0,100,true),
//				new DoubleParameter("a2", 0,0,100,true),
//				new DoubleParameter("b1", 100,0,100,true),
//				new DoubleParameter("b2", 0,0,100,true),
//				new DoubleParameter("c1", 0,0,100,true),
//				new DoubleParameter("c2", 100,0,100,true)
//			),
//			new ParameterGroup("p2", 
//				new DoubleParameter("a1", 0,0,100,true),
//				new DoubleParameter("a2", 33,0,100,true),
//				new DoubleParameter("b1", 85,0,100,true),
//				new DoubleParameter("b2", 25,0,100,true),
//				new DoubleParameter("c1", 15,0,100,true),
//				new DoubleParameter("c2", 70,0,100,true)
//			),
			new DoubleParameter("angle", 90,0,360)
		);
	}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
	    Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
	    Mat warpMat = new Mat(2, 3, CvType.CV_32FC1);
	    Mat warpDest=new Mat();
	    
//	    MatOfPoint2f srcTri=new MatOfPoint2f();
//	    MatOfPoint2f dstTri=new MatOfPoint2f();
	    Mat destination = new Mat(matOut.rows(), matOut.cols(), matOut.type());

	    
//	    srcTri = new MatOfPoint2f(
//	    		new Point(matOut.cols()*getDoubleVal("p1_a1"), matOut.rows()*getDoubleVal("p1_a2")),
//	    		new Point(matOut.cols()*getDoubleVal("p1_b1"), matOut.rows()*getDoubleVal("p1_b2")),
//	    		new Point(matOut.cols()*getDoubleVal("p1_c1"), matOut.rows()*getDoubleVal("p1_c2"))
//    		);
//
//	    dstTri = new MatOfPoint2f(
//	    		new Point(matOut.cols()*getDoubleVal("p2_a1"), matOut.rows()*getDoubleVal("p2_a2")),
//	    		new Point(matOut.cols()*getDoubleVal("p2_b1"), matOut.rows()*getDoubleVal("p2_b2")),
//	    		new Point(matOut.cols()*getDoubleVal("p2_c1"), matOut.rows()*getDoubleVal("p2_c2"))
//    		);
//	    
//	    warpMat=Imgproc.getAffineTransform(srcTri, dstTri);
//	    
	    Size s=new Size(destination.width(),destination.height());
//	    Imgproc.warpAffine(matOut, warpDest, warpMat, s);
//
//	    
	    Point center = new Point(destination.cols() /2, destination.rows() / 2);
	    double scale = 1;
	    
	    rotMat = Imgproc.getRotationMatrix2D(center, getDoubleVal("angle"), scale);
//	    Imgproc.warpAffine(warpDest, destination, rotMat, s);

//	    rotMat=Imgproc.getRotationMatrix2D(center, angle, scale);
	    
	    Imgproc.warpAffine(matOut, destination, rotMat, destination.size());
//	    Imgproc.warpAffine(matOut, destination, rotMat, destination.size());
	    
	    return destination;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/drehen.png");
	}
}
