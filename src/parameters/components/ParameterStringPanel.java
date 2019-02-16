package parameters.components;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.EditableDoubleLabel;
import components.EditableLabel;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Function;

import javax.swing.border.MatteBorder;
import javax.swing.BoxLayout;

public class ParameterStringPanel extends JPanel {
	private StringParameter parameter;
	private ParameterStringPanel pnl=this;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	private EditableLabel lblValue;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
		if(lblValue!=null)
			lblValue.setForeground(c);
	}
	
	public void declareOnSetValue(Function<Void,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public ParameterStringPanel(StringParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[50%][50%]", "[grow]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		lblValue = new EditableLabel(parameter.getValue());
		lblValue.setEditable(true,
			(txts)->{
				String newText=txts[0];
				
				int max=parameter.getMaxLength();
				int min=parameter.getMinLength();
				boolean limitMax=max>0;
				boolean limitMin=min>=0;
				if((newText.length()<=max || !limitMax) && (newText.length()>=min || !limitMin)) {
					setValue(newText);
					return newText;
				} else
					return txts[1];
			}
		);
		add(lblValue, "cell 1 0,grow");

	}
	
	
	public void setValue(String val) {
		parameter.setValue(val);
		lblValue.setText(val);
	}
	
	public String getValue() {
		return parameter.getValue();
	}
}
