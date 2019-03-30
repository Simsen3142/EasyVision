package diagramming;

import java.awt.Point;
import java.util.function.Function;

public class DiagramOutput extends DiagramConnector {
	public DiagramOutput(DiagramItem diagramItem, Function<Void, Point> position, String name) {
		super(diagramItem, position, name);
	}

	public DiagramOutput(DiagramItem diagramItem, Function<Void, Point> position) {
		this(diagramItem,position,"output");
	}
}
