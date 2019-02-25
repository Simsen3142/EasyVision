package functions.matedit;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class ChangeResolution extends MatEditFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2427446062843519325L;

	@Override
	protected Mat apply(Mat matIn) {
		int width=getIntVal("size_width");
		int height=getIntVal("size_height");
		
		Mat matOut=new Mat();
		
		Imgproc.resize(matIn, matOut, new Size(width,height));

		return matOut;
	}
	
	public ChangeResolution() {
		super(
			new ParameterGroup("size",
				new IntegerParameter("width", 200, 50, 2000),
				new IntegerParameter("height", 180, 50, 2000)
			)
		);	
	}

}
