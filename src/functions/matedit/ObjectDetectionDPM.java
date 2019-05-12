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

import enums.DPMClassifiers;
import enums.HaarClassifiers;
import parameters.EnumParameter;
import parameters.components.ParameterEnumPanel;
import database.ImageHandler;

public class ObjectDetectionDPM extends MatEditFunction {
	private static volatile Image img;

	private static final long serialVersionUID = 1L;
	private int absoluteObjectSize=0;
	private transient CascadeClassifier objectCascade;
	private transient boolean loaded=false;
	
	
	public ObjectDetectionDPM(Boolean empty) {}

	public ObjectDetectionDPM() {
		super(
			new EnumParameter("DPMclassifier", DPMClassifiers.CAR)
		);	
		objectCascade=new CascadeClassifier();
	}
	

	
	private Mat chooseClassifier(Mat clone) {
		EnumParameter param=(EnumParameter)getParameter("DPMclassifier");
		if(!param.hasOnChange()) {
			param.setOnChange((newVal)->{
				loaded=false;
				return null;
			});
		}
		
		DPMClassifiers haar = (DPMClassifiers) param.getValue();
		switch(haar) {
			case  CAR:
				return detectcarDPM(clone);
			default:
				System.out.println("There is no such param!");
				return null;
		}		
	}
	
	// public methods
	// DPM
	public Mat detectcarDPM(Mat frame) {
		return startDetection("opencv/build/etc/dpmcascades/dpm_car.xml",frame);
	}
	
	//private methods
	// this methods can detect everything when there is a classifier available for this object
	private Mat startDetection(String classifierPath, Mat frame) { // classifierPath is the path to where the resource is located
		loadClassifier(classifierPath);
		detectAndDisplay(frame);
		
		return frame;
	}
	
	private CascadeClassifier getObjectCascade() {
		if(objectCascade==null) {
			objectCascade=new CascadeClassifier();
		}
		
		return objectCascade;
	}
	    
	private void loadClassifier(String classifierPath) {
		if(!loaded) {
			getObjectCascade().load(classifierPath);
	        loaded=true;
		}
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
		if (absoluteObjectSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				absoluteObjectSize = Math.round(height * 0.2f);
			}
		}
		
		// object detection
		objectCascade.detectMultiScale(grayFrame, found, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,new Size(absoluteObjectSize, absoluteObjectSize), new Size());
				
		// each rectangle in the matofrect is a detected object
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
			img = ImageHandler.getImage("res/icons/haar.png");
		return img;
	}
}
