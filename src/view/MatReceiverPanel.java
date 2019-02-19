package view;

import java.awt.image.BufferedImage;
import org.opencv.core.*;
import main.MatReceiver;
import main.MatSender;

public class MatReceiverPanel extends MatPanel implements MatReceiver {

	private transient MatSender sender;
	
	/**
	 * @wbp.parser.constructor
	 */
	public MatReceiverPanel(MatSender sender) {
		this.sender = sender;
		sender.addMatReceiver(this);
	}
	
	public MatReceiverPanel(BufferedImage img) {
		image = img;
	}

	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		if(showFps) {
			registerFrameForFPSCalculation();
			fps=getFps();
		}
		this.updateMat(matIn);
	}
}
