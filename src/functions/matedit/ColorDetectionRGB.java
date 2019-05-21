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

public class ColorDetectionRGB extends MatEditFunction {

	private static final long serialVersionUID = 69493283231745755L;

	public ColorDetectionRGB(Boolean empty) {
	}
	
	public ColorDetectionRGB() {
		super(new ParameterGroup("ColorRange",
				new ColorParameterGroup("min", ColorType.RGB, 
						new DoubleParameter("r", 25, 0, 255),
						new DoubleParameter("g", 39, 0, 255), 
						new DoubleParameter("b", 43, 0, 255)
				),
				new ColorParameterGroup("max", ColorType.RGB, 
					new DoubleParameter("r", 95, 0, 255),
					new DoubleParameter("g", 255, 0, 255), 
					new DoubleParameter("b", 204, 0, 255)
				)
			)
		);
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/farberkennung.png");
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		
		Scalar min = new Scalar(getDoubleVal("ColorRange_min_b"), getDoubleVal("ColorRange_min_g"), getDoubleVal("ColorRange_min_r"));
		Scalar max = new Scalar(getDoubleVal("ColorRange_max_b"), getDoubleVal("ColorRange_max_g"), getDoubleVal("ColorRange_max_r"));
		
		Core.inRange(matOut, min, max, matOut);
		return matOut;
	}
	
}
