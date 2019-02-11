package cvfunctions;

import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

public class ContrastIncreasing extends MatEditFunction{

	@Override
	public Mat apply(Mat matIn) {
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
}
