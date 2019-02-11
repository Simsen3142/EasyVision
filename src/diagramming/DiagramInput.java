package diagramming;

import java.awt.Point;
import java.util.function.Function;

public class DiagramInput extends DiagramConnector {

	public DiagramInput(CustomDiagramItem diagramItem, Function<Void, Point> position, String name) {
		super(diagramItem, position, name);
	}

	public DiagramInput(CustomDiagramItem diagramItem, Function<Void, Point> position) {
		this(diagramItem,position,"input");
		super.setMaxConnectionNumber(1);
	}
}
