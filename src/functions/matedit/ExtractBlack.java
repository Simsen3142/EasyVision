package functions.matedit;

import java.awt.Image;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.ParameterGroup;

public class ExtractBlack extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 60977212850019L;

	public ExtractBlack(Boolean empty) {
	}

	public ExtractBlack() {
		super(new ParameterGroup("black", 
				new DoubleParameter("min", 140,0,255),
				new DoubleParameter("max", 255,0,255)
			)
		);
	}
	
	public static Mat apply(Mat matIn, double min, double max) {
		ExtractBlack extrBlack=new ExtractBlack();
		((DoubleParameter)extrBlack.getParameter("black_min")).setValue(min);
		((DoubleParameter)extrBlack.getParameter("black_max")).setValue(max);
		
		return extrBlack.apply(matIn);
	}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=new Mat(matIn.rows(), matIn.cols(), CvType.CV_8U);
		double min=getDoubleVal("black_min");
		double max=getDoubleVal("black_max");
		
		for (int y = 0; y < matIn.rows(); y++) {
			for (int x = 0; x < matIn.cols(); x++) {
				double[] data = matIn.get(y, x);
				double r = data[0];
				double g = data[1];
				double b = data[2];

				// calculate the distance between R, B and G
				double distance = Math.sqrt((r - g) * (r - g) + (r - b) * (r - b) + (g - b) * (g - b));

				// black_ratio = (1 - distance / 411)^4
				double black_ratio = 1 - (distance / 411);
				
				if (black_ratio < 0) {
					black_ratio = 0;
				}
				black_ratio = black_ratio * black_ratio;
				black_ratio = black_ratio * black_ratio;

				// grayscale
				int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
				int p = (int) ((255 - gray) * black_ratio);

				// binarize
				if (p >= min && p<=max) {
					matOut.put(y, x, 255);
				}else {
					matOut.put(y, x, 0);
				}
			}
		}
		
		return matOut;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/schwarzerkennung.png");
	}
}
