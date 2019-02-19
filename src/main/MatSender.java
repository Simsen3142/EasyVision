package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;

import cvfunctions.MatEditFunction;
import parameters.ParameterObject;
import parameters.ParameterizedObject;

public class MatSender extends ParameterizedObject {
	private List<MatReceiver> receivers=new ArrayList<>();
	private List<MatMapReceiver> receivers_map=new ArrayList<>();
	
	/**
	 * @return the receivers
	 */
	public List<MatReceiver> getReceivers() {
		return receivers;
	}

	/**
	 * @param receivers the receivers to set
	 */
	public void setReceivers(List<MatReceiver> receivers) {
		this.receivers = receivers;
	}

	/**
	 * @return the receivers_map
	 */
	public List<MatMapReceiver> getReceivers_map() {
		return receivers_map;
	}

	/**
	 * @param receivers_map the receivers_map to set
	 */
	public void setReceivers_map(List<MatMapReceiver> receivers_map) {
		this.receivers_map = receivers_map;
	}

	public void addMatReceiver(MatReceiver rcvr) {
		if(!receivers.contains(rcvr))
			receivers.add(rcvr);
	}
	
	public MatSender(ParameterObject...parameters) {
		super(parameters);
	}
	
	public void removeMatReceiver(MatReceiver rcvr) {
		receivers.remove(rcvr);
	}
	
	public void clearMatReceivers() {
		receivers.clear();
	}
	
	public void clearMatReceiverFunctions() {
		List<MatReceiver> toRemove=new ArrayList<>();
		
		for(MatReceiver receiver:receivers) {
			if(receiver instanceof MatEditFunction) {
				toRemove.add(receiver);
			}
		}
		
		receivers.removeAll(toRemove);
	}
	
	public void addMatMapReceiver(MatMapReceiver rcvr) {
		if(!receivers_map.contains(rcvr))
			receivers_map.add(rcvr);
	}
	
	protected void sendMat(Mat mat) {
		List<MatReceiver> list=new ArrayList<MatReceiver>(receivers);
		for(MatReceiver receiver:list) {
			receiver.onReceive(mat, this);
		}
	}
	
	protected void sendMatMap(Map<String,Mat> matMap) {
		List<MatMapReceiver> list=new ArrayList<MatMapReceiver>(receivers_map);
		for(MatMapReceiver receiver:list) {
			receiver.onReceive(matMap, this);
		}
	}
	
	public void stop() {
		
	}
}
