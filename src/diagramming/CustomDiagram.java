package diagramming;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.mindfusion.diagramming.DiagramItem;

import view.PanelFrame;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class CustomDiagram extends JPanel {
	private List<CustomDiagramItem> diagramItems = new ArrayList<>();
	private DiagramDragListener dragListener;
	private CustomDiagram instance = this;

	/**
	 * Create the panel.
	 */
	public CustomDiagram() {
		super();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 300, Short.MAX_VALUE)
		);
		setLayout(groupLayout);
		dragListener = new DiagramDragListener();
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		addMouseListener(new ThisMouseListener());
	}

//	public static void main(String[] args) {
//		CustomDiagram dgrm=new CustomDiagram();
//		dgrm.addDiagramItem(new JButton("TEST"), new Point(10,100));
//		dgrm.addDiagramItem(new JButton("TEST"), new Point(250,100));
//		new PanelFrame(dgrm).setVisible(true);
//	}

	public void addDiagramItem(JComponent component, Point position) {
		this.addDiagramItem(new CustomDiagramItem(component, this), position);
	}

	public void addDiagramItem(CustomDiagramItem item, Point position) {
		item.setDiagram(this);
		diagramItems.add(item);
		this.add(item);
		item.setBounds(position.x, position.y, 100, 50);
	}

	public void removeDiagramItem(CustomDiagramItem item) {
		diagramItems.remove(item);
		this.remove(item);
		if (item.getDiagram() == this)
			item.setDiagram(null);
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
