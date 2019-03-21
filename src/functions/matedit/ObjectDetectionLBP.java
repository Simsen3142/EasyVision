package functions.matedit;

import java.awt.Image;
import java.io.File;

import parameters.EnumParameter;
import parameters.components.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import database.ImageHandler;
import enums.LBPclassifiers;

public class ObjectDetectionLBP extends MatEditFunction {
	private static volatile Image img;
	private int absoluteobjectsize = 0;
	private static final long serialVersionUID = 136775703156348096L;
	private transient CascadeClassifier faceCascade;
	private transient boolean loaded = false;


	public ObjectDetectionLBP(Boolean empty) {
	}
	
	
	public ObjectDetectionLBP() {
		super(
			new EnumParameter("lbpclassifier", LBPclassifiers.FRONTAL_FACE)
		);	
		faceCascade=new CascadeClassifier();
		EnumParameter param=(EnumParameter)getParameter("lbpclassifier");
		param.setOnChange((newVal)->{
			loaded=false;
			System.out.println("2ONCHANGEDUodikzajutzudila");
			return null;
		});
	}
	
	private Mat chooseClassifier(Mat clone) {
		EnumParameter param=(EnumParameter)getParameter("lbpclassifier");
		if(!param.hasOnChange()) {
			param.setOnChange((newVal)->{
				loaded=false;
				return null;
			});
		}
		
		LBPclassifiers lbp = (LBPclassifiers)param.getValue();
		switch(lbp) {
			case  FRONTAL_FACE:
				return detectFrontalFaceLBP(clone);
			case PROFIL_FACE:
				return detectProfileFaceLBP(clone);
			default:
				System.out.println("There is no such param!");
				return null;
		}		
	}
	
	// Public methods
	// LBP
	public Mat detectFrontalFaceLBP(Mat frame) {
		return startDetection("opencv/build/etc/lbpcascades/lbpcascade_frontalface_improved.xml", frame);
	}

	public Mat detectProfileFaceLBP(Mat frame) {
		return startDetection("opencv/build/etc/lbpcascades/lbpcascade_profileface.xml", frame);
	}

	// private methods
	// this method can detect everything when there is a classifier available for
	// this object
	private Mat startDetection(String classifierPath, Mat frame) { // classifierPath is the path to where the resource
																	// is located
		loadClassifier(classifierPath);
		detectAndDisplay(frame);

		return frame;
	}

	private CascadeClassifier getFaceCascade() {
		if (faceCascade == null) {
			faceCascade = new CascadeClassifier();
		}

		return faceCascade;
	}

	private void loadClassifier(String classifierPath) {
		System.out.println(classifierPath);
		//System.out.println(new File(classifierPath).exists());

		if (!loaded) {
			getFaceCascade().load(classifierPath);
			loaded = true;
		}

		//System.out.println(faceCascade.empty());

	}

	private void detectAndDisplay(Mat frame) {
		MatOfRect found = new MatOfRect();
		Mat grayFrame = new Mat();

		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);

		// minimum face size
		if (absoluteobjectsize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				absoluteobjectsize = Math.round(height * 0.2f);
			}
		}

		// face detection
		faceCascade.detectMultiScale(grayFrame, found, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(absoluteobjectsize, absoluteobjectsize), new Size());

		// each rectangle in the matofrect faces is a face
		Rect[] foundArray = found.toArray();
		// draw every rectangle
		for (int i = 0; i < foundArray.length; i++) {
			Imgproc.rectangle(frame, foundArray[i].tl(), foundArray[i].br(), new Scalar(0, 255, 0), 3);
		}

	}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matout =  chooseClassifier(matIn.clone());
		getMats().put("matout", matout);
		return matout;
	}

	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/lbp.png");
		return img;
	}

}
