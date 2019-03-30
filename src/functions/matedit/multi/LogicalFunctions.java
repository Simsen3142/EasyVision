package functions.matedit.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import functions.matedit.ChangeResolution;

public abstract class LogicalFunctions extends MultiMatEditFunction{

	@Override
	public int getNrFunctionInputs() {
		return 0;
	}

	@Override
	public int getNrMatInputs() {
		return 10;
	}
	
	@Override
	public int getNrToSend() {
		return getSenderIndex().size();
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		int rows = 0;
		int cols = 0;
		
		Mat matOut = null;

		
		List<Mat> mats=new ArrayList<>();
		for(Mat mat:matsIn.values()) {
			if(mat!=null) {
				
				if(matOut==null) {
					rows=mat.rows();
					cols=mat.cols();
					
					matOut=new Mat(rows, cols, CvType.CV_8U);
				}
				
				if(mat.rows()!=rows || mat.cols()!=cols) {
					mat=ChangeResolution.apply(mat, cols, rows);
				}
				mats.add(mat);
			}
		}
		
		for(int col=0;col<cols;col++) {
			for(int row=0;row<rows;row++) {
				boolean b=checkMats(mats,row,col);
				matOut.put(row, col, b?255:0);
			}
		}
		
		return matOut;
	}

	protected boolean checkMats(List<Mat> mats, int row, int col) {
		List<Boolean> bs=new ArrayList<>();
		
		for(Mat mat:mats) {
			double[] values=mat.get(row, col);
			bs.add(values[0]!=0);
		}
		
		return performLogic(bs);
	}
	
	protected abstract boolean performLogic(List<Boolean> bs);
}