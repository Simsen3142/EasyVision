package functions.matedit;

import java.awt.Image;
import java.util.*;
import org.opencv.core.Mat;

import database.ImageHandler;
import functions.RepresentationIcon;
import main.MatReceiver;
import main.MatSender;
import parameters.*;

public abstract class MatEditFunction extends MatSender implements MatReceiver, RepresentationIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684474058713956295L;
	private transient Map<String, Mat> mats=new HashMap<>();
	private boolean send=true;
	private transient Mat mat;
	
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
		mat=apply(matIn);
		if(send) {
			sendMatMap(getMats());
			sendMat(mat);
			sendParameters();
		}
		
		return mat;
	}
	
	@Override
	public void stop() {
		mat=null;
	}
	
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		performFunction(matIn);
	}
	
	@Override
	public Image getRepresentationImage() {
		return null;
	}
}
