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

	private transient Map<String, Mat> mats=new HashMap<>();
	private boolean send=true;
	
	/**
	 * @return the send
	 */
	public boolean isSend() {
		return send;
	}

	/**
	 * @param send the send to set
	 */
	public void setSend(boolean send) {
		this.send = send;
	}

	public Map<String, Mat> getMats(){
		if(mats==null)
			mats=new HashMap<>();
		return mats;
	}
	
	public MatEditFunction() {}
	
	
	public MatEditFunction(ParameterObject...parameters) {
		super(parameters);
	}
	
	protected abstract Mat apply(Mat matIn);
	
	public Mat performFunction(Mat matIn) {
		Mat ret=apply(matIn);
		if(send) {
			sendMatMap(getMats());
			sendMat(ret);
		}
		
		return ret;
	}
	
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		performFunction(matIn);
	}
}
