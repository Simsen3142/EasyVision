package parameters.components;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;

import components.EditableLabel;
import main.MatReceiver;
import main.MatSender;
import main.ParameterReceiver;
import net.miginfocom.swing.MigLayout;
import parameters.NumberParameter;
import parameters.ParameterizedObject;
import parameters.group.RectangleParameterGroup;
import view.MatEditFunctionMatsPanel;
import view.MatReceiverPanel;

import javax.swing.border.LineBorder;

import org.opencv.core.Mat;

public class RectangleParameterGroupPanel extends JPanel implements IParameterGroupPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5918594102245381920L;
	private List<ParameterNumberSliderPanel> slpnlvals=new ArrayList<>();
	private RectangleParameterGroup rParamGroup;
	private EditableLabel label;
	private RectSelectionPanel rectPanel;
	private MatReceiver matRcvr;
	
	/**
	 * Create the panel.
	 */
	public RectangleParameterGroupPanel(RectangleParameterGroup rParamGroup) {
		this.rParamGroup=rParamGroup;
		
		setBorder(new LineBorder(new Color(0, 0, 0)));
		initialize();
	}
	
	public MatReceiver getMatRcvr() {
		return matRcvr;
	}

	private void initialize() {
		setLayout(new MigLayout("insets 0, gap 0", "[50%:n:50%,grow,center][grow,center]", "[20px][200px:n:200px,grow][70.00px:n:70px][70.00px:n:70px]"));
		
		label = new EditableLabel(rParamGroup.getName());
		add(label, "cell 0 0 2 1,alignx center");
		JPanel pnl=(JPanel) (matRcvr=new MatReceiverPanel() {
			private static final long serialVersionUID = -6585307938572848224L;

			@Override
			public void updateMat(Mat mat) {
				super.updateMat(mat);
				EventQueue.invokeLater(()->{
					rectPanel.revalidate();
					rectPanel.repaint();
				});
			}
		});
		rectPanel=new RectSelectionPanel(pnl);
		rectPanel.setOnRectChanged((rect)->{
			for(int i=0;i<rect.length;i++) {
				ParameterNumberSliderPanel slpnl=slpnlvals.get(i);
				double distance=slpnl.getMax().doubleValue()-slpnl.getMin().doubleValue();
				double val=slpnl.getMin().doubleValue()+distance*(rect[i]);
				slpnl.setValue(val, true);
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

					switch(index) {
						case 0: //x
							if(r[0]+r[2]>1) {
								ParameterNumberSliderPanel pnl=slpnlvals.get(2);
								double distance=pnl.getMax().doubleValue()-pnl.getMin().doubleValue();
								double val=pnl.getMin().doubleValue()+distance*(1-r[0]);
								System.out.println(1-r[0]+" IST DER WERT");
								System.out.println(val+" IST DER WERT");
								pnl.setValue(val, true);
							}
							break;
						case 1: //y
							if(r[1]+r[3]>1) {
								ParameterNumberSliderPanel pnl=slpnlvals.get(3);
								double distance=pnl.getMax().doubleValue()-pnl.getMin().doubleValue();
								double val=pnl.getMin().doubleValue()+distance*(1-r[1]);
								pnl.setValue(val, true);
							}
							break;
						case 2: //width
							if(r[0]+r[2]>1) {
								ParameterNumberSliderPanel pnl=slpnlvals.get(0);
								double distance=pnl.getMax().doubleValue()-pnl.getMin().doubleValue();
								double val=pnl.getMin().doubleValue()+distance*(1-r[2]);
								pnl.setValue(val, true);
							}
							break;
						case 3: //height
							if(r[1]+r[3]>1) {
								ParameterNumberSliderPanel pnl=slpnlvals.get(1);
								double distance=pnl.getMax().doubleValue()-pnl.getMin().doubleValue();
								double val=pnl.getMin().doubleValue()+distance*(1-r[3]);
								pnl.setValue(val, true);
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
			slpnlvals.add(paramSldrpnl);
		}
		
		changeRectFunction.apply(null);		
	}
}
