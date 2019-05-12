package functions.matedit;

import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.*;
import org.opencv.utils.*;
import java.util.*;

public class TextDetection extends MatEditFunction{

	    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	    public static void main(String[] args) {
	    	
	    	// jfile chooser im parameter panel!
	    	
	        // samples/data/digits.png, have a look at it.
	        Mat digits = Imgcodecs.imread("digits.png", 0);
	        // setup train/test data:
	        Mat trainData = new Mat(),
	            testData = new Mat();
	        List<Integer> trainLabs = new ArrayList<Integer>(),
	            testLabs = new ArrayList<Integer>();
	        // 10 digits a 5 rows:
	        for (int r=0; r<50; r++) { 
	            // 100 digits per row:
	            for (int c=0; c<100; c++) {
	                // crop out 1 digit:
	                Mat num = digits.submat(new Rect(c*20,r*20,20,20));
	                // we need float data for knn:
	                num.convertTo(num, CvType.CV_32F);
	                // 50/50 train/test split:
	                if (c % 2 == 0) {
	                    // for opencv ml, each feature has to be a single row:
	                    trainData.push_back(num.reshape(1,1));
	                    // add a label for that feature (the digit number):
	                    trainLabs.add(r/5);
	                } else {
	                    testData.push_back(num.reshape(1,1));
	                    testLabs.add(r/5);
	                }
	            }                
	        }

	        // make a Mat of the train labels, and train knn:
	        KNearest knn = KNearest.create();
	        knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabs));
	        // now test predictions:
	        for (int i=0; i<testData.rows(); i++)
	        {
	            Mat one_feature = testData.row(i);
	            int testLabel = testLabs.get(i);

	            Mat res = new Mat();
	            float p = knn.findNearest(one_feature, 1, res);
	            System.out.println(testLabel + " " + p + " " + res.dump());
	        }
	}

	@Override
	protected Mat apply(Mat matIn) {
		
		return null;
	}

}