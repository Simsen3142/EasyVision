package functions.matedit;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;

public class ToBlackAndWhite extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public ToBlackAndWhite(Boolean empty) {}
	
	public ToBlackAndWhite() {super();}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=new Mat();
		Imgproc.cvtColor(matIn, matOut, Imgproc.COLOR_BGR2GRAY);
		return matOut;
	}
}
