package functions.matedit;

import java.awt.Image;

import org.opencv.core.Mat;

import database.ImageHandler;
import parameters.group.ConnectionParameterGroup;

public class Function2 extends MatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6097760758289850019L;
	private static volatile Image img;

	public Function2(Boolean empty) {}
	
	public Function2() {super(new ConnectionParameterGroup("group"));}

	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=matIn.clone();
		for(int i=0;i<matIn.rows();i++) {
			for(int c=0;c<matIn.cols()/2;c++) {
				matOut.put(i, c, new double[] {255,255,255} );
			}
		}
		return matOut;
	}
}
