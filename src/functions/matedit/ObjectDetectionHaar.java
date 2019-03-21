package functions.matedit;

import java.awt.Image;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import enums.HaarClassifiers;
import parameters.EnumParameter;
import parameters.components.ParameterEnumPanel;
import database.ImageHandler;

public class ObjectDetectionHaar extends MatEditFunction {
	private static volatile Image img;

	private static final long serialVersionUID = 1L;
	private int absoluteFaceSize=0;
	private transient CascadeClassifier faceCascade;
	private transient boolean loaded=false;
	
	
	public ObjectDetectionHaar(Boolean empty) {}

	public ObjectDetectionHaar() {
		super(
			new EnumParameter("haarclassifier", HaarClassifiers.FRONTAL_FACE)
		);	
		faceCascade=new CascadeClassifier();
	}
	

	
	private Mat chooseClassifier(Mat clone) {
		EnumParameter param=(EnumParameter)getParameter("haarclassifier");
		if(!param.hasOnChange()) {
			param.setOnChange((newVal)->{
				loaded=false;
				return null;
			});
		}
		
		HaarClassifiers haar = (HaarClassifiers) param.getValue();
		switch(haar) {
			case  FRONTAL_FACE:
				return detectFrontalFaceHaar(clone);
			case PROFIL_FACE:
				return detectProfileFaceHaar(clone);
			case EYES:
				return detectEyesHaar(clone);
			case EYES_GLASSES:
				return detectEyesWithGlassesHaar(clone);
			case SMILING_FACE:
				return detectSmilingFaceHaar(clone);
			case UPPER_BODY:
				return detectUpperBodyHaar(clone);
			case FULL_BODY:
				return detectFullBodyHaar(clone);
			default:
				System.out.println("There is no such param!");
				return null;
		}		
	}
	
	// public methods
	// Haar
	public Mat detectEyesHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_eye.xml",frame);
	}
	
	public Mat detectEyesWithGlassesHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_eye_tree_eyeglasses.xml", frame);
	}
	
	public Mat detectFrontalFaceHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_frontalface_alt.xml", frame);
	}
	
	public Mat detectFullBodyHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_fullbody.xml", frame);
	}
	
	public Mat detectUpperBodyHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_upperbody.xml", frame);
	}
	
	public Mat detectSmilingFaceHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_smile.xml", frame);
	}
	
	public Mat detectProfileFaceHaar(Mat frame) {
		return startDetection("opencv/build/etc/haarcascades/haarcascade_profileface.xml", frame);
	}
	
	//private methods
	// this methods can detect everything when there is a classifier available for this object
	private Mat startDetection(String classifierPath, Mat frame) { // classifierPath is the path to where the resource is located
		loadClassifier(classifierPath);
		detectAndDisplay(frame);
		
		return frame;
	}
	
	private CascadeClassifier getFaceCascade() {
		if(faceCascade==null) {
			faceCascade=new CascadeClassifier();
		}
		
		return faceCascade;
	}
	    
	private void loadClassifier(String classifierPath) {
<<<<<<< HEAD
	//	System.out.println(new File(classifierPath).exists());
=======
>>>>>>> 453df690829ebc759ff97ee6a98b74d76c847746
		if(!loaded) {
			getFaceCascade().load(classifierPath);
	        loaded=true;
		}
<<<<<<< HEAD
     //   System.out.println(faceCascade.empty());
=======
>>>>>>> 453df690829ebc759ff97ee6a98b74d76c847746
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
<<<<<<< HEAD
		Mat matout =  chooseClassifier(matIn.clone());
=======
		Mat matout =  matIn.clone();
		chooseClassifier(matout);
>>>>>>> 453df690829ebc759ff97ee6a98b74d76c847746
		getMats().put("matout", matout);
		return matout;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/haar.png");
		return img;
	}
}
