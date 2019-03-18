package diagramming;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomDiagramListenerTrigger {
	private List<CustomDiagramListener> customDiagramListeners=Collections.synchronizedList(new ArrayList<>());
	
	/**
	 * @return the customDiagramListeners
	 */
	public List<CustomDiagramListener> getCustomDiagramListeners() {
		return customDiagramListeners;
	}

	public void triggerOnDiagramItemDeleted(CustomDiagramItem item) {
		customDiagramListeners.forEach((listener)->{
			listener.onDeleteItem(item);
		});
	}
	
	public void triggerOnDiagramItemCreated(CustomDiagramItem item) {
		customDiagramListeners.forEach((listener)->{
			listener.onCreateItem(item);
		});
	}
	
	public void triggerOnConnectionDeleted(CustomDiagramItemConnection connection) {
		customDiagramListeners.forEach((listener)->{
			listener.onDeleteConnection(connection);
		});
	}
	
	public void triggerOnConnectionCreated(CustomDiagramItemConnection connection) {
		customDiagramListeners.forEach((listener)->{
			listener.onCreateConnection(connection);
		});
	}
	
	public boolean triggerOnConnectionAvailable(CustomDiagramItem from, CustomDiagramItem to) {
		boolean connect=true;
		for(CustomDiagramListener listener:customDiagramListeners) {
			if(connect!=listener.onConnectionAvailable(from,to)) {
				connect=!connect;
				break;
			}
		}
		return connect;
	}
	
	public void triggerOnCopied(CustomDiagramItem diagramItem) {
		customDiagramListeners.forEach((listener)->{
			listener.onCopied(diagramItem);
		});
	}
	
	public void triggerOnPasted(Transferable t, Point mouseLocation) {
		customDiagramListeners.forEach((listener)->{
			listener.onPasted(t,mouseLocation);
		});
	}
}
