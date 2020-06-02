package communication.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.fazecast.jSerialComm.SerialPort;

import communication.ConnectionHandler;
import communication.serial.view.SerialMonitor;

public class SerialHandlerSingle extends ConnectionHandler<SerialPort>{
	private static SerialHandlerSingle instance;
	private TwoWaySerialComm serialComm;
	private SerialMonitor serialMonitor;
	private List<SerialPort> ports;
	
	public static synchronized SerialHandlerSingle getInstance() {
		if(instance==null)
			instance=new SerialHandlerSingle();
		
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
	public List<SerialPort> getPorts() {
		return TwoWaySerialComm.getAvailablePorts();
	}

	private SerialHandlerSingle() {
		serialComm=new TwoWaySerialComm();
		serialMonitor=new SerialMonitor();
		ports=new ArrayList<>();
	}
	
	public Function<String, Void> getOnReceive(){
		return (line) -> 
		{
			serialMonitor.append(line+"\n");
			
			return null;
		};
	}

	@Override
	protected boolean connectToOverride(SerialPort o, int id) {
		try {
			serialComm.connect(o);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		serialComm.addOnReceive(getOnReceive());
		return true;
	}

	@Override
	public void sendMessage(String text) {
		getSerialComm().sendText(text);
	}

	@Override
	public List<SerialPort> getConnectables() {
		if(ports.size()==0) {
			ports=TwoWaySerialComm.getAvailablePorts();
		}
		List<SerialPort> availablePorts=ports;
		
		availablePorts.forEach((id)->connectableFound(id));
		
		return availablePorts;
	}

	@Override
	public boolean disconnectOverride() {
		return getSerialComm().disconnect();
	}
	
	@Override
	public boolean isConnected() {
		return getSerialComm().isConnected();
	}

	@Override
	public SerialPort getConnected() {
		return serialComm.getPort();
	}

	@Override
	public String getConnectableName(SerialPort c) {
		return c.getDescriptivePortName();
	}
	
	@Override
	public void searchConnectables() {
		super.searchConnectables();
		getConnectables();
		connectableSearchDone();
	}
}
