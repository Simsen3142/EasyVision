package parameters.components;

import components.EditableLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import parameters.*;
import java.awt.Color;
import java.util.Collection;
import java.util.function.Function;

public class ParameterCollectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2441609677004898265L;
	private CollectionParameter parameter;
	private ParameterCollectionPanel pnl=this;
	private EditableLabel lblTitle;
	private EditableLabel lblValue;
	private JScrollPane scrollPane;
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if(lblTitle!=null)
			lblTitle.setForeground(c);
		if(lblValue!=null)
			lblValue.setForeground(c);
	}
	
	public ParameterCollectionPanel(CollectionParameter parameter) {
		this.parameter=parameter;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new MigLayout("insets 0, gap 0", "[50%][::90%]", "[40px]"));
		lblTitle = new EditableLabel(parameter.getName());
		add(lblTitle, "cell 0 0,growx,aligny center");
		
		lblValue = new EditableLabel(parameter==null?"null":parameter.getValue().toString());
		lblValue.setMaximumSize(lblValue.getParent().getSize());
		scrollPane=new JScrollPane(lblValue);

		add(scrollPane, "cell 1 0,grow");
	}
	
	
	public Collection<?> getValue() {
		return parameter.getValue();
	}
}
