package parameters.components;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.EditableDoubleLabel;
import components.EditableLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.util.function.Function;

import javax.swing.border.MatteBorder;

public class ParameterNumberSliderPanel extends JPanel {
	private NumberParameter<?> parameter;
	private LabeledSlider slider;
	private ParameterNumberSliderPanel pnl=this;
	private EditableDoubleLabel lblMin;
	private EditableDoubleLabel lblVal;
	private EditableDoubleLabel lblMax;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	
	public class LabeledSlider extends JSlider{
		public LabeledSlider() {
			super();
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
			if(parameter instanceof DoubleParameter)
				parameter.setValue((double)val);
			else if(parameter instanceof IntegerParameter)
				parameter.setValue((int)val);
			else if(parameter instanceof LongParameter)
				parameter.setValue((long)val);
			
			if(onSetValue!=null) {
				onSetValue.apply(null);
			}
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
		setLayout(new MigLayout("insets 0, gap 0", "[33%][34%][33%]", "[33%:n:33%][33.00,grow][33%:n:33%]"));
		
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 1 0,alignx center,aligny center");

		lblMin = new EditableDoubleLabel("0");
		add(lblMin, "cell 0 2,growx");
		
		lblVal = new EditableDoubleLabel("0");
		lblVal.setEditable(true, 
			(txts)->{
				double x=Double.parseDouble(txts[0]);
				if(x>parameter.getMinValue().doubleValue()&&x<parameter.getMaxValue().doubleValue()) {
					slider.setValue((int)x);
					return txts[0];
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
		
		this.setValue(parameter.getValue());
	}
	
	
	public LabeledSlider getLabeledSlider() {
		return slider;
	}
	
	public void setValue(Number val) {
		slider.setValue(val.intValue());
		parameter.setValue(val);
	}
	
	public int getValue() {
		return slider.getValue();
	}
	

}
