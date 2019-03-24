package functions.matedit;

import java.awt.Image;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import database.ImageHandler;

public class LogicalNot extends MatEditFunction{

	public LogicalNot(Boolean empty) {}
	
	public LogicalNot() {
		super();
	}
	@Override
	protected Mat apply(Mat matIn) {
		int rows=matIn.rows();
		int cols=matIn.cols();
		
		Mat matOut=new Mat(rows,cols, CvType.CV_8U);
		for(int col=0;col<cols;col++) {
			for(int row=0;row<rows;row++) {
				if(matIn.get(row, col)[0]!=255)
					matOut.put(row, col, 255);
			}
		}
		
		return matOut;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/not.png");
	}
}
