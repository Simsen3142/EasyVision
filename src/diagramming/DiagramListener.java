package diagramming;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

public interface DiagramListener {
	public void onCreateItem(DiagramItem diagramItem);
	public void onDeleteItem(DiagramItem diagramItem);
	public void onDeleteConnection(DiagramItemConnection connection);
	public void onCreateConnection(DiagramItemConnection connection);
	public boolean onConnectionAvailable(DiagramItem from, DiagramItem to);
	public void onCopied(DiagramItem diagramItem);
	public void onPasted(Transferable t, Point mouseLocation);
	public void onItemSelectionChanged(DiagramItem diagramItem, boolean selected);
}
