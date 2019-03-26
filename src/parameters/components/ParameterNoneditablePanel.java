package parameters.components;

import components.EditableLabel;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.util.function.Function;

public class ParameterNoneditablePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1931597601519518349L;
	private ParameterNoneditablePanel pnl=this;
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
	
	public ParameterNoneditablePanel(Parameter parameter) {
		initialize(parameter);
	}
	
	private void initialize(Parameter parameter) {
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[50%][grow]", "[40px]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		lblValue = new EditableLabel(parameter.getValue().toString());
		lblValue.setEditable(false);
		add(lblValue, "cell 1 0,grow");

	}
}
