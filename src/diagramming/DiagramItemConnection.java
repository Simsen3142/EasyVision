package diagramming;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DiagramItemConnection extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2421362256939007590L;
	private boolean selected = false;
	private DiagramOutput from;
	private DiagramInput to;
	private Point pnt1;
	private Point pnt2;
	private transient List<int[]> lines=new ArrayList<>();
	private final DiagramItemConnection instance=this;

	private static Polygon arrowHead;

	/**
	 * @return the from
	 */
	public DiagramOutput getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public DiagramInput getTo() {
		return to;
	}
	
	/**
	 * @param from the from to set
	 */
	public void setFrom(DiagramOutput from) {
		this.from = from;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(DiagramInput to) {
		this.to = to;
	}

	/**
	 * Create the panel.
	 */
	public DiagramItemConnection(DiagramOutput from, DiagramInput to) {
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
		
		int highestX = x1 > x2 ? x1 : x2;
		int highestY = y1 > y2 ? y1 : y2;

		Rectangle bounds = new Rectangle(lowestX - 15, lowestY - 15, highestX - lowestX + 30, highestY - lowestY + 30);
		if (!getBounds().equals(bounds)) {
			this.setBounds(bounds);
		}

		
		pnt1 = new Point(x1 - this.getX(), y1 - this.getY());
		pnt2 = new Point(x2 - this.getX(), y2 - this.getY());

		Graphics2D g2 = (Graphics2D) g;

		int dx = pnt2.x - pnt1.x;
		int dy = pnt2.y - pnt1.y;

		g2.setColor(selected ? Color.GREEN : getForeground());

		lines.clear();
		if (dx > 0) {
			int middleX = pnt1.x + dx / 2;
			lines.add(new int[] {pnt1.x, pnt1.y, pnt1.x + dx / 2, pnt1.y});
			lines.add(new int[] {pnt1.x, pnt1.y, middleX, pnt1.y});
			lines.add(new int[] {middleX, pnt1.y, middleX, pnt2.y});
			lines.add(new int[] {middleX, pnt2.y, pnt2.x, pnt2.y});
		} else {
			int middleY = pnt1.y + dy / 2;
			int xout = pnt1.x + 10;
			int xin = pnt2.x - 10;
			
			lines.add(new int[] {pnt1.x, pnt1.y, xout, pnt1.y});
			lines.add(new int[] {xout, pnt1.y, xout, middleY});
			lines.add(new int[] {xout, middleY, xin, middleY});
			lines.add(new int[] {xin, middleY, xin, pnt2.y});
			lines.add(new int[] {xin, pnt2.y, pnt2.x, pnt2.y});
		}
		
		drawLines(g2);
		drawArrowHead(g2, pnt2);
	}
	
	private void drawLines(Graphics2D g2) {
		for(int[] line:lines) {
			g2.drawLine(line[0], line[1], line[2], line[3]);
		}
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
		Diagram diagram = (Diagram) getParent().getParent();
		diagram.removeDiagramConnection(this);
	}

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}

	private class SelectionListener extends MouseAdapter implements FocusListener {
		@Override
		public void mousePressed(MouseEvent e) {
			
			if(selected) {
				transferFocus();
				return;
			}
			
			if(isLineClicked(e.getX(), e.getY())) {
				requestFocus();
			}else {
				if(getParent() instanceof JLayeredPane) {
					JLayeredPane pane=(JLayeredPane) getParent();
					pane.setLayer(instance, 0);
				}
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			selected = true;
			
			if(getParent() instanceof JLayeredPane) {
				JLayeredPane pane=(JLayeredPane) getParent();
				pane.setLayer(instance, 1);
			}
			
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});	
		}

		@Override
		public void focusLost(FocusEvent e) {
			selected = false;
			
			if(getParent() instanceof JLayeredPane) {
				JLayeredPane pane=(JLayeredPane) getParent();
				pane.setLayer(instance, 0);
			}
			
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}
		
		private boolean isLineClicked(int x, int y) {
			int distanceToHit=20;
			for(int[] line:lines) {
				if(Line2D.ptLineDist(line[0], line[1], line[2], line[3], x, y)<distanceToHit) {
					return true;
				}
			}
			return false;
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
