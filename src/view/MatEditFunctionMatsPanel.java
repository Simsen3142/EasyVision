package view;

import java.awt.EventQueue;
import java.util.Map;

import org.opencv.core.Mat;

import main.MatMapReceiver;
import main.MatSender;

public class MatEditFunctionMatsPanel extends MatPanel  implements MatMapReceiver{

	private String matName;

	public MatEditFunctionMatsPanel(MatSender sender, String matName) {
		sender.addMatMapReceiver(this);
		this.matName=matName;
	}

	@Override
	public void onReceive(Map<String, Mat> mats, MatSender sender) {
		if(showFps) {
			registerFrameForFPSCalculation();
			fps=getFps();
		}
		updateMat(mats.get(matName));
	}
}