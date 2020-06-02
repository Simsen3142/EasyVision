package communication.netsocket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import communication.MultiConnHandler;
import communication.serial.TwoWaySerialComm;
import communication.serial.TwoWaySerialCommLidar;
import communication.serial.view.SerialLogger;

public class NetworkHandler extends MultiConnHandler<InetSocketAddress> {
	private static volatile NetworkHandler instance;
	private List<SocketComm> serialComms;
	private Map<InetSocketAddress, SocketComm> portsNdComms;
	private SerialLogger netLogger;
	private List<InetSocketAddress> ports;

	public static NetworkHandler getInstance() {
		if (instance == null) {
			instance = new NetworkHandler();
		}
		return instance;
	}

	/**
	 * @return the serialComm
	 */
	public List<SocketComm> getSerialComms() {
		return serialComms;
	}

//	/**
//	 * @return the ports
//	 */
//	public List<InetSocketAddress> getPorts() {
//		return SocketComm.getAvailablePorts();
//	}

	private NetworkHandler() {
		serialComms = new ArrayList<SocketComm>();
		ports = new ArrayList<>();
		portsNdComms = new TreeMap<InetSocketAddress, SocketComm>(
				(p1, p2) -> p1.getAddress().toString().compareTo(p2.getAddress().toString()));
	}

	@Override
	protected boolean connectToOverride(InetSocketAddress o, int id) {
		try {
			SocketComm serialComm=id==0?new SocketComm():new SocketCommLidar();
			if (serialComm.connect(o)) {
				serialComms.add(serialComm);
				portsNdComms.put(o, serialComm);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<InetSocketAddress> getConnected() {
		ArrayList<InetSocketAddress> connected = new ArrayList<InetSocketAddress>();
		for (SocketComm com : serialComms) {
			connected.add(com.getPort());
		}
		return connected;
	}

	@Override
	public List<InetSocketAddress> getConnectables() {
		return ports;
	}

	@Override
	public String getConnectableName(InetSocketAddress p) {
		return p.toString();
	}

	@Override
	public String getConnectableSystemName(InetSocketAddress p) {
		return p.toString();
	}

	public InetSocketAddress getSocketByName(String s) {
		for (InetSocketAddress port : portsNdComms.keySet()) {
			if (getConnectableName(port).equalsIgnoreCase(s) || getConnectableSystemName(port).equalsIgnoreCase(s)) {
				return port;
			}
		}
		return null;
	}

	@Override
	public void searchConnectables() {
		super.searchConnectables();
		getConnectables();
		super.connectableSearchDone();
	}

	public SocketComm getCommunication(InetSocketAddress o) {
		return portsNdComms.get(o);
	}

	@Override
	public boolean isConnectedTo(InetSocketAddress o) {
		return portsNdComms.get(o) != null;
	}

	@Override
	protected boolean disconnectFromOverride(InetSocketAddress o) {
		SocketComm com = portsNdComms.get(o);
		boolean ret = true;
		if (com != null) {
			ret = com.disconnect();
			serialComms.remove(com);
			portsNdComms.put(o, null);
		}
		return ret;
	}

	@Override
	public void sendMessage(InetSocketAddress o, String text) {
		SocketComm com = portsNdComms.get(o);
		if (com != null) {
			com.sendText(text);
		}
	}

	public void sendMessage(String portName, String text) {
		InetSocketAddress port = getSocketByName(portName);
		if (port != null) {
			sendMessage(port, text);
		}
	}

	public void sendMessageToAll(String text) {
		for (SocketComm com : serialComms) {
			sendMessage(com.getPort(), text);
		}
	}

	@Override
	public boolean isConnected() {
		return serialComms.size() > 0;
	}
}
