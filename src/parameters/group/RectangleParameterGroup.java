package parameters.group;

import javax.swing.JComponent;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import main.MatReceiver;
import main.ParameterReceiver;
import parameters.DoubleParameter;
import parameters.ParameterizedObject;
import parameters.components.RectangleParameterGroupPanel;

public class RectangleParameterGroup extends ParameterGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -216641001692023432L;
	private transient RectangleParameterGroupPanel pnl;
	
	public MatReceiver getMatReceiver() {
		if(pnl==null) {
			pnl=(RectangleParameterGroupPanel)getComponent(null);
		}
		return pnl.getMatRcvr();		
	}
	
	public RectangleParameterGroup(String name, DoubleParameter...parameters) {
		super(name, parameters);
	}
	
	public double[] getRect() {
		double[] ret=new double[4];
		for(int i=0;i<4;i++) {
			DoubleParameter param=(DoubleParameter)parameters.get(i);
			double val=param.getValue();
			double min=param.getMinValue();
			double max=param.getMaxValue();
			ret[i]=(val-min)/(max-min);
		}
		
		return ret;
		
//		double x=((DoubleParameter)parameters.get(0)).getValue().doubleValue();
//		double y=((DoubleParameter)parameters.get(1)).getValue().doubleValue();
//		double width=((DoubleParameter)parameters.get(2)).getValue().doubleValue();
//		double height=((DoubleParameter)parameters.get(3)).getValue().doubleValue();
//		
//		return new double[] {x/100.0,y/100.0,width/100.0,height/100.0};
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
		if(pnl==null)
			pnl=new RectangleParameterGroupPanel(this);
		return pnl;
	}
}
