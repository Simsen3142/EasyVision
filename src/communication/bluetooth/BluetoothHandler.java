package communication.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import com.google.common.base.Function;

import communication.ConnectionHandler;

public class BluetoothHandler extends ConnectionHandler<RemoteDevice> implements DiscoveryListener {

	private static Object inquiryLock = new Object();
	private static Object serviceLock = new Object();
	private boolean inquiring=false;
	private ArrayList<RemoteDevice> devices;
	private static BluetoothHandler instance;
	private DiscoveryAgent agent;
	private Function<ArrayList<RemoteDevice>, Void> onInquiryCompleted;
	private StreamConnection connection;
	private RemoteDevice connectedDevice;
	private PrintWriter out;

	public static BluetoothHandler getInstance() {
		if (instance == null)
			instance = new BluetoothHandler();

		return instance;
	}

	private BluetoothHandler() {
		devices = new ArrayList<RemoteDevice>();
	}

	public ArrayList<RemoteDevice> getDevices() {
		return devices;
	}

	public DiscoveryAgent getAgent() throws BluetoothStateException {
		if (agent == null) {
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			agent = localDevice.getDiscoveryAgent();
		}

		return agent;
	}

	public static void main(String[] args) {

		BluetoothHandler listener = getInstance();

		try {
			listener.searchConnectables();

			try {
				synchronized (inquiryLock) {
					inquiryLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}

			for (RemoteDevice device : listener.devices) {
				if(device.getFriendlyName(false).contains("JDY")) {
					listener.connectTo(device);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.sendMessage("Hello World 124dsflkhjsfsfffffffffffffffff5");
		listener.disconnect();
	}
	
	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
		if(!devices.contains(btDevice)) {
			devices.add(btDevice);
			connectableFound(btDevice);
		}
	}

	@Override
	public void inquiryCompleted(int arg0) {
		inquiring=false;
		synchronized (inquiryLock) {
			inquiryLock.notify();
		}
		if(onInquiryCompleted!=null) {
			onInquiryCompleted.apply(devices);
		}
		
		connectableSearchDone();
	}

	@Override
	public void serviceSearchCompleted(int arg0, int arg1) {
		synchronized (serviceLock) {
			serviceLock.notify();
		}
	}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (int i = 0; i < servRecord.length; i++) {
			String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (url == null) {
				continue;
			}
			DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
			if (serviceName != null) {
				if (url!=null) {
					try {
						this.connection = (StreamConnection) Connector.open(url);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("service found " + url);
			}
		}
	}
	
	
	
	
	//CONNECTION HANDLER ###########################################

	@Override
	public List<RemoteDevice> getConnectables() {
		if(devices.size()==0) {
			searchConnectables();
		}
		return devices;
	}

	@Override
	public RemoteDevice getConnected() {
		if(connection==null) {
			disconnect();
		}
		return connectedDevice;
	}

	@Override
	public boolean isConnected() {
		return connection!=null;
	}

	@Override
	protected boolean connectToOverride(RemoteDevice device, int id) {
		if(connectedDevice!=null) {
			disconnect();
		}
		
		UUID[] uuidSet = new UUID[1];
		uuidSet[0] = new UUID(0x1101); //scan for btspp://... services (as HC-05 offers it)

		int[] attrIDs = new int[] { 0x0100 // Service name
		};
		
		try {
			this.getAgent().searchServices(attrIDs, uuidSet, device, this);
		} catch (BluetoothStateException e1) {
			e1.printStackTrace();
			return false;
		}
		
		this.connectedDevice=device;
		try {
			synchronized (serviceLock) {
				serviceLock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		
		return isConnected();
	}

	@Override
	public boolean disconnectOverride() {
		try {
			connectedDevice=null;
			if(connection!=null) {
				connection.close();
				connection=null;
			}
			if(out!=null) {
				out.close();
				out=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return true;
	}

	@Override
	public void sendMessage(String text) {
		if(out==null) {
			try {
				// Send some text to server
				OutputStream os = connection.openOutputStream();
				out=new PrintWriter(os);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		out.println(text);
		out.flush();
		try {
			System.out.println("PRINTING: "+text+" to "+connectedDevice.getFriendlyName(false));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public String getConnectableName(RemoteDevice c) {
		try {
			return c.getFriendlyName(false);
		} catch (IOException e) {
			return c.getBluetoothAddress();
		}
	}

	@Override
	public void searchConnectables() {
		if(!inquiring) {
			try {
				getAgent().startInquiry(DiscoveryAgent.GIAC, this);
				inquiring=true;
				super.searchConnectables();
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}
	}
}