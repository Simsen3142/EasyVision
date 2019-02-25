package parameters.components;

import components.EditableLabel;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Function;

public class ParameterBooleanCheckboxPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1113056612697281442L;
	private BooleanParameter parameter;
	private JCheckBox checkBox;
	private ParameterBooleanCheckboxPanel pnl=this;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(checkBox!=null)
			checkBox.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
	}
	
	public void declareOnSetValue(Function<Void,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public ParameterBooleanCheckboxPanel(BooleanParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("", "[429px][21px]", "[grow]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");

		checkBox=new JCheckBox("");
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setValue(checkBox.isSelected());
				if(onSetValue!=null) {
					onSetValue.apply(null);
				}
			}
		});
		this.add(checkBox, "cell 1 0,alignx left,aligny center");
		this.setValue(parameter.getValue());
	}
	
	
	public JCheckBox getLabeledSlider() {
		return checkBox;
	}
	
	public void setValue(boolean val) {
		checkBox.setSelected(val);
		parameter.setValue(val);
	}
	
	public boolean getValue() {
		return parameter.getValue();
	}
}
