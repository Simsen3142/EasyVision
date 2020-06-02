package parameters.components;

import components.EditableLabel;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.util.function.Function;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ParameterStringMultiOptionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2441609677004898265L;
	private StringMultiOptionParameter parameter;
	private ParameterStringMultiOptionPanel pnl=this;
	private EditableLabel lblTitle;
	private Function<Void,Void> onSetValue;
	private JComboBox<String> cbxValue;
	private DefaultComboBoxModel<String> model;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
		if(cbxValue!=null)
			cbxValue.setForeground(c);
	}
	
	public void declareOnSetValue(Function<Void,Void> onSetValue) {
		this.onSetValue=onSetValue;
	}
	
	public ParameterStringMultiOptionPanel(StringMultiOptionParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[50%][grow]", "[40px]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		model=new DefaultComboBoxModel<String>();
		if(!parameter.getOptions().contains(parameter.getValue()))
			model.addElement(parameter.getValue());
		parameter.getOptions().forEach((option)->model.addElement(option));
		cbxValue = new JComboBox<String>(model);
		cbxValue.addItemListener(new CbxValueItemListener());
		cbxValue.setEditable(false);
		add(cbxValue, "cell 1 0,grow");

	}
	
	public void setValue(String val) {
		parameter.setValue(val);
		int index=model.getIndexOf(val);
		cbxValue.setSelectedIndex(index);
		
		if(onSetValue!=null) {
			onSetValue.apply(null);
		}
	}
	
	public String getValue() {
		return parameter.getValue();
	}
	
	private class CbxValueItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			parameter.setValue((String)cbxValue.getSelectedItem());
		}
	}
}
