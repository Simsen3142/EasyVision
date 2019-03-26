package functions;

import java.awt.Image;
import java.util.function.Function;

public interface RepresentationIcon {
	public Image getRepresentationImage();
	
	public void getRepresentationImage(Function<Image, Void> onReceive);
}
