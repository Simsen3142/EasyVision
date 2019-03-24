package functions.matedit;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class NoiseRemoval extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public NoiseRemoval(Boolean empty) {}
	
	public NoiseRemoval() {
		super(
			new ParameterGroup("noise", 
				new ParameterGroup("erosion",
					new IntegerParameter("x", 2, 0, 100),
					new IntegerParameter("y", 2, 0, 100)
				),
				new ParameterGroup("dilation",
					new IntegerParameter("x", 2, 0, 100),
					new IntegerParameter("y", 2, 0, 100)
				)
			)
		);
	}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		int erosion_x = getIntVal("noise_erosion_x");
		int erosion_y = getIntVal("noise_erosion_y");
		int dilation_x = getIntVal("noise_dilation_x");
		int dilation_y = getIntVal("noise_dilation_y");
		removeNoise(matOut, erosion_x, erosion_y, dilation_x,
				dilation_y);
		return matOut;
	}
	
	private void removeNoise(Mat matIn, int erosionX, int erosionY, int dilationX, int dilationY) {
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosionX + 1, 2 * erosionY + 1));
		Imgproc.erode(matIn, matIn, element);
		element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilationX + 1, 2 * dilationY + 1));
		Imgproc.dilate(matIn, matIn, element);
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/rausch.png");
	}
}
