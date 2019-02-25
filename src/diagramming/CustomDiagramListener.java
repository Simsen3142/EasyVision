package diagramming;

public interface CustomDiagramListener {
	public void onCreateItem(CustomDiagramItem diagramItem);
	public void onDeleteItem(CustomDiagramItem diagramItem);
	public void onDeleteConnection(CustomDiagramItemConnection connection);
	public void onCreateConnection(CustomDiagramItemConnection connection);
	public boolean onConnectionAvailable(CustomDiagramItem from, CustomDiagramItem to);
}
