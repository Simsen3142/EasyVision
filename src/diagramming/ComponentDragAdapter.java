package diagramming;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Function;

import javax.swing.*;

/**
 * @see http://stackoverflow.com/a/12456113/909085
 */

public class ComponentDragAdapter extends MouseAdapter implements SwingConstants {
	private boolean dragging = false;
	private int prevX = -1;
	private int prevY = -1;
	private Function<MouseEvent,Boolean> checkIfRightButton;
	private JComponent component;

	public static void install(JComponent component, Function<MouseEvent, Boolean> checkIfRightButton) {
		ComponentDragAdapter wra = new ComponentDragAdapter(component, checkIfRightButton);
		component.addMouseListener(wra);
		component.addMouseMotionListener(wra);
		
		for(Component c:component.getComponents()) {
    		c.addMouseListener(wra);
    		c.addMouseMotionListener(wra);
    	}
	}

	public ComponentDragAdapter(JComponent component, Function<MouseEvent, Boolean> checkIfRightButton) {
		super();
		this.component=component;
		this.checkIfRightButton=checkIfRightButton;
	}

	public void mousePressed(MouseEvent e) {
		if (checkIfRightButton.apply(e)) {
			dragging = true;
		}
		prevX = e.getXOnScreen();
		prevY = e.getYOnScreen();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (prevX != -1 && prevY != -1 && dragging) {
			Component c = component;
			Rectangle rect = c.getBounds();

			Dimension dim;
			dim = c.getPreferredSize();

			// Checking for minimal width and height
			int xInc = e.getXOnScreen() - prevX;
			int yInc = e.getYOnScreen() - prevY;
			

			// Resizing window if any changes are done
			if (xInc != 0 || yInc != 0) {
				c.setBounds(rect.x + xInc, rect.y + yInc, rect.width, rect.height);
				prevX = e.getXOnScreen();
				prevY = e.getYOnScreen();
				
				c.getParent().setVisible(false);
				c.getParent().setVisible(true);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		dragging = false;
	}
}