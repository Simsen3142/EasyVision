package parameters.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;

import components.EditableLabel;
import net.miginfocom.swing.MigLayout;
import parameters.NumberParameter;
import parameters.group.ColorParameterGroup;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class ColorParameterGroupPanel extends JPanel implements IParameterGroupPanel{
	private List<ParameterNumberSliderPanel> parameterNumberSliderPanel_values=new ArrayList<>();
	private ColorParameterGroup cParamGroup;
	private EditableLabel label;
	
	/**
	 * Create the panel.
	 */
	public ColorParameterGroupPanel(ColorParameterGroup cParamGroup) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		this.cParamGroup=cParamGroup;
		initialize();
	}

	private void initialize() {
		setLayout(new MigLayout("insets 0, gap 0", "[grow][grow][grow]", "[][grow]"));
		
		label = new EditableLabel(cParamGroup.getName());
		add(label, "cell 1 0,alignx center");
		
		addChildren();
	}

	@Override
	public void addChildren() {
		Function<Void, Void> changeColorfnctn=
				(x)->{
					Color c=cParamGroup.getColor();
					setBackground(c);
					Color fg=ColorParameterGroup.getBrightness(c)<255/2?Color.WHITE:Color.BLACK;
					for(ParameterNumberSliderPanel paramSlider:parameterNumberSliderPanel_values) {
						paramSlider.setForeground(fg);
					}
					label.setForeground(fg);
					return null;
				};
		for(int i=0;i<3;i++) {
			ParameterNumberSliderPanel paramSldrpnl = new ParameterNumberSliderPanel((NumberParameter<?>) cParamGroup.getParameters().get(i));
			add(paramSldrpnl, "cell "+i+" 1");
			paramSldrpnl.declareOnSetValue(changeColorfnctn);
			parameterNumberSliderPanel_values.add(paramSldrpnl);
		}
		
		changeColorfnctn.apply(null);		
	}
}
