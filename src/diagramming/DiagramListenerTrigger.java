package diagramming;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagramListenerTrigger {
	private List<DiagramListener> customDiagramListeners=Collections.synchronizedList(new ArrayList<>());
	
	/**
	 * @return the customDiagramListeners
	 */
	public List<DiagramListener> getCustomDiagramListeners() {
		return customDiagramListeners;
	}

	public void triggerOnDiagramItemDeleted(DiagramItem item) {
		customDiagramListeners.forEach((listener)->{
			listener.onDeleteItem(item);
		});
	}
	
	public void triggerOnDiagramItemCreated(DiagramItem item) {
		customDiagramListeners.forEach((listener)->{
			listener.onCreateItem(item);
		});
	}
	
	public void triggerOnConnectionDeleted(DiagramItemConnection connection) {
		customDiagramListeners.forEach((listener)->{
			listener.onDeleteConnection(connection);
		});
	}
	
	public void triggerOnConnectionCreated(DiagramItemConnection connection) {
		customDiagramListeners.forEach((listener)->{
			listener.onCreateConnection(connection);
		});
	}
	
	public boolean triggerOnConnectionAvailable(DiagramItem from, DiagramItem to) {
		boolean connect=true;
		for(DiagramListener listener:customDiagramListeners) {
			if(connect!=listener.onConnectionAvailable(from,to)) {
				connect=!connect;
				break;
			}
		}
		return connect;
	}
	
	public void triggerOnCopied(DiagramItem diagramItem) {
		customDiagramListeners.forEach((listener)->{
			listener.onCopied(diagramItem);
		});
	}
	
	public void triggerOnPasted(Transferable t, Point mouseLocation) {
		customDiagramListeners.forEach((listener)->{
			listener.onPasted(t,mouseLocation);
		});
	}
	
	public void triggerOnItemSelectionChanged(DiagramItem item, boolean selected) {
		customDiagramListeners.forEach((listener)->{
			listener.onItemSelectionChanged(item,selected);
		});
	}
}
