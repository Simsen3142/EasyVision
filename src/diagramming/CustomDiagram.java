package diagramming;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLayeredPane;

public class CustomDiagram extends JLayeredPane {
	private static final long serialVersionUID = 1819079186185325112L;
	private List<CustomDiagramItem> diagramItems = Collections.synchronizedList(new ArrayList<>());
	private transient DiagramDragListener dragListener;
	private transient CustomDiagram instance = this;
	private transient CustomDiagramListenerTrigger listenerTrigger;
	private JPanel pnlDiagramItems;
	private JPanel pnlConnections;
	
	/**
	 * @return the listenerTrigger
	 */
	public CustomDiagramListenerTrigger getListenerTrigger() {
		return listenerTrigger;
	}

	/**
	 * @return the pnlDiagramItems
	 */
	public JPanel getPnlDiagramItems() {
		return pnlDiagramItems;
	}

	/**
	 * @return the pnlConnections
	 */
	public JPanel getPnlConnections() {
		return pnlConnections;
	}

	public void addDiagramListener(CustomDiagramListener listener) {
		listenerTrigger.getCustomDiagramListeners().add(listener);
	}
	
	public boolean removeDiagramListener(CustomDiagramListener listener) {
		return listenerTrigger.getCustomDiagramListeners().remove(listener);
	}
	
	public void clearDiagramListeners(CustomDiagramListener listener) {
		listenerTrigger.getCustomDiagramListeners().clear();
	}

	/**
	 * Create the panel.
	 */
	public CustomDiagram() {
		super();
		listenerTrigger=new CustomDiagramListenerTrigger();
		setLayout(null);
		dragListener = new DiagramDragListener();
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		addMouseListener(new ThisMouseListener());
		addComponentListener(new ResizeListener());
		
		pnlDiagramItems=new JPanel();
		pnlDiagramItems.setOpaque(false);
		pnlDiagramItems.setLayout(null);
		this.add(pnlDiagramItems);
		
		pnlConnections=new JPanel();
		pnlConnections.setOpaque(false);
		pnlConnections.setLayout(null);
		this.add(pnlConnections);
		
		setLayer(pnlDiagramItems,2);
		setLayer(pnlConnections,1);
	}
	
	public void clear() {
		while(diagramItems.size()>0) {
			diagramItems.get(0).deleteThis();
		}
	}

//	public static void main(String[] args) {
//		CustomDiagram dgrm=new CustomDiagram();
//		dgrm.addDiagramItem(new JButton("TEST"), new Point(10,100));
//		dgrm.addDiagramItem(new JButton("TEST"), new Point(250,100));
//		new PanelFrame(dgrm).setVisible(true);
//	}

	public void addDiagramItem(JComponent component, Point position) {
		CustomDiagramItem diagramItem=new CustomDiagramItem(component, this);
		this.addDiagramItem(diagramItem, position);
		listenerTrigger.triggerOnDiagramItemCreated(diagramItem);
	}

	public void addDiagramItem(CustomDiagramItem item, Point position) {
		item.setDiagram(this);
		diagramItems.add(item);
		pnlDiagramItems.add(item);
		item.setBounds(position.x, position.y, 100, 50);
	}

	public void removeDiagramItem(CustomDiagramItem item) {
		diagramItems.remove(item);
		pnlDiagramItems.remove(item);
		if (item.getDiagram() == this)
			item.setDiagram(null);
		listenerTrigger.triggerOnDiagramItemDeleted(item);
	}
	
	public void addDiagramConnection(CustomDiagramItemConnection connection) {
		Rectangle visibleRect=getVisibleRect();
		connection.setBounds(visibleRect.x, visibleRect.y, 10, 10);
		pnlConnections.add(connection);
		listenerTrigger.triggerOnConnectionCreated(connection);
	}
	
	public void removeDiagramConnection(CustomDiagramItemConnection connection) {
		pnlConnections.remove(connection);
		this.repaint();
		listenerTrigger.triggerOnConnectionDeleted(connection);
	}

	/**
	 * @return the diagramItems
	 */
	public List<CustomDiagramItem> getDiagramItems() {
		return diagramItems;
	}

	private class ThisMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocus();
		}
	}
	
	private class ResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent arg0) {
			resizeThings();
		}
		
		private void resizeThings() {
			int x=0;
			int y=0;
			pnlConnections.setBounds(x,y, getWidth(), getHeight());
			pnlDiagramItems.setBounds(x,y, getWidth(), getHeight());
			
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}
	}

	private class DiagramDragListener extends MouseAdapter {
		private Point origin;
		private boolean dragging = false;

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON2 || (e.getButton()==MouseEvent.BUTTON1 && e.isControlDown())) {
				dragging = true;
				origin = new Point(e.getPoint());
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (dragging) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				dragging = false;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (dragging && origin != null) {
				JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, instance);
				if (viewPort != null) {
					int deltaX = origin.x - e.getX();
					int deltaY = origin.y - e.getY();

					Rectangle view = viewPort.getViewRect();
					view.x += deltaX;
					view.y += deltaY;

					instance.scrollRectToVisible(view);
				}
			}
		}
	}
}
