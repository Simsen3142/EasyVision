package cvfunctions;

import org.opencv.core.Mat;

public class Function2 extends MatEditFunction {

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		for(int i=0;i<matIn.rows();i++) {
			for(int c=0;c<matIn.cols();c++) {
				matOut.put(i, c, new double[] {255,255,255} );
			}
		}
		return matOut;
	}

}
