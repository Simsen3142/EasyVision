package functions.matedit.multi;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.group.ColorParameterGroup;
import parameters.group.ColorParameterGroup.ColorType;

public class MarkOverlay extends MultiMatEditFunction {

	public MarkOverlay() {
		super(
				new ColorParameterGroup("markcolor", ColorType.RGB, 
					new DoubleParameter("r", 95, 0, 255),
					new DoubleParameter("g", 255, 0, 255), 
					new DoubleParameter("b", 204, 0, 255)
				),
				new IntegerParameter("size", 1,-1,20)
		);
	}
	
	public MarkOverlay(Boolean empty) {
	}
	
	@Override
	public int getNrFunctionInputs() {
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		return 2;
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat basic=matsIn.get(0);
		Mat overlay=matsIn.get(1);
		
		ArrayList<MatOfPoint> contoursOverlay = new ArrayList<>();
		Imgproc.findContours(overlay, contoursOverlay, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Scalar color = new Scalar(getDoubleVal("markcolor_b"), getDoubleVal("markcolor_g"), getDoubleVal("markcolor_r"));
		int thickness=getIntVal("size");
		Imgproc.drawContours(basic, contoursOverlay, -1, color, thickness);
		
		return basic;
	}

	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/markieren.png");
	}
}
