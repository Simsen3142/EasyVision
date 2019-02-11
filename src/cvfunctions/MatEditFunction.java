package cvfunctions;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import org.opencv.core.Mat;

import main.MatReceiver;
import main.MatSender;
import parameters.*;
import parameters.group.ParameterGroup;
import view.ParameterChangeDialog;

public abstract class MatEditFunction extends MatSender implements MatReceiver, Serializable {
	protected transient Mat paintTo;
	
	private transient Map<String, Mat> mats=new HashMap<>();
	
	/**
	 * @return the paintTo
	 */
	public Mat getPaintTo() {
		return paintTo;
	}
	
	/**
	 * @param paintTo the paintTo to set
	 */
	public void setPaintTo(Mat paintTo) {
		this.paintTo = paintTo;
	}
	
	public Map<String, Mat> getMats(){
		if(mats==null)
			mats=new HashMap<>();
		return mats;
	}
	
	public MatEditFunction() {}
	
	public MatEditFunction(Mat paintTo) {
		this.paintTo=paintTo;
	}
	
	public MatEditFunction(ParameterObject...parameters) {
		super(parameters);
	}
	
	protected abstract Mat apply(Mat matIn);
	
	public Mat performFunction(Mat matIn) {
		Mat ret=apply(matIn);
		sendMatMap(getMats());
		sendMat(ret);
		registerFrameForFPSCalculation();
		
		return ret;
	}
	
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		performFunction(matIn);
	}
}
