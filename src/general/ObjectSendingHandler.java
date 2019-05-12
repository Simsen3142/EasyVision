package general;

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
}
