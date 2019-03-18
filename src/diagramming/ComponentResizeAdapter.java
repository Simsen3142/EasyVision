package diagramming;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Function;

import javax.swing.*;

/**
 * @see http://stackoverflow.com/a/12456113/909085
 */

public class ComponentResizeAdapter extends MouseAdapter implements SwingConstants {
	private boolean resizing = false;
	private int prevX = -1;
	private int prevY = -1;
	private int resizeSide = 0;
	private Function<MouseEvent,Boolean> checkIfRightButton;
	private JComponent component;

	public static void install(JComponent component, int resizeSide, Function<MouseEvent, Boolean> checkIfRightButton) {
		ComponentResizeAdapter wra = new ComponentResizeAdapter(component, resizeSide,checkIfRightButton);
		component.addMouseListener(wra);
		component.addMouseMotionListener(wra);
		
		for(Component c:component.getComponents()) {
    		c.addMouseListener(wra);
    		c.addMouseMotionListener(wra);
    	}
	}

	public ComponentResizeAdapter(JComponent component, int resizeSide, Function<MouseEvent, Boolean> checkIfRightButton) {
		super();
		this.component=component;
		this.resizeSide=resizeSide;
		this.checkIfRightButton=checkIfRightButton;
	}

	public void mousePressed(MouseEvent e) {
		if (checkIfRightButton.apply(e)) {
			resizing = true;
			component.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		}
		prevX = e.getXOnScreen();
		prevY = e.getYOnScreen();
	}

	public void mouseDragged(MouseEvent e) {
		if (prevX != -1 && prevY != -1 && resizing) {
			Component c = component;
			Rectangle rect = c.getBounds();

			Dimension dim;
			dim = c.getPreferredSize();

			// Checking for minimal width and height
			int xInc = e.getXOnScreen() - prevX;
			int yInc = e.getYOnScreen() - prevY;
			
			if (resizeSide == SwingConstants.NORTH_WEST || resizeSide == SwingConstants.WEST
					|| resizeSide == SwingConstants.SOUTH_WEST) {
				if (rect.width - xInc < dim.width) {
					xInc = 0;
				}
			} else if (resizeSide == SwingConstants.NORTH_EAST || resizeSide == SwingConstants.EAST
					|| resizeSide == SwingConstants.SOUTH_EAST) {
				if (rect.width + xInc < dim.width) {
					xInc = 0;
				}
			}
			if (resizeSide == SwingConstants.NORTH_WEST || resizeSide == SwingConstants.NORTH
					|| resizeSide == SwingConstants.NORTH_EAST) {
				if (rect.height - yInc < dim.height) {
					yInc = 0;
				}
			} else if (resizeSide == SwingConstants.SOUTH_WEST || resizeSide == SwingConstants.SOUTH
					|| resizeSide == SwingConstants.SOUTH_EAST) {
				if (rect.height + yInc < dim.height) {
					yInc = 0;
				}
			}

			// Resizing window if any changes are done
			if (xInc != 0 || yInc != 0) {
				if (resizeSide == SwingConstants.NORTH_WEST) {
					c.setBounds(rect.x + xInc, rect.y + yInc, rect.width - xInc, rect.height - yInc);
				} else if (resizeSide == SwingConstants.NORTH) {
					c.setBounds(rect.x, rect.y + yInc, rect.width, rect.height - yInc);
				} else if (resizeSide == SwingConstants.NORTH_EAST) {
					c.setBounds(rect.x, rect.y + yInc, rect.width + xInc, rect.height - yInc);
				} else if (resizeSide == SwingConstants.WEST) {
					c.setBounds(rect.x + xInc, rect.y, rect.width - xInc, rect.height);
				} else if (resizeSide == SwingConstants.EAST) {
					c.setBounds(rect.x, rect.y, rect.width + xInc, rect.height);
				} else if (resizeSide == SwingConstants.SOUTH_WEST) {
					c.setBounds(rect.x + xInc, rect.y, rect.width - xInc, rect.height + yInc);
				} else if (resizeSide == SwingConstants.SOUTH) {
					c.setBounds(rect.x, rect.y, rect.width, rect.height + yInc);
				} else if (resizeSide == SwingConstants.SOUTH_EAST) {
					c.setBounds(rect.x, rect.y, rect.width + xInc, rect.height + yInc);
				}
				prevX = e.getXOnScreen();
				prevY = e.getYOnScreen();
				
				EventQueue.invokeLater(()->{
					c.getParent().revalidate();
					c.getParent().repaint();
				});
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		resizing = false;
		component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}