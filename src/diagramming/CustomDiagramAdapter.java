package diagramming;

public class CustomDiagramAdapter implements CustomDiagramListener {
	@Override
	public void onDeleteItem(CustomDiagramItem diagramItem) {
	}
	@Override
	public void onDeleteConnection(CustomDiagramItemConnection diagramItemConnection) {
	}
	@Override
	public void onCreateConnection(CustomDiagramItemConnection connection) {
	}
	@Override
	public boolean onConnectionAvailable(CustomDiagramItem from, CustomDiagramItem to) {
		return true;
	}
	@Override
	public void onCreateItem(CustomDiagramItem diagramItem) {
	}
}
