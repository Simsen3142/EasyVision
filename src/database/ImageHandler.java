package database;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageHandler {
	public static Image getImage(String path) {
		return getImageIcon(path).getImage();
	}
	
	public static ImageIcon getImageIcon(String path) {
		return new ImageIcon(path);
	}
	
	public static ImageIcon getScaledImageIcon(String path, int width, int height, int scaling) {
		return getScaledImageIcon(getImage(path),width,height,scaling);
	}
	
	public static ImageIcon getScaledImageIcon(Image image, int width, int height, int scaling) {
		if(image!=null)
			return new ImageIcon(image.getScaledInstance(width,height,scaling));
		return null;
	}
}
