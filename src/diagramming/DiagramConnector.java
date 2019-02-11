package diagramming;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;

public class DiagramConnector {
	protected ArrayList<CustomDiagramItemConnection> connections=new ArrayList<>();
	protected CustomDiagramItem diagramItem;
	protected String name;
	protected Function<Void, Point> findPosition;
	protected int maxConnectionNumber=100;
	
	/**
	 * @return the connections
	 */
	public ArrayList<CustomDiagramItemConnection> getConnections() {
		return connections;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * @return the diagramItem
	 */
	public CustomDiagramItem getDiagramItem() {
		return diagramItem;
	}
	
	/**
	 * @return the maxConnectionNumber
	 */
	public int getMaxConnectionNumber() {
		return maxConnectionNumber;
	}
	/**
	 * @param maxConnectionNumber the maxConnectionNumber to set
	 */
	public void setMaxConnectionNumber(int maxConnectionNumber) {
		this.maxConnectionNumber = maxConnectionNumber;
	}

	/**
	 * @return the position
	 */
	public Point getPosition() {
		return findPosition.apply(null);
	}

	public DiagramConnector(CustomDiagramItem diagramItem, Function<Void, Point> findPosition, String name) {
		this.diagramItem=diagramItem;
		this.name=name;
		this.findPosition=findPosition;
	}

	public DiagramConnector(CustomDiagramItem diagramItem, Function<Void, Point> findPosition) {
		this(diagramItem,findPosition,"input");
	}
	
	public boolean isConnectionAllowed() {
		return connections.size()<maxConnectionNumber;
	}
}
