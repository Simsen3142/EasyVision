package parameters.group;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

import communication.MultiConnHandler;
import communication.TwoWayConnection;
import communication.bluetooth.BluetoothHandler;
import communication.netsocket.NetworkHandler;
import communication.serial.SerialHandler;
import parameters.BooleanParameter;
import parameters.EnumParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.SerialPortParameter;
import parameters.StringParameter;

public class ConnectionParameterGroup extends ParameterGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5148887694593503835L;
	private Connectiontype lastVal;
	private ParameterGroup conSettings;

	public ConnectionParameterGroup(String name) {
		super(name, (ParameterObject[]) null);
		EnumParameter choice = new EnumParameter("connectiontype", Connectiontype.SERIAL) {
			private static final long serialVersionUID = 6668978381381502486L;

			@Override
			public void onChange(Enum<?> val) {
				if(val!=lastVal) {
					conSettings.removeParameters(getConnectionTypeParameters(lastVal));
					conSettings.addParameters(getConnectionTypeParameters((Connectiontype) val));
					lastVal = (Connectiontype) val;
				}
			}
		};
		lastVal = (Connectiontype) choice.getValue();
		BooleanParameter connected = new BooleanParameter("connected", false, false);
		BooleanParameter autoconnect = new BooleanParameter("autoconnect", false);
		conSettings = new ParameterGroup("consettings");
		addParameters(choice, autoconnect,connected, conSettings);
		conSettings.addParameters(getConnectionTypeParameters((Connectiontype) choice.getValue()));
	}
	
	public Connectiontype getConnectionType() {
		return lastVal;
	}
	
	public boolean isAutoConnect() {
		BooleanParameter param=(BooleanParameter) getParameterByName("autoconnect");
		return param.getValue();
	}

	public MultiConnHandler<?> getConnectionHandler() {
		switch (lastVal) {
			case NETWORK:
				return NetworkHandler.getInstance();
			case SERIAL:
				return SerialHandler.getInstance();
			default:
				break;
		}

		return null;
	}
	
	public boolean connect() {
		switch (lastVal) {
			case NETWORK: {
				StringParameter ip = (StringParameter) conSettings.getParameterByName("ipaddress");
				IntegerParameter port = (IntegerParameter) conSettings.getParameterByName("portnr");
				InetSocketAddress addr = new InetSocketAddress(ip.getValue(), port.getValue());
				return NetworkHandler.getInstance().connectTo(addr);
			}
			case SERIAL:{
				SerialPortParameter port = (SerialPortParameter) conSettings.getParameterByName("port");
				return SerialHandler.getInstance().connectTo(port.getSelectedSerialPort());
			}
			default:
				return false;
		}
	}
	
	public boolean disconnect() {
		TwoWayConnection<?> con=getConnection();
		if(con!=null)
			return con.disconnect();
		return false;
	}

	public TwoWayConnection<?> getConnection() {
		switch (lastVal) {
			case NETWORK: {
				StringParameter ip = (StringParameter) conSettings.getParameterByName("ipaddress");
				IntegerParameter port = (IntegerParameter) conSettings.getParameterByName("portnr");
				InetSocketAddress addr = new InetSocketAddress(ip.getValue(), port.getValue());
				return NetworkHandler.getInstance().getCommunication(addr);
			}
			case SERIAL:{
				SerialPortParameter port = (SerialPortParameter) conSettings.getParameterByName("port");
				return port.getSelectedCommunication();
			}
			default:
				return null;
		}
	}
	
	public Object getConnectable() {
		switch(lastVal) {
		case BLUETOOTH:
			break;
		case NETWORK:
			return getInetSocketAddress();
		case SERIAL:
			return getSelectedSerialPort();
		}
		return null;
	}
	
	public SerialPort getSelectedSerialPort() {
		SerialPortParameter port = (SerialPortParameter) conSettings.getParameterByName("port");
		return port!=null?port.getSelectedSerialPort():null;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		StringParameter ip = (StringParameter) conSettings.getParameterByName("ipaddress");
		IntegerParameter port = (IntegerParameter) conSettings.getParameterByName("portnr");
		InetSocketAddress addr = new InetSocketAddress(ip.getValue(), port.getValue());
		return addr!=null?addr:null;
	}
	
	public void sendText(String text) {
		TwoWayConnection<?> con=getConnection();
		if(con!=null)
			con.sendText(text);
		else if(isAutoConnect()) {
			connect();
			con=getConnection();
			if(con!=null)
				con.sendText(text);
		}
	}

	private List<ParameterObject> getConnectionTypeParameters(Connectiontype contype) {
		List<ParameterObject> ret = new ArrayList<ParameterObject>();
		switch (contype) {
		case BLUETOOTH:
			break;
		case NETWORK:
			ret.add(new StringParameter("ipaddress", "127.0.0.1"));
			ret.add(new IntegerParameter("portnr", 8081, 0, 65535));
			break;
		case SERIAL:
			ret.add(new SerialPortParameter("port"));
			break;
		}

		for (ParameterObject param : ret) {
			param.setParamGroup(conSettings);
		}

		return ret;
	}

	public enum Connectiontype {
		SERIAL, NETWORK, BLUETOOTH
	}

}
