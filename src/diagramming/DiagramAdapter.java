package diagramming;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

public class DiagramAdapter implements DiagramListener {
	@Override
	public void onDeleteItem(DiagramItem diagramItem) {
	}
	@Override
	public void onDeleteConnection(DiagramItemConnection diagramItemConnection) {
	}
	@Override
	public void onCreateConnection(DiagramItemConnection connection) {
	}
	@Override
	public boolean onConnectionAvailable(DiagramItem from, DiagramItem to) {
		return true;
	}
	@Override
	public void onCreateItem(DiagramItem diagramItem) {
	}
	@Override
	public void onCopied(DiagramItem diagramItem) {
	}
	@Override
	public void onPasted(Transferable t,Point mouseLocation) {
	}
	@Override
	public void onItemSelectionChanged(DiagramItem diagramItem, boolean selected) {
	}
}
