package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import functions.RepresentationIcon;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class FunctionPanel<type> extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 372519913729274044L;
	protected type function;
	private JLabel lblName;
	private static JPopupMenu popupMenu;
	private Image image;

	public void hideLabel(boolean hide) {
		lblName.setVisible(!hide);
	}
	
	/**
	 * @return the function
	 */
	public type getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(type function) {
		this.function = function;
	}

	/**
	 * @wbp.parser.constructor
	 */
	public FunctionPanel(type function, String name) {
		super();
		initialize(function, name);
	}

	protected void initialize(type function, String name) {
		this.function = function;
		lblName = new JLabel(name);
		lblName.setFont(new Font("Arial", Font.BOLD, 20));
		
		if(function instanceof RepresentationIcon) {
			this.image=((RepresentationIcon) function).getRepresentationImage();
		}

		addPopup(this);

		setLayout(new BorderLayout(0, 0));

		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName, BorderLayout.CENTER);

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int imgWidth=image.getWidth(null);
		int imgHeight=image.getHeight(null);
		
		int width=getWidth();
		int height=getHeight();
		
		int retWidth=0;
		int retHeight=0;
		
		if(width>height) {
			retWidth=height*imgWidth/imgHeight;
			retHeight=height*imgHeight/imgWidth;
		}else {
			retWidth=width*imgWidth/imgHeight;
			retHeight=width*imgHeight/imgWidth;
		}
		
		if(retHeight>height) {
			retHeight=height;
			retWidth=retHeight*imgWidth/imgHeight;
		}else if(retWidth>width) {
			retWidth=width;
			retHeight=retWidth*imgHeight/imgWidth;
		}
		
		if(image!=null){
			g.drawImage(image, width/2-retWidth/2, height/2-retHeight/2,retWidth, retHeight, null);
		}
	}
	
	protected JPopupMenu createPopup() {
		popupMenu = new JPopupMenu();
		
		return popupMenu;
	}

	private void addPopup(Component component) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu=createPopup();
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu=createPopup();
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
