package cvfunctions;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

// used sample: https://opencv-java-tutorials.readthedocs.io/en/latest/06-face-detection-and-tracking.html
// https://github.com/opencv-java/face-detection
// https://github.com/opencv-java/face-detection/blob/master/src/it/polito/teaching/cv/FaceDetectionController.java
public class FaceDetection extends MatEditFunction {
	private int absoluteFaceSize=0;
	private transient CascadeClassifier faceCascade;
	
	// public methods
	// Haar
	public Mat detectEyesHaar() {
		return startDetection("/EasyVision/opencv/build/etc/haarcascades/haarcascade_eye.xml");
	}
	
	public FaceDetection() {
		faceCascade=new CascadeClassifier();
	}
	
	public Mat detectEyesHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_eye.xml",frame);
	}
	
	public Mat detectEyesWithGlassesHaar() {
		return startDetection("EasyVision/EasyVision/opencv/build/etc/haarcascade/haarcascade_eye_tree_eyeglasses.xml");
	}
	
	public Mat detectFrontalFaceHaar() {
		return startDetection("EasyVision/src/resources/haarcascade_frontalface_alt.xml");
	}
	
	public Mat detectFullBodyHaar() {
		return startDetection("EasyVision/src/resources/haarcascade_fullbody.xml");
	}
	
	public Mat detectUpperBodyHaar() {
		return startDetection("EasyVision/src/resources/haarcascade_upperbody.xml");
	}
	
	public Mat detectSmilingFaceHaar() {
		return startDetection("EasyVision/src/resources/haarcascade_smile.xml");
	}
	
	public Mat detectProfileFaceHaar() {
		return startDetection("EasyVision/src/resources/haarcascade_profileface.xml");
	}
	
	// LBP
	public Mat detectFrontalFaceLBP() {
		return startDetection("EasyVision/src/resources/lbpcascade_frontalface_improved.xml");
	}
	 
	public Mat detectProfileFaceLBP() {
		return startDetection("EasyVision/src/resource/lbpcascade_profileface.xml");
	}
	
	//private methods
	// this method can detect everything when there is a classifier available for this object
	private Mat startDetection(String classifierPath) { // classifierPath is the path to where the resource is located
		Mat frame = new Mat();
				
		detectAndDisplay(frame);
		
		return frame;
	}
	
	private Mat startDetection(String classifierPath, Mat frame) { // classifierPath is the path to where the resource is located
		loadClassifier(classifierPath);
		detectAndDisplay(frame);
		
		return frame;
	}
	    
	private void loadClassifier(String classifierPath) {
		System.out.println(classifierPath);
		System.out.println(new File(classifierPath).exists());
        faceCascade.load(classifierPath);
        System.out.println(faceCascade.empty());
	}
	
	private void detectAndDisplay(Mat frame)
	{
		MatOfRect found = new MatOfRect();
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		// minimum face size
		if (absoluteFaceSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		
		// face detection
		faceCascade.detectMultiScale(grayFrame, found, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,new Size(absoluteFaceSize, absoluteFaceSize), new Size());
				
		// each rectangle in the matofrect faces is a face
		Rect[] foundArray = found.toArray();
		// draw every rectangle
		for (int i = 0; i < foundArray.length; i++) {
			Imgproc.rectangle(frame, foundArray[i].tl(), foundArray[i].br(), new Scalar(0, 255, 0), 3);
		}
			
	}

	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matout = detectEyesHaar(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}
	
}
