package functions.matedit;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import database.ImageHandler;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.group.RectangleParameterGroup;

public class CropImage extends MatEditFunction{
	private static volatile Image img;

	public CropImage(Boolean empty) {
	}
	
	public CropImage() {
		super(
			new RectangleParameterGroup("rect", 
				new DoubleParameter("x", 0,0,100),
				new DoubleParameter("y", 0,0,100),
				new DoubleParameter("width", 100,0,100),
				new DoubleParameter("height", 100 ,0,100)
			)
		);
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		getMats().put("input",matIn.clone());
		
		int height=matIn.height();
		int width=matIn.width();
		Rect rect=((RectangleParameterGroup)getAllParameters().get("rect")).getRect(width, height);
		return new Mat(matIn, rect);
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/cropimage.png");
		return img;
	}
	
}
