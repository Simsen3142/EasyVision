package diagramming;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.BorderLayout;

public class CustomDiagramItem extends JPanel {
	private static final long serialVersionUID = 5435912444464919228L;
	private JComponent component;
	private CustomDiagram diagram;
	private ConnectingMouseMotionListener connectingListener;
	private ControlListener controlListener;
	private CustomDiagramItem instance = this;
	private HashMap<String, DiagramConnector> connectors=new HashMap<String, DiagramConnector>();
	
	private DiagramOutput selectedOutput;
	private DiagramInput selectedInput;

	public JComponent getComponent() {
		return component;
	}
	
	public void setComponent(JComponent component) {
		if (this.component != null) {
			connectingListener.uninstall(this.component);
			controlListener.uninstall(this.component);
			this.remove(this.component);
			this.component = component;
		}

		if (component != null) {
			this.add(component, BorderLayout.CENTER);
			connectingListener.install(component);
			controlListener.install(component);
			this.component=component;
		}
	}
	
	public DiagramConnector getConnector(String connectorName) {
		return connectors.get(connectorName);
	}
	
	public void addDiagramConnector(DiagramConnector connector) {
		connectors.put(connector.getName(), connector);
	}
	
	/**
	 * @return the connectors
	 */
	private HashMap<String, DiagramConnector> getConnectors() {
		return connectors;
	}
	
	/**
	 * @return the selectedOutput
	 */
	public DiagramOutput getSelectedOutput() {
		return selectedOutput;
	}
	
	public boolean setSelectedInput(String name) {
		DiagramConnector con=connectors.get(name);
		
		if(con!=null && con instanceof DiagramInput) {
			selectedInput=(DiagramInput)con;
			return true;
		}
		
		return false;
	}
	
	public boolean setSelectedOutput(String name) {
		DiagramConnector con=connectors.get(name);
		
		if(con!=null && con instanceof DiagramOutput) {
			selectedOutput=(DiagramOutput)con;
			return true;
		}
		
		return false;
	}

	/**
	 * @param selectedInput the selectedInput to set
	 */
	public DiagramInput getSelectedInput() {
		return selectedInput;
	}

	public ArrayList<DiagramInput> getInputs(){
		ArrayList<DiagramInput> ret=new ArrayList<>();
		
		for(DiagramConnector connector:connectors.values()) {
			if(connector instanceof DiagramInput) {
				ret.add((DiagramInput)connector);
			}
		}
		
		return ret;
	}
	
	public ArrayList<DiagramOutput> getOutputs(){
		ArrayList<DiagramOutput> ret=new ArrayList<>();
		
		for(DiagramConnector connector:connectors.values()) {
			if(connector instanceof DiagramOutput) {
				ret.add((DiagramOutput)connector);
			}
		}
		
		return ret;
	}
	
	public void deleteThis() {
		CustomDiagram diagrm=diagram;
		diagrm.removeDiagramItem(instance);
		
		List<CustomDiagramItemConnection> connections=new ArrayList<>();
		for(DiagramConnector connector:connectors.values()) {
			connector.getConnections().forEach((connection)->connections.add(connection));
		}
		connections.forEach((connection)->connection.deleteConnection());
		
		connectors.clear();
		connectors=null;
		
		connectingListener.uninstall(this);
		connectingListener.uninstall(component);
		connectingListener=null;
		
		controlListener.uninstall(this);
		controlListener.uninstall(component);
		controlListener=null;

		component=null;
		
		diagrm.repaint();
	}

	/**
	 * @return the diagram
	 */
	public CustomDiagram getDiagram() {
		return diagram;
	}

	/**
	 * @param diagram the diagram to set
	 */
	public void setDiagram(CustomDiagram diagram) {
		this.diagram = diagram;
	}
	
	public List<CustomDiagramItemConnection> getConnections() {
		List<CustomDiagramItemConnection> ret=new ArrayList<>();
		ret.addAll(getIncomingConnections());
		ret.addAll(getOutgoingConnections());
		
		return ret;
	}

	public List<CustomDiagramItemConnection> getOutgoingConnections(){
		List<CustomDiagramItemConnection> ret=new ArrayList<>();
		
		for(DiagramOutput output:getOutputs()) {
			for(CustomDiagramItemConnection con:output.getConnections()) {
				ret.add(con);
			}
		}
		return ret;
	}
	
	public List<CustomDiagramItemConnection> getIncomingConnections(){
		List<CustomDiagramItemConnection> ret=new ArrayList<>();
		
		for(DiagramInput input:getInputs()) {
			for(CustomDiagramItemConnection con:input.getConnections()) {
				ret.add(con);
			}
		}
		return ret;
	}
	
	public void removeConnection(CustomDiagramItemConnection connection) {
		for(DiagramConnector connector:connectors.values()) {
			ArrayList<CustomDiagramItemConnection> connections=connector.getConnections();
			for(int i=0;i<connections.size();i++) {
				if(connections.get(i).equals(connection)) {
					connections.remove(i);
					return;
				}
			}
		}
	}

