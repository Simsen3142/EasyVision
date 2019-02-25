package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class FunctionPanel<type> extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 372519913729274044L;
	protected type function;
	private JLabel lblName;
	private static JPopupMenu popupMenu;

	public FunctionPanel(type function) {
		
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
	 * Create the panel.
	 */
	public FunctionPanel(type function, String name) {
		super();
		initialize(function, name);
	}

	protected void initialize(type function, String name) {
		this.function = function;
		lblName = new JLabel(name);

		addPopup(this);

		setLayout(new BorderLayout(0, 0));

		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName, BorderLayout.CENTER);

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
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
