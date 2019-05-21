package parameters.components;

import javax.swing.JPanel;

import components.EditableLabel;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.group.ParameterGroup;

import javax.swing.BoxLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EmptyBorder;

public class ParameterGroupPanel extends JPanel implements IParameterGroupPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 263740774905185369L;
	private EditableLabel editableLabel;
	private ParameterGroup parameterGroup;
	private ParameterizedObject po;

	/**
	 * Create the panel.
	 */
	public ParameterGroupPanel(ParameterGroup parameterGroup, ParameterizedObject po) {
		this.parameterGroup=parameterGroup;
		this.po=po;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0), 3), new EmptyBorder(5, 3, 3, 3)));

		editableLabel = new EditableLabel(parameterGroup.getName());
		add(editableLabel);
		
		addChildren();
	}
	
	@Override
	public void addChildren() {
		for(ParameterObject paramObject:parameterGroup.getParameters()) {
			this.add(paramObject.getComponent(po));
		}
	}

}
