package database;

import java.awt.Image;
import java.util.function.Function;

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
	
	
	//ASYNCH
	public static void getImage(String path, Function<Image, Void> onReceive) {
		if(activated) {
			new Thread(()->{
				Image img = getImageIcon(path).getImage();
				onReceive.apply(img);
			}).start();
		}else {
			onReceive.apply(null);
		}
	}
	
	public static void getImageIcon(String path, Function<ImageIcon, Void> onReceive) {
		if(activated) {
			new Thread(()->{
				ImageIcon ic = new ImageIcon(path);
				onReceive.apply(ic);
			}).start();
		}else {
			onReceive.apply(null);
		}
	}
	
	public static void getScaledImageIcon(String path, int width, int height, int scaling, Function<ImageIcon, Void> onReceive) {
		if(activated) {
			new Thread(()->{
				ImageIcon ic = getScaledImageIcon(getImage(path),width,height,scaling);
				onReceive.apply(ic);
			}).start();
		}else {
			onReceive.apply(null);
		}
	}
	
	public static void getScaledImageIcon(Image image, int width, int height, int scaling, Function<ImageIcon, Void> onReceive) {
		if(activated) {
			new Thread(()->{
				if(image!=null) {
					ImageIcon ic = new ImageIcon(image.getScaledInstance(width,height,scaling));
					onReceive.apply(ic);
				}
			}).start();
		}else {
			onReceive.apply(null);
		}
	}
}
