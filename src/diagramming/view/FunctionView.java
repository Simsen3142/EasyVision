package diagramming.view;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import database.ImageHandler;
import functions.RepresentationIcon;
import parameters.ParameterizedObject;
import javax.swing.JLabel;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import java.awt.Graphics;

public class FunctionView extends JPanel {
	
	private String text;
	private ImageIcon icon;
	private JLabel lblText;
	private JLabel lblIcon;
	private int index;
	private boolean isLast;
	
	/**
	 * Create the panel.
	 */
	public FunctionView(String text, ImageIcon ic, int index, boolean isSelected, boolean isLast) {
		this.index=index;
		this.isLast=isLast;
		this.text=text;
		this.icon=ic;
		
		setOpaque(false);
		
		if(index%2==0)
			setBackground(Color.WHITE);
		else
			setBackground(new Color(230,230,231));
		
		if(isSelected)
			setBackground(new Color(230,230,255));
		
		initialize();
	}
	
	private void initialize() {
		setLayout(new MigLayout("insets 0 0 6, gap 0", "[68%,grow][10%]", "[36px:n:36px]"));
		lblText = new JLabel(text);
		lblText.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblText, "cell 0 0,alignx center,aligny center");
		
		lblIcon = new JLabel(icon);
		add(lblIcon, "cell 1 0,alignx center,aligny center");
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		int width=getWidth();
		int height=getHeight();
		int height2=height/2;
		System.out.println("TEST");
		if(!isLast) {
			g.fillRect(0, 0, width, height);
		}else {
			g.fillRoundRect(0, 0, width, height, 10, 10);
			g.fillRect(0, 0, width, height2+1);	
		}
		
		super.paint(g);
	}
	

}
