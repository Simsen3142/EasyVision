package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.group.ColorParameterGroup;
import parameters.group.ParameterGroup;
import parameters.group.ColorParameterGroup.ColorType;
import view.MatEditFunctionMatsPanel;
import view.PanelFrame;

public class ColorDetection extends MatEditFunction {

	private static final long serialVersionUID = 6949328861977145755L;

	public ColorDetection(Boolean empty) {
	}
	
	public ColorDetection() {
		super(new ParameterGroup("ColorRange",
				new ColorParameterGroup("min", ColorType.HSV, 
						new DoubleParameter("h", 0, 0, 255),
						new DoubleParameter("s", 0, 0, 255), 
						new DoubleParameter("v", 0, 0, 255)
					),
					new ColorParameterGroup("max", ColorType.HSV, 
						new DoubleParameter("h", 255, 0, 255),
						new DoubleParameter("s", 255, 0, 255), 
						new DoubleParameter("v", 50, 0, 255)
					)));
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/formerkennung.png");
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat grayFrame = new Mat();
		Mat inrange = new Mat();
		
		Scalar min = new Scalar(getDoubleVal("ColorRange_min_h"), getDoubleVal("ColorRange_min_s"), getDoubleVal("ColorRange_min_v"));
		Scalar max = new Scalar(getDoubleVal("ColorRange_max_h"), getDoubleVal("ColorRange_max_s"), getDoubleVal("ColorRange_max_v"));
		
		// Core.inRange(grayFrame, min, max, inrange);
		Mat matout = getMats().put("ColorRange", grayFrame);
		return matout;
	}

}
