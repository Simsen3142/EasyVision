package diagramming;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;

public class DiagramOutput extends DiagramConnector {
	public DiagramOutput(CustomDiagramItem diagramItem, Function<Void, Point> position, String name) {
		super(diagramItem, position, name);
	}

	public DiagramOutput(CustomDiagramItem diagramItem, Function<Void, Point> position) {
		this(diagramItem,position,"output");
	}
}
