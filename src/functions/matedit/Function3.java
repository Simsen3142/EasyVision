package functions.matedit;

import java.awt.Image;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;

public class Function3 extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public Function3(Boolean empty) {}
	
	public Function3() {super();}

	@Override
	protected Mat apply(Mat matIn) {
		
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat matOut=matIn.clone();

		Imgproc.findContours(matOut, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		System.out.println(contours.size());
		return matOut;
	}
}
