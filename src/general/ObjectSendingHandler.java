package general;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

public class ObjectSendingHandler {
	public static void getBytesFromBitmap(BufferedImage bufferedImg, ObjectOutputStream oos) {
	    try {
			ImageIO.write(bufferedImg, "png", oos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToClipboard(String text) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection data=new StringSelection(text);
		c.setContents(data, null);
	}
}
