package functions.matedit;

import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;

public class ContrastIncreasing extends MatEditFunction{

	public ContrastIncreasing(Boolean empty) {}

	public ContrastIncreasing() {
		super();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 704246551123112612L;
	private static volatile Image img;

	@Override
	protected Mat apply(Mat matIn) {
		Mat ret=new Mat();
		
		Mat[] channel = new Mat[] {new Mat(),new Mat(),new Mat()};

        Imgproc.cvtColor(matIn, ret, Imgproc.COLOR_BGR2Lab);

        // Extract the L channel
        for(int i=0;i<channel.length;i++) {
        	Core.extractChannel(ret, channel[i], i);

	        // apply the CLAHE algorithm to the L channel
	        CLAHE clahe = Imgproc.createCLAHE();
	        clahe.setClipLimit(4);
        	clahe.apply(channel[i], channel[i]);
	
	        // Merge the the color planes back into an Lab image
	        Core.insertChannel(channel[i], ret, i);
	        
	        // Temporary Mat not reused, so release from memory.
	        channel[i].release();
        }

        // convert back to RGB
        Imgproc.cvtColor(ret, ret, Imgproc.COLOR_Lab2BGR);

        return ret;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/contrast.png");
		return img;
	}
}
