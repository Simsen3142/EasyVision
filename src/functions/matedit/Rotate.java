package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import database.ImageHandler;

public class Rotate extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public Rotate(Boolean empty) {}
	
	public Rotate() {super();}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		Core.flip(matOut, matOut, 1);
		return matOut;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/drehen.png");
	}
}
