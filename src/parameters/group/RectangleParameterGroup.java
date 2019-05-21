package parameters.group;

import javax.swing.JComponent;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import parameters.DoubleParameter;
import parameters.ParameterizedObject;
import parameters.components.RectangleParameterGroupPanel;

public class RectangleParameterGroup extends ParameterGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -216641001692023432L;
	
	public RectangleParameterGroup(String name, DoubleParameter...parameters) {
		super(name, parameters);
	}
	
	public double[] getRect() {
		double x=((DoubleParameter)parameters.get(0)).getValue().doubleValue();
		double y=((DoubleParameter)parameters.get(1)).getValue().doubleValue();
		double width=((DoubleParameter)parameters.get(2)).getValue().doubleValue();
		double height=((DoubleParameter)parameters.get(3)).getValue().doubleValue();
		
		return new double[] {x/100.0,y/100.0,width/100.0,height/100.0};
	}
	
	public Rect getRect(int width, int height) {
		double[] r=getRect();
		
		int x=(int) (r[0]*width);
		int y=(int) (r[1]*height);
		
		int w=(int) (r[2]*width);
		int h=(int) (r[3]*height);
		
		return new Rect(x, y, w, h);
	}
	
	@Override
	public JComponent getComponent(ParameterizedObject po) {
		return new RectangleParameterGroupPanel(this,po);
	}
}
