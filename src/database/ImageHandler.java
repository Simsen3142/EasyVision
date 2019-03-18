package database;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageHandler {
	private static boolean activated=true;
	
	/**
	 * @return the activated
	 */
	public static boolean isActivated() {
		return activated;
	}

	/**
	 * @param activated the activated to set
	 */
	public static void setActivated(boolean activated) {
		ImageHandler.activated = activated;
	}

	public static Image getImage(String path) {
		if(activated)
			return getImageIcon(path).getImage();
		return null;
	}
	
	public static ImageIcon getImageIcon(String path) {
		if(activated)
			return new ImageIcon(path);
		return null;
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
