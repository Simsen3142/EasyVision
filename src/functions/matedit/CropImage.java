package functions.matedit;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import parameters.IntegerParameter;

public class CropImage extends MatEditFunction{

	public CropImage() {
		super(
			new IntegerParameter("xL", 0,0,10000),
			new IntegerParameter("yU", 0,0,10000),
			new IntegerParameter("xR", 10,0,10000),
			new IntegerParameter("yD", 10,0,10000)
		);
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		int xL=getIntVal("xL");
		int xR=getIntVal("xR");
		int yD=getIntVal("yD");
		int yU=getIntVal("yU");
		
		int height=matIn.height();
		int width=matIn.width();
		Rect rect=new Rect(xL, yU, width-xL-xR, height-yD-yU);
		return new Mat(matIn, rect);
	}
	
}
