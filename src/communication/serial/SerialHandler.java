package communication.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import com.fazecast.jSerialComm.SerialPort;

import communication.MultiConnHandler;
import communication.serial.view.SerialLogger;

public class SerialHandler extends MultiConnHandler<SerialPort>{
	private static SerialHandler instance;
	private List<TwoWaySerialComm> serialComms;
	private Map<SerialPort, TwoWaySerialComm> portsNdComms;
	private SerialLogger serialLogger;
	private List<SerialPort> ports;
	
	public static synchronized SerialHandler getInstance() {
		if(instance==null)
			instance=new SerialHandler();
		
		return instance;
	}
	
	/**
	 * @return the serialComm
	 */
	public List<TwoWaySerialComm> getSerialComms() {
		return serialComms;
	}

	/**
	 * @return the serialLogger
	 */
	public SerialLogger getSerialLogger() {
		return serialLogger;
	}
	
	/**
	 * @return the ports
	 */
	public List<SerialPort> getPorts() {
		return TwoWaySerialComm.getAvailablePorts();
	}

	private SerialHandler() {
		serialComms=new ArrayList<TwoWaySerialComm>();
		serialLogger=new SerialLogger();
		ports=new ArrayList<>();
		portsNdComms=new TreeMap<SerialPort, TwoWaySerialComm>((p1,p2)->p1.getDescriptivePortName().compareTo(p2.getDescriptivePortName()));
	}
	
	public Function<String, Void> getOnReceive(SerialPort port){
		return (line) -> 
		{
			serialLogger.addMessage(line, port, System.currentTimeMillis());
			return null;
		};
	}

	@Override
	protected boolean connectToOverride(SerialPort o, int id) {
		try {
			TwoWaySerialComm serialComm=id==0?new TwoWaySerialComm():new TwoWaySerialCommLidar();
			System.out.println(serialComm);
			if(serialComm.connect(o)) {
				serialComms.add(serialComm);
				portsNdComms.put(o, serialComm);
				serialComm.addOnReceive(getOnReceive(serialComm.getPort()));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	

	@Override
	public List<SerialPort> getConnected() {
		ArrayList<SerialPort> connected=new ArrayList<SerialPort>();
		for(TwoWaySerialComm com:serialComms) {
			connected.add(com.getPort());
		}
		return connected;
	}

	@Override
	public List<SerialPort> getConnectables() {
		if(ports.size()==0) {
			ports=getPorts();
		}
		List<SerialPort> ports=this.ports;
		List<SerialPort> availablePorts=new ArrayList<SerialPort>();
		ports.forEach((port) -> {
			if (!port.isOpen()) {
				availablePorts.add(port);
				connectableFound(port);
			}
		});
		return availablePorts;
	}

	@Override
	public String getConnectableName(SerialPort p) {
		return p.getDescriptivePortName();
	}
	
	@Override
	public String getConnectableSystemName(SerialPort p) {
		return p.getSystemPortName();
	}
	
	public SerialPort getPortByName(String s) {
		for(SerialPort port:TwoWaySerialComm.getAvailablePorts()) {
			if(getConnectableName(port).equalsIgnoreCase(s) || getConnectableSystemName(port).equalsIgnoreCase(s)) {
				return port;
			}
		}
		return null;
	}
	
	@Override
	public void searchConnectables() {
		super.searchConnectables();
		getConnectables();
		connectableSearchDone();
	}
	
	public TwoWaySerialComm getCommunication(SerialPort o) {
		return portsNdComms.get(o);
	}

	@Override
	public boolean isConnectedTo(SerialPort o) {
		return portsNdComms.get(o)!=null;
	}

	@Override
	protected boolean disconnectFromOverride(SerialPort o) {
		TwoWaySerialComm com=portsNdComms.get(o);
		boolean ret=true;
		if(com!=null) {
			ret=com.disconnect();
			serialComms.remove(com);
			portsNdComms.put(o,null);
		}
		return ret;
	}

	@Override
	public void sendMessage(SerialPort o, String text) {
		TwoWaySerialComm com=portsNdComms.get(o);
		if(com!=null) {
			com.sendText(text);
		}
	}
	
	public void sendMessage(String portName, String text) {
		SerialPort port=getPortByName(portName);
		if(port!=null) {
			sendMessage(port, text);
		}
	}
	
	public void sendMessageToAll(String text) {
		for(TwoWaySerialComm com:serialComms) {
			sendMessage(com.getPort(),text);
		}
	}

	@Override
	public boolean isConnected() {
		return serialComms.size()>0;
	}
}
