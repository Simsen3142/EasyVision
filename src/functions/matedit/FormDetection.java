package functions.matedit;

import java.awt.Image;
import java.util.Vector;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import database.ImageHandler;
import enums.Forms;
import parameters.EnumParameter;
import parameters.components.ParameterEnumPanel;

public class FormDetection extends MatEditFunction {

	private static final long serialVersionUID = 7427096678751354797L;

	public FormDetection(Boolean empty) {
	}
	
	public FormDetection() {
		super(
			new EnumParameter("Form", Forms.circle)
		);	
	}
	
	
	public static Mat findChoosenForm(Mat frame) {
		Forms f = (Forms) ParameterEnumPanel.getSelected();
		switch(f) {
			case circle: 
				findCircles(frame);
				return frame;				
			case rectangle:
				findRectangles(frame);
				return frame;
			default: 
				return frame;
		}
			
	}
	
	
	private static void findCircles(Mat frame){
		Mat grayFrame = new Mat();
		MatOfRect found = new MatOfRect();
		double x = 0.0;
	    double y = 0.0;
	    int r = 0;
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// noise reduction
		Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(), 2,2);
		// detected circles
		Imgproc.HoughCircles(grayFrame, found, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0 );

		for( int i = 0; i < found.rows(); i++ )
		    {
		      double[] data = grayFrame.get(i, 0);
		      for(int j = 0 ; j < data.length ; j++){
		           x = data[0];
		           y = data[1];
		           r = (int) data[2];
		      }
		      
		      Point center = new Point(x,y);
		      // circle center
		      Imgproc.circle(grayFrame, center, 3, new Scalar(0,255,0), -1);
		      // circle outline
		      Imgproc.circle(grayFrame, center, r, new Scalar(0,0,255), 1);
		    }
	}
	
	private static void findRectangles(Mat frame) {
		
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matout = findChoosenForm(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/formerkennung.png");
	}
}