	public CustomDiagramItem(JComponent component, CustomDiagram diagram) {
		controlListener=new ControlListener();
		controlListener.install(this);
		connectingListener = new ConnectingMouseMotionListener();
		connectingListener.install(this);
		this.diagram = diagram;
		
		DiagramInput input=new DiagramInput(this, (nix)-> {
			int x=this.getX();
			int y=this.getY()+this.getHeight()/2;
			
			return new Point(x,y);
		});
		
		DiagramOutput output=new DiagramOutput(this, (nix)-> {
			int x=this.getX()+this.getWidth();
			int y=this.getY()+this.getHeight()/2;
			
			return new Point(x,y);
		});
		
		selectedInput=input;
		selectedOutput=output;
		
		addDiagramConnector(input);
		addDiagramConnector(output);
		
		setBorder(Color.BLACK, 5);
		setLayout(new BorderLayout(0, 0));

		setComponent(component);

		ComponentResizeAdapter.install(this, SwingConstants.SOUTH_EAST, (e) -> {
			return e.getButton() == MouseEvent.BUTTON3 || e.isShiftDown();
		});
		ComponentDragAdapter.install(this, (e) -> {
			return e.getButton() == MouseEvent.BUTTON2 || e.isControlDown();
		});
		ComponentCursorAdapter.install(component, KeyEvent.VK_CONTROL, InputEvent.CTRL_DOWN_MASK,
				new Cursor(Cursor.HAND_CURSOR), new Cursor(Cursor.MOVE_CURSOR));
		ComponentCursorAdapter.install(component, KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK,
				new Cursor(Cursor.HAND_CURSOR), new Cursor(Cursor.SE_RESIZE_CURSOR));

		this.setCursor(new Cursor(Cursor.HAND_CURSOR));

		this.setSize(100, 50);
	}

	public void setBorder(Color c, int thickness) {
		this.setBorder(new LineBorder(c, thickness));
	}
	
	
	public void connectTo(CustomDiagramItem item) {
		if(checkIfAlreadyConnectedTo(item))
			return;
		CustomDiagramItemConnection connection=new CustomDiagramItemConnection(
				this.selectedOutput,
				item.selectedInput);
		
		this.selectedOutput.getConnections().add(connection);
		diagram.addDiagramConnection(connection);
		
		item.selectedInput.getConnections().add(connection);
		
		diagram.repaint();
	}
	
	public boolean checkIfAlreadyConnectedTo(CustomDiagramItem item) {
		for(CustomDiagramItemConnection con:getOutgoingConnections()) {
			if(con.getTo().equals(item)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((component == null) ? 0 : component.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CustomDiagramItem))
			return false;
		CustomDiagramItem other = (CustomDiagramItem) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		return true;
	}

	private class ConnectingMouseMotionListener extends MouseAdapter {
		private boolean connecting;
		private CustomDiagramItem diagramItemFound;

		public void install(JComponent component) {
			component.addMouseListener(this);
			component.addMouseMotionListener(this);
		}

		public void uninstall(JComponent component) {
			component.removeMouseListener(this);
			component.removeMouseMotionListener(this);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown() && !e.isShiftDown()) {
				connecting = true;
				setBorder(Color.GREEN, 5);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (connecting) {
				Point pnt = new Point(e.getX() + instance.getX(), e.getY() + instance.getY());
				Component component = diagram.getPnlDiagramItems().getComponentAt(pnt);
				if (component != instance 
						&& component != null 
						&& component instanceof CustomDiagramItem) {
					if (diagramItemFound != component) {
						if (diagramItemFound != null) {
							diagramItemFound.setBorder(Color.BLACK, 5);
						}
						
						CustomDiagramItem itemFound = (CustomDiagramItem) component;
						if(!checkIfAlreadyConnectedTo(itemFound) && itemFound.selectedInput.isConnectionAllowed() 
								&& diagram.getListenerTrigger().triggerOnConnectionAvailable(instance, itemFound)) {
							diagramItemFound=itemFound;
							diagramItemFound.setBorder(Color.GREEN, 5);
						}
					}
				} else {
					if (diagramItemFound != null) {
						diagramItemFound.setBorder(Color.BLACK, 5);
						diagramItemFound=null;
					}
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			connecting = false;
			if(!controlListener.selected) {
				setBorder(Color.BLACK, 5);
			}
			
			if(diagramItemFound!=null) {
				diagramItemFound.setBorder(Color.BLACK, 5);
				connectTo(diagramItemFound);
				diagramItemFound=null;
			}
		}
	}
	
	
	private class ControlListener extends MouseAdapter implements KeyListener, FocusListener {
		private boolean selected=false;
		private long lastTimeClicked=0;
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(checkIfDoubleClick(500)){
				setBorder(Color.LIGHT_GRAY, 5);
				selected=true;
			}
		}
		
		private boolean checkIfDoubleClick(int time4doubleClick) {
			long time=System.currentTimeMillis();
			boolean doubleclick=time-lastTimeClicked<time4doubleClick;
			lastTimeClicked=System.currentTimeMillis();
			
			return doubleclick;
		}
		
		public void install(JComponent component) {
			component.addMouseListener(this);
			component.addKeyListener(this);
			component.addFocusListener(this);
		}

		public void uninstall(JComponent component) {
			component.removeMouseListener(this);
			component.removeKeyListener(this);
			component.removeFocusListener(this);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(selected) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					diagram.requestFocus();
					break;
				case KeyEvent.VK_DELETE:
					deleteThis();
					break;
				case KeyEvent.VK_UP:
					setLocation(getX(), getY()-1);
					break;
				case KeyEvent.VK_DOWN:
					setLocation(getX(), getY()+1);
					break;
				case KeyEvent.VK_LEFT:
					setLocation(getX()-1, getY());
					break;
				case KeyEvent.VK_RIGHT:
					setLocation(getX()+1, getY());
					break;
	
				default:
					break;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			setBorder(Color.BLACK, 5);
			selected=false;
			lastTimeClicked=0;
		}
	}
}
