package parameters.components;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;

import components.EditableLabel;
import main.MatSender;
import net.miginfocom.swing.MigLayout;
import parameters.NumberParameter;
import parameters.ParameterizedObject;
import parameters.group.RectangleParameterGroup;
import view.MatEditFunctionMatsPanel;
import view.MatReceiverPanel;

import javax.swing.border.LineBorder;

public class RectangleParameterGroupPanel extends JPanel implements IParameterGroupPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5918594102245381920L;
	private List<ParameterNumberSliderPanel> parameterNumberSliderPanel_values=new ArrayList<>();
	private RectangleParameterGroup rParamGroup;
	private ParameterizedObject po;
	private EditableLabel label;
	private RectSelectionPanel rectPanel;
	
	/**
	 * Create the panel.
	 */
	public RectangleParameterGroupPanel(RectangleParameterGroup rParamGroup, ParameterizedObject po) {
		this.rParamGroup=rParamGroup;
		this.po=po;
		
		setBorder(new LineBorder(new Color(0, 0, 0)));
		initialize();
	}

	private void initialize() {
		setLayout(new MigLayout("insets 0, gap 0", "[50%:n:50%,grow,center][grow,center]", "[20px][200px:n:200px,grow][70.00px:n:70px][70.00px:n:70px]"));
		
		label = new EditableLabel(rParamGroup.getName());
		add(label, "cell 0 0 2 1,alignx center");
		
		JPanel pnl=null;
		if(po instanceof MatSender) {
			pnl=new MatEditFunctionMatsPanel((MatSender) po,"input");
		}
		rectPanel=new RectSelectionPanel(pnl);
		rectPanel.setOnRectChanged((rect)->{
			for(int i=0;i<rect.length;i++) {
				parameterNumberSliderPanel_values.get(i).setValue(rect[i]*100, true);
			}
			return null;
		});
		this.add(rectPanel,"cell 0 1 2 1,grow");
		addChildren();
	}

	@Override
	public void addChildren() {
		Function<String, Void> changeRectFunction=
				(name)->{
					int index=-1;
					for(int i=0;i<rParamGroup.getParameters().size();i++) {
						if(rParamGroup.getParameters().get(i).getName().equals(name)) {
							index=i;
							break;
						}
					}
					double[] r=rParamGroup.getRect();

					System.out.println("INDEX IS "+index+" ##############################");
					switch(index) {
						case 0: //x
							if(r[0]+r[2]>1) {
								parameterNumberSliderPanel_values.get(2).setValue(100*(1-r[0]), true);
							}
							break;
						case 1: //y
							if(r[1]+r[3]>1) {
								parameterNumberSliderPanel_values.get(3).setValue(100*(1-r[1]), true);
							}
							break;
						case 2: //width
							if(r[0]+r[2]>1) {
								parameterNumberSliderPanel_values.get(0).setValue(100*(1-r[2]), true);
							}
							break;
						case 3: //height
							if(r[1]+r[3]>1) {
								parameterNumberSliderPanel_values.get(1).setValue(100*(1-r[3]), true);
							}
							break;
					}
					
					rectPanel.setRect(rParamGroup.getRect());
//					setBackground(c);
					
					EventQueue.invokeLater(()->{
						revalidate();
						repaint();
					});
					
					return null;
				};
		
		for(int i=0;i<4;i++) {
			ParameterNumberSliderPanel paramSldrpnl = new ParameterNumberSliderPanel((NumberParameter<?>) rParamGroup.getParameters().get(i));
			add(paramSldrpnl, "cell "+i%2+" "+(2+i/2));
			paramSldrpnl.declareOnSetValue(changeRectFunction);
			parameterNumberSliderPanel_values.add(paramSldrpnl);
		}
		
		changeRectFunction.apply(null);		
	}
}
