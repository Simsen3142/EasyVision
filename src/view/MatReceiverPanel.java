package view;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.opencv.core.*;

import database.ImageHandler;
import main.MatReceiver;
import main.MatSender;

public class MatReceiverPanel extends MatPanel implements MatReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -153725482784880394L;
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
	
	public void stop() {
		sender.removeMatReceiver(this);
	}
	
}
