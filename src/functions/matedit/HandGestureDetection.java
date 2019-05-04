package functions.matedit;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import parameters.FileParameter;
import view.MatPanel;
import view.PanelFrame;

public class HandGestureDetection extends MatEditFunction {
	
	private transient MatOfPoint mpeace;
	private transient MatOfPoint mfist;
	private transient MatOfPoint mstop;
	
	public HandGestureDetection() {
		super(
			new FileParameter("peace", new File("data/handcontours/Peace1.jpg")),
			new FileParameter("fist", new File("data/handcontours/Fist1.jpg")),
			new FileParameter("stop", new File("data/handcontours/Stop1.jpg"))
		);
	}

	@Override
	protected Mat apply(Mat matIn) {
		if(mpeace==null) {
			mpeace=getContourFromPic("peace");
			mfist=getContourFromPic("fist");
			mstop=getContourFromPic("stop");
		}
		
		Mat mat=matIn.clone();

		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfPoint contour=contours.get(0);

		System.out.println(contour.get(0, 0)[0]);
		System.out.println(mpeace.get(0, 0)[0]);
		System.out.println(mfist.get(0, 0)[0]);
		System.out.println(mstop.get(0, 0)[0]);
		
		double vals[]=new double[3];
		
		vals[0]=Imgproc.matchShapes(mpeace,mstop,Imgproc.CV_CONTOURS_MATCH_I1,0);
		vals[1]=Imgproc.matchShapes(contour,mfist,Imgproc.CV_CONTOURS_MATCH_I2,0);
		vals[2]=Imgproc.matchShapes(contour,mstop,Imgproc.CV_CONTOURS_MATCH_I3,0);
		
		double lowest=100000;
		int index=0;
		for(int i=0;i<vals.length;i++) {
			System.out.println(i+": "+vals[i]);
			if(vals[i]<lowest) {
				lowest=vals[i];
				index=i;
			}
		}
		
		switch (index) {
		case 0:
			System.out.println("PEACE");
			break;
		case 1:
			System.out.println("FIST");
			break;
		case 2:
			System.out.println("STOP");
			break;

		default:
			break;
		}
		
		return mat;
	}
	
	private MatOfPoint getContourFromPic(String fileparamname) {
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat m=Imgcodecs.imread(getFileVal(fileparamname).getAbsolutePath());
		
		Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2GRAY);
		
		m.convertTo(m, CvType.CV_8U);
		new PanelFrame(new MatPanel(m)).setVisible(true);

		Imgproc.findContours(m, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		System.out.println(contours.size()+"###############################");
		System.out.println(contours.get(0).cols());
		System.out.println(contours.get(0).rows());
		
		return contours.get(0);
	}

}
