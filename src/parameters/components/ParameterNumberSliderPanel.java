package parameters.components;

import javax.swing.JSlider;
import components.EditableDoubleLabel;
import components.EditableLabel;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
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
		private static final long serialVersionUID = 1L;

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
			
			System.out.println(instance.getValue());
			System.out.println(val);
			if(instance.getValue().intValue()!=val)
				instance.setValue(val);
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
				if(x>=parameter.getMinValue().doubleValue()&&x<=parameter.getMaxValue().doubleValue()) {
					setValue((int)x,false);
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
		
		this.setValue(parameter.getValue(),false);
	}
	
	
	public LabeledSlider getLabeledSlider() {
		return slider;
	}
	
	private void setValue(Number val) {
		setValue(val,true);
		System.out.println("TEosdkzufszoi");
	}
	
	public void setValue(Number val, boolean withParameter) {
		System.out.println("min"+parameter.getMinValue());
		
		if(slider.getValue()!=val.intValue())
			slider.setValue(val.intValue());
		System.out.println(withParameter);
		if(withParameter) {
			parameter.setValue(val);
			if(onSetValue!=null) {
				onSetValue.apply(null);
			}
		}
	}
	
	public Number getValue() {
		return parameter.getValue();
	}
	

}
