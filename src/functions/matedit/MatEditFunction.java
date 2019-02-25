package functions.matedit;

import java.util.*;
import org.opencv.core.Mat;

import main.MatReceiver;
import main.MatSender;
import parameters.*;

public abstract class MatEditFunction extends MatSender implements MatReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684474058713956295L;
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
			sendParameters();
		}
		
		return ret;
	}
	
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		performFunction(matIn);
	}
}
