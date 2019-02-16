package diagramming;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CustomDiagramItemConnection extends JPanel {

	private boolean selected = false;
	private DiagramConnector from;
	private DiagramConnector to;
	private Point pnt1;
	private Point pnt2;
	private AffineTransform tx = new AffineTransform();
	private CustomDiagramItemConnection instance = this;

	private static Polygon arrowHead;

	/**
	 * @return the from
	 */
	public DiagramConnector getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public DiagramConnector getTo() {
		return to;
	}

	/**
	 * Create the panel.
	 */
	public CustomDiagramItemConnection(DiagramOutput from, DiagramInput to) {
		super();
		addKeyListener(new ThisKeyListener());
		SelectionListener listener = new SelectionListener();
		addMouseListener(listener);
		addFocusListener(listener);
		this.from = from;
		this.to = to;
		this.setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Rectangle visiblePart=((JComponent)getParent()).getVisibleRect();
		
		int x1=from.getPosition().x;
		int x2=to.getPosition().x;
		
		int y1=from.getPosition().y;
		int y2=to.getPosition().y;

		int lowestX = x1 < x2 ? x1 : x2;
		lowestX=lowestX>visiblePart.x?lowestX:visiblePart.x;
		int lowestY = y1 < y2 ? y1 : y2;
		lowestY=lowestY>visiblePart.y?lowestY:visiblePart.y;

		int visiblePartMaxX=visiblePart.x+visiblePart.width;
		int visiblePartMaxY=visiblePart.y+visiblePart.height;
		
		int highestX = x1 > x2 ? x1 : x2;
		highestX=highestX<visiblePartMaxX?highestX:visiblePartMaxX;
		int highestY = y1 > y2 ? y1 : y2;
		highestY=highestY<visiblePartMaxY?highestY:visiblePartMaxY;

		Rectangle bounds = new Rectangle(lowestX - 15, lowestY - 15, highestX - lowestX + 30, highestY - lowestY + 30);
		if (!getBounds().equals(bounds)) {
			this.setBounds(bounds);
		}

		
		pnt1 = new Point(x1 - this.getX(), y1 - this.getY());
		pnt2 = new Point(x2 - this.getX(), y2 - this.getY());

		Graphics2D g2 = (Graphics2D) g;

		int dx = pnt2.x - pnt1.x;
		int dy = pnt2.y - pnt1.y;

		g2.setColor(selected ? Color.GREEN : Color.BLACK);

		if (dx > 0) {
			int middleX = pnt1.x + dx / 2;
			g2.drawLine(pnt1.x, pnt1.y, pnt1.x + dx / 2, pnt1.y);
			g2.drawLine(pnt1.x, pnt1.y, middleX, pnt1.y);
			g2.drawLine(middleX, pnt1.y, middleX, pnt2.y);

			g2.drawLine(middleX, pnt2.y, pnt2.x, pnt2.y);
		} else {
			int middleY = pnt1.y + dy / 2;
			int xout = pnt1.x + 10;
			g2.drawLine(pnt1.x, pnt1.y, xout, pnt1.y);
			g2.drawLine(xout, pnt1.y, xout, middleY);
			int xin = pnt2.x - 10;
			g2.drawLine(xout, middleY, xin, middleY);
			g2.drawLine(xin, middleY, xin, pnt2.y);
			g2.drawLine(xin, pnt2.y, pnt2.x, pnt2.y);
		}
		drawArrowHead(g2, pnt2);
	}

	private void drawArrowHead(Graphics2D g2d, Point p) {
		if (arrowHead == null) {
			arrowHead = new Polygon();
			arrowHead.addPoint(0, 5);
			arrowHead.addPoint(-5, -5);
			arrowHead.addPoint(5, -5);
		}
		g2d.translate(p.x - 2, p.y);
		g2d.rotate(Math.PI + Math.PI / 2);
		g2d.fill(arrowHead);
	}

	public void deleteConnection() {
		from.getDiagramItem().removeConnection(this);
		to.getDiagramItem().removeConnection(this);
		CustomDiagram diagram = (CustomDiagram) getParent();
		diagram.removeDiagramConnection(this);
	}

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}

	private class SelectionListener extends MouseAdapter implements FocusListener {
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocus();
		}

		@Override
		public void focusGained(FocusEvent e) {
			selected = true;
			getParent().repaint();
		}

		@Override
		public void focusLost(FocusEvent e) {
			selected = false;
			setVisible(false);
			setVisible(true);
		}
	}

	private class ThisKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent arg0) {
			if (selected) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					transferFocus();
				} else if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteConnection();
				}
			}
		}
	}

	

}
