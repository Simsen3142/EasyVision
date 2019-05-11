package bluetooth;

import java.io.OutputStream;
import java.util.ArrayList;

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
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import com.google.common.base.Function;

public class BluetoothHandler implements DiscoveryListener {

	private static Object lock = new Object();
	private ArrayList<RemoteDevice> devices;
	private static BluetoothHandler instance;
	private DiscoveryAgent agent;
	private Function<ArrayList<RemoteDevice>, Void> onInquiryCompleted;

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

	public void searchDevices() throws BluetoothStateException {
		getAgent().startInquiry(DiscoveryAgent.GIAC, this);
	}

	public static void main(String[] args) {

		BluetoothHandler listener = getInstance();

		try {
			listener.searchDevices();

			try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}

			System.out.println("Device Inquiry Completed. ");

			UUID[] uuidSet = new UUID[1];
			uuidSet[0] = new UUID(0x1105); // OBEX Object Push service

			int[] attrIDs = new int[] { 0x0100 // Service name
			};

			for (RemoteDevice device : listener.devices) {
				System.out.println("\t" + device.getFriendlyName(false));
				listener.getAgent().searchServices(attrIDs, uuidSet, device, listener);

				try {
					synchronized (lock) {
						lock.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}

				System.out.println("Service search finished.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
		devices.add(btDevice);
	}

	@Override
	public void inquiryCompleted(int arg0) {
		synchronized (lock) {
			lock.notify();
		}
		if(onInquiryCompleted!=null) {
			onInquiryCompleted.apply(devices);
		}
	}

	@Override
	public void serviceSearchCompleted(int arg0, int arg1) {
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (int i = 0; i < servRecord.length; i++) {
			String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			System.out.println("\t" + url);
			if (url == null) {
				continue;
			}
			DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
			if (serviceName != null) {
				System.out.println("service " + serviceName.getValue() + " found " + url);
				System.out.println("\t"+serviceName.getValue());
				if (serviceName.getValue().toString().toLowerCase().contains("obex object push")) {
					sendMessageToDevice("Hello World !!!",url);
				}
			} else {
				System.out.println("service found " + url);
			}
		}
	}

	private static void sendMessageToDevice(String text, String serverURL) {
		try {
			System.out.println("Connecting to " + serverURL);

			ClientSession clientSession = (ClientSession) Connector.open(serverURL);
			HeaderSet hsConnectReply = clientSession.connect(null);
			if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
				System.out.println("Failed to connect");
				return;
			}

			HeaderSet hsOperation = clientSession.createHeaderSet();
			hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
			hsOperation.setHeader(HeaderSet.TYPE, "text");

			// Create PUT Operation
			Operation putOperation = clientSession.put(hsOperation);

			// Send some text to server
			byte data[] = text.getBytes("iso-8859-1");
			OutputStream os = putOperation.openOutputStream();
			os.write(data);
			os.close();

			putOperation.close();

//			clientSession.disconnect(null);

//			clientSession.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ClientConnection implements DiscoveryListener {
		private RemoteDevice device;
		private ClientSession clientSession;
		
		public ClientConnection(RemoteDevice device) {
			this.device=device;
		}
		
		public void searchService() throws BluetoothStateException {
			UUID[] uuidSet = new UUID[1];
			uuidSet[0] = new UUID(0x1105); // OBEX Object Push service

			int[] attrIDs = new int[] { 0x0100 // Service name
			};

			getAgent().searchServices(attrIDs, uuidSet, device, getInstance());
		}

		@Override
		public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
		}

		@Override
		public void inquiryCompleted(int arg0) {
		}

		@Override
		public void serviceSearchCompleted(int arg0, int arg1) {
			
		}

		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			for (int i = 0; i < servRecord.length; i++) {
				String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				System.out.println("\t" + url);
				if (url == null) {
					continue;
				}
				DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
				if (serviceName != null) {
					System.out.println("service " + serviceName.getValue() + " found " + url);

					if (serviceName.getValue().toString().toLowerCase().contains("obex object push")) {
						sendMessageToDevice("Hello World !!!",url);
					}
				} else {
					System.out.println("service found " + url);
				}
			}
		}
		
	}

}