package parameters.group;

import java.awt.Color;

import javax.swing.JComponent;

import org.opencv.core.Scalar;

import parameters.IntegerParameter;
import parameters.NumberParameter;
import parameters.ParameterizedObject;
import parameters.components.ColorParameterGroupPanel;

public class ColorParameterGroup extends ParameterGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -216641001692023432L;

	public static enum ColorType{
		RGB, BGR, HSV
	}
	
	private ColorType type;
	
	public ColorParameterGroup(String name, ColorType type) {
		super(name);
		switch(type) {
			case RGB:
				addParameters(new IntegerParameter("r", 100,0,255),
						new IntegerParameter("g", 100,0,255),
						new IntegerParameter("b", 100,0,255));
				break;
			case BGR:
				addParameters(new IntegerParameter("b", 100,0,255),
						new IntegerParameter("g", 100,0,255),
						new IntegerParameter("r", 100,0,255));
				break;
			case HSV:
				addParameters(new IntegerParameter("h", 100,0,180),
						new IntegerParameter("s", 100,0,255),
						new IntegerParameter("v", 100,0,255));
				break;
		}
		
		this.type=type;
	}
	
	public ColorParameterGroup(String name, ColorType type, NumberParameter<?>...parameters) {
		super(name, parameters);
		this.type=type;
	}
	
	/**
	 * @return the type
	 */
	public ColorType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ColorType type) {
		this.type = type;
	}
	
	public Color getColor() {
		Number v1=((NumberParameter<?>)parameters.get(0)).getValue();
		Number v2=((NumberParameter<?>)parameters.get(1)).getValue();
		Number v3=((NumberParameter<?>)parameters.get(2)).getValue();
		
		return getColor(v1,v2,v3);
	}
	
	public Color getColor(Number... nrs) {
		switch(type) {
			case HSV:{
			 	return Color.getHSBColor(nrs[0].floatValue()/180f,nrs[1].floatValue()/255f,nrs[2].floatValue()/255f);
			}case RGB:{
			 	return new Color(nrs[0].intValue(),nrs[1].intValue(),nrs[2].intValue());
			}case BGR:{
			 	return new Color(nrs[0].intValue(),nrs[1].intValue(),nrs[2].intValue());
			}
		}
		
		return null;
	}
	
	public Scalar getColorOpencv() {
		Color c=getColor();
		return new Scalar(c.getBlue(),c.getGreen(),c.getRed());
	}
	
	public static int getBrightness(Color c) {
		return (int)Math.sqrt(
			      c.getRed() * c.getRed() * 0.241 + 
			      c.getGreen() * c.getGreen() * 0.691 + 
			      c.getBlue() * c.getBlue() * 0.068);
	}
	
	@Override
	public JComponent getComponent(ParameterizedObject po) {
		return new ColorParameterGroupPanel(this);
	}
}
