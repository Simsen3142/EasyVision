package arduino;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.FileHandler;

import javax.bluetooth.BluetoothStateException;

import arduino.csv.CsvConverter;
import arduino.serial.TwoWaySerialComm;
import arduino.view.SerialMonitor;
import connections.ConnectionHandler;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class ArduinoHandler extends ConnectionHandler<CommPortIdentifier>{
	private static ArduinoHandler instance;
	private TwoWaySerialComm serialComm;
	private SerialMonitor serialMonitor;
	private List<CommPortIdentifier> portIds;
	
	public static synchronized ArduinoHandler getInstance() {
		if(instance==null)
			instance=new ArduinoHandler();
		
		return instance;
	}
	
	/**
	 * @return the serialComm
	 */
	public TwoWaySerialComm getSerialComm() {
		return serialComm;
	}

	/**
	 * @return the serialMonitor
	 */
	public SerialMonitor getSerialMonitor() {
		return serialMonitor;
	}
	
	/**
	 * @return the ports
	 */
	public List<CommPortIdentifier> getPorts() {
		return serialComm.getAvailablePorts();
	}

	private ArduinoHandler() {
		serialComm=new TwoWaySerialComm();
		serialMonitor=new SerialMonitor();
		portIds=new ArrayList<>();
	}
	
	public Function<String, Void> getOnReceive(){
		return (line) -> 
		{
			serialMonitor.append(line+"\n");
			
			return null;
		};
	}

	@Override
	public boolean connectTo(CommPortIdentifier o) {
		try {
			serialComm.connect(o);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		serialComm.addOnReceive(getOnReceive());
		serialComm.startReader();
		return true;
	}

	@Override
	public void sendMessage(String text) {
		getSerialComm().getWriter().doWrite(text);
	}

	@Override
	public List<CommPortIdentifier> getConnectables() {
		if(portIds.size()==0) {
			portIds=getSerialComm().getAvailablePorts();
		}
		List<CommPortIdentifier> availablePorts=portIds;
		
		availablePorts.forEach((id)->connectableFound(id));
		
		return availablePorts;
	}

	@Override
	public void disconnect() {
		getSerialComm().disconnect();
	}

	@Override
	public boolean isConnected() {
		return getSerialComm().isConnected();
	}

	@Override
	public CommPortIdentifier getConnected() {
		return serialComm.getConnected();
	}

	@Override
	public String getConnectableName(CommPortIdentifier c) {
		return c.getName();
	}
	
	@Override
	public void searchConnectables() {
		super.searchConnectables();
		getConnectables();
		connectableSearchDone();
	}
}
