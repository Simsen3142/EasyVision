package parameters.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;

import components.EditableLabel;
import net.miginfocom.swing.MigLayout;
import parameters.NumberParameter;
import parameters.components.ParameterNumberSliderPanel.LabeledSlider;
import parameters.group.ColorParameterGroup;
import javax.swing.border.LineBorder;

public class ColorParameterGroupPanel extends JPanel implements IParameterGroupPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5918594102245381920L;
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
		setLayout(new MigLayout("insets 0, gap 0", "[33%:n:33%,center][grow,center][33%:n:33%,center]", "[20px][60.00px:n:60px]"));
		
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
					
//					revalidate();
//					repaint();
					
					return null;
				};
		
		List<LabeledSlider> sliders=new ArrayList<>();
		for(int i=0;i<3;i++) {
			ParameterNumberSliderPanel paramSldrpnl = new ParameterNumberSliderPanel((NumberParameter<?>) cParamGroup.getParameters().get(i));
			add(paramSldrpnl, "cell "+i+" 1");
			paramSldrpnl.declareOnSetValue(changeColorfnctn);
			parameterNumberSliderPanel_values.add(paramSldrpnl);
			
			LabeledSlider slider=paramSldrpnl.getLabeledSlider();
			sliders.add(slider);
		}
		
		int slidernr=0;
		for(LabeledSlider slider:sliders) {
			slider.setOpaque(false);
			final int nr=slidernr;
			slider.declareSliderPaint((g)->createGradient1(g,nr,sliders));
			slidernr++;
		}
		
		changeColorfnctn.apply(null);		
	}
	
	private Graphics createGradient1(Graphics g, int nr, List<LabeledSlider> sliders) {
		int[] color=getNumbersFromSliders(sliders);
		
		color[nr]=sliders.get(nr).getMaximum();
		
		Graphics2D g2=(Graphics2D)g;
		
		int width=sliders.get(2).getWidth();
		int height=sliders.get(2).getHeight();
		
		float amtSteps=10;
		for(int i=0;i<amtSteps;i++) {
			
			float v1=i/amtSteps;
			float v2=(i+1)/amtSteps;
			
			int[] colorOut1=color.clone();
			int[] colorOut2=color.clone();
			
			colorOut1[nr]*=v1;
			colorOut2[nr]*=v2;
			
			Color c1=cParamGroup.getColor(colorOut1[0], colorOut1[1], colorOut1[2]);
			Color c2=cParamGroup.getColor(colorOut2[0], colorOut2[1], colorOut2[2]);
			
			int x1=(int) (width*v1);
			int x2=(int) (width*v2);

			
			GradientPaint fromTo = new GradientPaint(x1, height, c1,
					x2, height, c2);
			
			g2.setPaint(fromTo);
			
			g2.fillRect(x1, 0, x2-x1, height);
		}
		
		return g;
	}
	
	private Graphics createGradient2(Graphics g, int nr, List<LabeledSlider> sliders) {
		int[] color1=getNumbersFromSliders(sliders);
		int[] color2=color1.clone();
		
		color1[nr]=sliders.get(nr).getMinimum();
		color2[nr]=sliders.get(nr).getMaximum();
		
		Graphics2D g2=(Graphics2D)g;
		
		int width=sliders.get(2).getWidth();
		int height=sliders.get(2).getHeight();
		
		int[] colorOut1=color1.clone();
		int[] colorOut2=color2.clone();
		
		Color c1=cParamGroup.getColor(colorOut1[0], colorOut1[1], colorOut1[2]);
		Color c2=cParamGroup.getColor(colorOut2[0], colorOut2[1], colorOut2[2]);
		
		GradientPaint fromTo = new GradientPaint(0, height, c1,
				width, height, c2);
		
		
		g2.setPaint(fromTo);
		g2.fillRect(0, 0, width, height);
		
		return g;
	}
	
	private int[] getNumbersFromSliders(List<LabeledSlider> sliders) {
		int[] color=new int[3];
		int i=0;
		for(LabeledSlider slider:sliders) {
			color[i++]=slider.getValue();
		}
		return color;
	}
}
