package general;

import java.awt.Dimension;
import java.awt.Toolkit;

public class SystemParameters {
	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
}
