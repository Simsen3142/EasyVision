package diagramming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
	
	public void triggerOnConnectionDeleted(CustomDiagramItemConnection connection) {
		customDiagramListeners.forEach((listener)->{
			listener.onDeleteConnection(connection);
		});
	}
}
