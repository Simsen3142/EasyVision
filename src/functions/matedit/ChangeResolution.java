package functions.matedit;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class ChangeResolution extends MatEditFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2427446062843519325L;
	private static volatile Image img;

	@Override
	protected Mat apply(Mat matIn) {
		int width=getIntVal("size_width");
		int height=getIntVal("size_height");
		
		Mat matOut=new Mat();
		
		Imgproc.resize(matIn, matOut, new Size(width,height));

		return matOut;
	}
	
	public ChangeResolution(Boolean empty) {}
	
	public ChangeResolution() {
		super(
			new ParameterGroup("size",
				new IntegerParameter("width", 200, 50, 2000),
				new IntegerParameter("height", 180, 50, 2000)
			)
		);	
	}

	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/aufloesung.png");
		return img;
	}

}
