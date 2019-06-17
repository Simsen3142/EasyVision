package functions.matedit;

import java.awt.Image;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.BooleanParameter;

public class ContourInBoundChecker extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public ContourInBoundChecker(Boolean empty) {}
	
	public ContourInBoundChecker() {super(new BooleanParameter("found", false,false));}

	@Override
	protected Mat apply(Mat matIn) {
		
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat matOut=matIn.clone();

		Imgproc.findContours(matOut, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		((BooleanParameter)getParameter("found")).setValue(contours.size()!=0);

		return matOut;
	}
}
