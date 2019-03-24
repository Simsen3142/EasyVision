package functions.matedit.multi;

import java.awt.Image;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.opencv.core.Mat;

import functions.RepresentationIcon;
import functions.matedit.MatEditFunction;
import main.MatReceiver;
import main.MatSender;
import parameters.ParameterObject;

public abstract class MultiMatEditFunction extends MatSender implements MatReceiver, RepresentationIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6443555271381178386L;
	private transient Map<String, Mat> mats=new HashMap<>();
	private Map<MatSender, Integer> senderIndex=new HashMap<>();
	private transient Map<Integer, Mat> matsIn;
	private boolean send=true;
	private transient Mat mat;
	private int id=System.identityHashCode(this);
	
	public void recalculateId() {
		this.id*=Math.random();
	}
	
	/**
	 * @return the senderIndex
	 */
	public Map<MatSender, Integer> getSenderIndex() {
		if(senderIndex==null)
			senderIndex=new HashMap<>();
		return senderIndex;
	}
	
	public MatSender getMatSenderByIndex(int index) {
		Set<MatSender> senderSet=getSenderIndex().keySet();
		for(MatSender sender:senderSet) {
			if(senderIndex.get(sender).equals(index)) {
				return sender;
			}
		}
		
		return null;
	}
	
	public void clearMatSenderIndex() {
		senderIndex.clear();
	}

	/**
	 * @return the nrInputs
	 */
	public abstract int getNrFunctionInputs();

	public abstract int getNrMatInputs();
	
	public int getNrToWork() {
		return getNrMatInputs();
	}
	
	public void addMatSender(MatSender sender, int index) {
		getSenderIndex().put(sender, index);
	}
	
	public void removeMatSender(MatSender sender) {
		getSenderIndex().remove(sender);
	}
	
	public int getIndexOfMatsender(MatSender sender) {
		Integer i=getSenderIndex().get(sender);
		if(i!=null)
			return i;
		return -1;
	}
	
	/**
	 * @return the send
	 */
	public boolean isSend() {
		return send;
	}
	
	private Map<Integer, Mat> getMatsIn(){
		if(matsIn==null)
			matsIn=new LinkedHashMap<>();
		return matsIn;
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
	
	public MultiMatEditFunction() {}
	
	public MultiMatEditFunction(ParameterObject...parameters) {
		super(parameters);
	}
	
	protected abstract Mat apply(Map<Integer, Mat> matsIn);
	
	public Mat performFunction(Map<Integer, Mat> matsIn) {
		mat=apply(matsIn);
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
		getMatsIn().clear();
	}
	
	@Override
	public void onReceive(Mat matIn, MatSender sender) {
		int i=getIndexOfMatsender(sender);
		getMatsIn().put(i, matIn.clone());
		
		if(matsIn.size()>=getNrToWork()) {
			performFunction(matsIn);
			clearMatsIn(matsIn);
		}
	}
	
	public void clearMatsIn(Map<Integer, Mat> matsIn) {
		matsIn.clear();
	}
	
	@Override
	public Image getRepresentationImage() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MultiMatEditFunction))
			return false;
		MultiMatEditFunction other = (MultiMatEditFunction) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
