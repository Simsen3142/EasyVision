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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NumberParameter<?> parameter;
	private LabeledSlider slider;
	private ParameterNumberSliderPanel pnl=this;
	private EditableDoubleLabel lblMin;
	private EditableDoubleLabel lblVal;
	private EditableDoubleLabel lblMax;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	private ParameterNumberSliderPanel instance=this;
	
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
			lblMax.setText(max+"");
		}
		
		@Override
		public void setMinimum(int min) {
			super.setMinimum(min);
			lblMin.setText(min+"");
		}
		
		@Override
		public void setValue(int val) {
			super.setValue(val);
			lblVal.setText(val+"");
			
			if(instance.getValue().intValue()!=val)
				instance.setValue(val);
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
	
	public void declareOnSetValue(Function<Void,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public ParameterNumberSliderPanel(NumberParameter<?> parameter) {
		this.parameter=parameter;
		initialize();
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
		
		this.setValue(parameter.getValue(),false);
	}
	
	
	public LabeledSlider getLabeledSlider() {
		return slider;
	}
	
	private void setValue(Number val) {
		setValue(val,true);
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
			parameter.setValue(val);
			
			if(onSetValue!=null) {
				onSetValue.apply(null);
			}
		}
		
		lblVal.setText(getValue()+"");
	}
	
	public Number getValue() {
		return parameter.getValue();
	}
	

}
