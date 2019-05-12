package functions.matedit;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import database.ImageHandler;
import enums.LBPclassifiers;

public class FaceRecognition extends MatEditFunction{

	public FaceRecognition(Boolean empty) {
		
	}
	
	public FaceRecognition() {

	}
	
	@Override
	protected Mat apply(Mat matIn) {
		return null;
	}

	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/face.png");
	}
}
