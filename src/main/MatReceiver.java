package main;

import org.opencv.core.Mat;

public interface MatReceiver {
	void onReceive(Mat matIn, MatSender sender);
}
