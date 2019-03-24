package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.ColorParameterGroup;
import parameters.group.ParameterGroup;
import parameters.group.ColorParameterGroup.ColorType;
import view.MatEditFunctionMatsPanel;
import view.PanelFrame;

public class ColorDetectionHSV extends MatEditFunction {

	private static final long serialVersionUID = 6949328861977145755L;

	public ColorDetectionHSV(Boolean empty) {
	}
	
	public ColorDetectionHSV() {
		super(new ParameterGroup("ColorRange",
				new ColorParameterGroup("min", ColorType.HSV, 
						new DoubleParameter("h", 25, 0, 255),
						new DoubleParameter("s", 97, 0, 255), 
						new DoubleParameter("v", 43, 0, 255)
					),
					new ColorParameterGroup("max", ColorType.HSV, 
						new DoubleParameter("h", 95, 0, 255),
						new DoubleParameter("s", 255, 0, 255), 
						new DoubleParameter("v", 204, 0, 255)
					)));
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/farberkennung.png");
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=new Mat();
		
		Imgproc.cvtColor(matIn, matOut, Imgproc.COLOR_BGR2HSV);
		
		Scalar min = new Scalar(getDoubleVal("ColorRange_min_h"), getDoubleVal("ColorRange_min_s"), getDoubleVal("ColorRange_min_v"));
		Scalar max = new Scalar(getDoubleVal("ColorRange_max_h"), getDoubleVal("ColorRange_max_s"), getDoubleVal("ColorRange_max_v"));

		Core.inRange(matOut, min, max, matOut);
		return matOut;
	}
}
