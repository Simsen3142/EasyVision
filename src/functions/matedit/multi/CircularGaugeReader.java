package functions.matedit.multi;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import main.MatSender;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.ParameterizedObject;
import parameters.group.ColorParameterGroup;
import parameters.group.ColorParameterGroup.ColorType;

public class CircularGaugeReader extends MultiMatEditFunction  {

	public CircularGaugeReader() {
		super(
				new ColorParameterGroup("markcolor", ColorType.RGB, 
					new DoubleParameter("r", 95, 0, 255),
					new DoubleParameter("g", 255, 0, 255), 
					new DoubleParameter("b", 204, 0, 255)
				),
				new IntegerParameter("size", 1,-1,20)
		);
	}
	
	public CircularGaugeReader(Boolean empty) {
	}
	
	@Override
	public int getNrFunctionInputs() {
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		return 3;
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat matOut=matsIn.get(0).clone();
		MatSender circle=getMatSenderByIndex(1);
		MatSender hand=getMatSenderByIndex(2);

		if(circle==null || hand ==null)
			return matOut;
			
		double xError=ParameterizedObject.getFirstFittingParameter(circle.getAllParameters(), DoubleParameter.class, "output_xerror").getValue();
		double yError=ParameterizedObject.getFirstFittingParameter(circle.getAllParameters(), DoubleParameter.class, "output_yerror").getValue();
		double x=xError+matOut.width()/2;
		double y=yError+matOut.height()/2;
		
		double radius=ParameterizedObject.getFirstFittingParameter(circle.getAllParameters(), DoubleParameter.class, "output_radius").getValue();
		
		double handAngle=ParameterizedObject.getFirstFittingParameter(hand.getAllParameters(), DoubleParameter.class, "output_angle").getValue();
		
		if(matsIn.get(1).size().equals(matsIn.get(2).size())) {
			try{
				DoubleParameter paramLineX=(DoubleParameter) hand.getParameter("line_centerx");
				DoubleParameter paramLineY=(DoubleParameter) hand.getParameter("line_centery");
				if(paramLineX!=null) {
					paramLineX.setValue(x);
				}
				if(paramLineY!=null) {
					paramLineY.setValue(y);
				}
			}catch (Exception e) {
			}
		}
		
		Point center = new Point(x, y);
		// circle center
		Imgproc.circle(matOut, center, 5, new Scalar(0, 255, 0), -1);
		// circle outline
		Imgproc.circle(matOut, center, (int) radius, new Scalar(0, 0, 255), 3);
		
		double lh=200;
		double ly=Math.sin(handAngle)*lh;
		double lx=Math.cos(handAngle)*lh;
		
		Imgproc.line(matOut, new Point(x,y), new Point(x+lx, y+ly), 
				new Scalar(0, 255, 0), 2, Imgproc.LINE_AA,0);
		
		Imgproc.putText(matOut,"Angle: "+String.format("%,.2f deg", handAngle*180/Math.PI), 
				new Point(12,12), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 0));


		return matOut;
	}

	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/markieren.png");
	}
}
