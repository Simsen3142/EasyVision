package main;

import java.util.Map;

import org.opencv.core.Mat;

public interface MatMapReceiver {
	void onReceive(Map<String,Mat> mats, MatSender sender);
}
