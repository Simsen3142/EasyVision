package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import database.ImageHandler;
import functions.RepresentationIcon;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import net.miginfocom.swing.MigLayout;
import view.PicturePanel;

public class FunctionPanel<type> extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 372519913729274044L;
	protected type function;
	private JLabel lblName;
	private static JPopupMenu popupMenu;
	private PicturePanel picturePanel;
	private static LayoutManager layoutHide;
	private static LayoutManager layoutVisible;

	public void hideLabel(boolean hide) {
		lblName.setVisible(!hide);
		picturePanel.setVisible(!hide);
		
		setLayout(hide?layoutHide:layoutVisible);
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
		addPopup(this);
		
		if(layoutVisible==null)
			layoutVisible=new MigLayout("insets 0 3 10 3, gap 0", "[grow]", "[50%][35.00,grow]");
		if(layoutHide==null)
			layoutHide=new BorderLayout(4,4);
		setLayout(layoutVisible);
		this.removeAll();

		lblName = new JLabel(name);
		lblName.setFont(new Font("Arial", Font.BOLD, 20));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName, "cell 0 0,grow");

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		if(function instanceof RepresentationIcon) {
			picturePanel = new PicturePanel(((RepresentationIcon) function).getRepresentationImage());
			add(picturePanel, "cell 0 1,grow");
		}
	}
	
	
	protected JPopupMenu createPopup() {
		popupMenu = new JPopupMenu();
		
		return popupMenu;
	}

	protected void addPopup(Component component) {
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
