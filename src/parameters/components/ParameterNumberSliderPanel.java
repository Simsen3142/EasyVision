package parameters.components;

import javax.swing.JSlider;
import components.EditableDoubleLabel;
import components.EditableLabel;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Function;

public class ParameterNumberSliderPanel extends JPanel {
	
	public static enum Type {
		DECIMAL, BINARY;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NumberParameter<?> parameter;
	private LabeledSlider slider;
	private EditableDoubleLabel lblMin;
	private EditableDoubleLabel lblVal;
	private EditableDoubleLabel lblMax;
	private EditableLabel lblTitle;
	private Function<String,Void> onSetValue;
	private ParameterNumberSliderPanel instance=this;
	private Type type=Type.DECIMAL;
	
	public class LabeledSlider extends JSlider{
		/**
		 * 
		 */
		private static final long serialVersionUID = -310439884404868136L;
		private Function<Graphics, Graphics> paint;
		
		public LabeledSlider() {
			super();
			setOpaque(false);
		}
		
		@Override
		public void setMaximum(int max) {
			super.setMaximum(max);
			new Integer(max);
			lblMax.setText(getNrString(max));
		}
		
		@Override
		public void setMinimum(int min) {
			super.setMinimum(min);
			lblMin.setText(getNrString(min));
		}
		
		@Override
		public void setValue(int val) {
			super.setValue(val);
			System.out.println(getNrString(val)+"SETZ DES DOCJ");
			lblVal.setText(getNrString(val));
			
			if(instance.getValue().intValue()!=val) {
				System.out.println("NEIN");
				instance.setValue(val);
			}
		}
		
		public void paint(Graphics g) {
			if(paint!=null) {
				g=paint.apply(g);
			}
			
			super.paintComponent(g);
		}
		
		public void declareSliderPaint(Function<Graphics, Graphics> paint) {
			this.paint=paint;
		}
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(slider!=null)
			slider.setForeground(c);
		if(lblMin!=null)
			lblMin.setForeground(c);
		if(lblMax!=null)
			lblMax.setForeground(c);
		if(lblVal!=null)
			lblVal.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
	}
	
	public void declareOnSetValue(Function<String,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public ParameterNumberSliderPanel(NumberParameter<?> parameter, Type type) {
		this.parameter=parameter;
		this.type=type;
		initialize();
	}
	
	public ParameterNumberSliderPanel(NumberParameter<?> parameter) {
		this(parameter,Type.DECIMAL);
	}
	
	public static String getBinaryNumberString(int nr, int length) {
		String s=Integer.toBinaryString(nr);
		for(int i=s.length();i<length;i++) {
			s="0"+s;
		}
		
		return s;
	}
	
	private String getNrString(int nr) {
		String s="";
		switch(type) {
		case BINARY:
			if(parameter instanceof BinaryIntParameter) {
				int l=Integer.toBinaryString(parameter.getMaxValue().intValue()).length();
				s=getBinaryNumberString(nr,l);
			}
			break;
		case DECIMAL:
			s=nr+"";
			break;
		default:
			break;
		}
		
		return s;
		
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[:33%:200px][grow][:33%:200px]", "[30px][38.00px][30px]"));
		
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 1 0,alignx center,aligny center");

		lblMin = new EditableDoubleLabel("0");
		add(lblMin, "cell 0 2,growx");
		
		lblVal = new EditableDoubleLabel("0");
		lblVal.setEditable(true, 
			(txts)->{
				Number x=Double.parseDouble(txts[0]);
				if(x.doubleValue()>=parameter.getMinValue().doubleValue()
						&&x.doubleValue()<=parameter.getMaxValue().doubleValue()) {
					if(parameter instanceof IntegerParameter) {
						x=x.intValue();
					}
					setValue(x,true);
					return x+"";
				} else
					return txts[1];
			}
		);
		add(lblVal, "cell 1 2,growx");
		
		lblMax = new EditableDoubleLabel("0");
		add(lblMax, "cell 2 2,growx");
		
		slider=new LabeledSlider();
		slider.setMinimum(parameter.getMinValue().intValue());
		slider.setMaximum(parameter.getMaxValue().intValue());
		add(slider, "cell 0 1 3 1,growx");
		
		if(parameter instanceof DoubleParameter) {
			if(((DoubleParameter) parameter).isPercent()) {
				slider.setMaximum(parameter.getMaxValue().intValue()*100);
				this.setValue(parameter.getValue().doubleValue()*100,false);
				System.out.println("SIASk");
			}else {
				this.setValue(parameter.getValue().doubleValue(),false);
			}
		}else
			this.setValue(parameter.getValue(),false);
	}
	
	
	public LabeledSlider getLabeledSlider() {
		return slider;
	}
	
	private void setValue(Number val) {
		setValue(val,true);
	}
	
	public Number getMin() {
		return parameter.getMinValue();
	}
	
	public Number getMax() {
		return parameter.getMaxValue();
	}
	
	public void setValue(Number val, boolean withParameter) {
		try {
			if(slider.getValue()!=val.intValue()) {
				slider.setValue(val.intValue());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(withParameter) {
			if(parameter instanceof DoubleParameter) {
				if(((DoubleParameter) parameter).isPercent()) {
					parameter.setValue(val.doubleValue()/100.0);
				}else {
					parameter.setValue(val.doubleValue());
				}
			}else {
				parameter.setValue(val);
			}
			
			if(onSetValue!=null) {
				onSetValue.apply(parameter.getName());
			}
		}
		if(type == Type.BINARY) {
			lblVal.setText(getNrString(getValue().intValue()));
		}else {
			String text="-";
			if(val instanceof Double) {
				text=String.format("%,.2f", getValue().doubleValue());
			}else {
				text=String.format("%,.0f", getValue().doubleValue());
			}
			System.out.println("TEXT = "+text);
			lblVal.setText(text);
		}
	}
	
	public Number getValue() {
		if(parameter instanceof DoubleParameter) {
			if(((DoubleParameter) parameter).isPercent()) {
				return parameter.getValue().doubleValue()*100;
			}
			return parameter.getValue().doubleValue();
		}
		return parameter.getValue();
	}
	

}
