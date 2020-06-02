package parameters.components;

import components.EditableLabel;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.util.function.Function;

public class ParameterStringPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2441609677004898265L;
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
		setLayout(new MigLayout("insets 0, gap 0", "[50%][grow]", "[40px]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		lblValue = new EditableLabel(parameter.getValue());
		if(parameter.isEditable()) {
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
		}
		add(lblValue, "cell 1 0,grow");

	}
	
	public void setValue(String val) {
		parameter.setValue(val);
		lblValue.setText(val);
		
		if(onSetValue!=null) {
			onSetValue.apply(null);
		}
	}
	
	public String getValue() {
		return parameter.getValue();
	}
}
