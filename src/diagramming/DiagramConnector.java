package diagramming;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;

public class DiagramConnector {
	protected ArrayList<DiagramItemConnection> connections=new ArrayList<>();
	protected DiagramItem diagramItem;
	protected String name;
	protected Function<Void, Point> findPosition;
	protected int maxConnectionNumber=100;
	
	/**
	 * @return the connections
	 */
	public ArrayList<DiagramItemConnection> getConnections() {
		return connections;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * @return the diagramItem
	 */
	public DiagramItem getDiagramItem() {
		return diagramItem;
	} /* Type of Parameter is Void*/
	
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

	public DiagramConnector(DiagramItem diagramItem, Function<Void, Point> findPosition, String name) {
		this.diagramItem=diagramItem;
		this.name=name;
		this.findPosition=findPosition;
	}

	public DiagramConnector(DiagramItem diagramItem, Function<Void, Point> findPosition) {
		this(diagramItem,findPosition,"input");
	}
	
	public boolean isConnectionAllowed() {
		return connections.size()<maxConnectionNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diagramItem == null) ? 0 : diagramItem.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DiagramConnector))
			return false;
		DiagramConnector other = (DiagramConnector) obj;
		if (diagramItem == null) {
			if (other.diagramItem != null)
				return false;
		} else if (!diagramItem.equals(other.diagramItem))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
