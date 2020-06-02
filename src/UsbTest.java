import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotOpenException;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;

import com.fazecast.jSerialComm.SerialPort;

import communication.MultiConnHandler;

/**
 * Simply lists all available USB devices.
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class UsbTest extends MultiConnHandler<UsbDevice> {

	private List<UsbDevice> usbDevices;

	public static void main(String[] args) {
		System.out.println(SerialPort.getCommPorts()[0].getSystemPortName());
		System.out.println(SerialPort.getCommPort("COM4").getDescriptivePortName());
		
//		UsbTest tst = new UsbTest();
//		UsbTest.findAllDevices().forEach((usb) -> {
//			System.out.println(usb);
//			try {
//				tst.connectTo(usb);
//				System.out.println(usb.getProductString());
//				tst.disconnectFrom(usb);
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
	}

	public UsbTest() {
		usbDevices = new ArrayList<UsbDevice>();
	}

	private static List<UsbDevice> findAllDevices() {
		UsbServices services;
		try {
			services = UsbHostManager.getUsbServices();
			List<UsbDevice> usbDevices = new ArrayList<UsbDevice>();
			findDevices(services.getRootUsbHub(), usbDevices);
			return usbDevices;
		} catch (SecurityException | UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void findDevices(UsbHub hub, List<UsbDevice> devices) {
		for (UsbDevice usbDevice : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
			if (usbDevice.isUsbHub()) {
				findDevices((UsbHub) usbDevice, devices);
			} else {
				devices.add(usbDevice);
			}
		}
	}

//	private void syncSubmit(UsbDevice device) {
//		UsbControlIrp irp = device.createUsbControlIrp(
//				(byte) (UsbConst.REQUESTTYPE_DIRECTION_IN | UsbConst.REQUESTTYPE_TYPE_STANDARD
//						| UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
//				UsbConst.REQUEST_GET_CONFIGURATION, (short) 0, (short) 0);
//		irp.setData(new byte[1]);
//		try {
//			device.syncSubmit(irp);
//		} catch (IllegalArgumentException | UsbDisconnectedException | UsbException e) {
//			e.printStackTrace();
//		}
//	}

	private static UsbInterface getDeviceInterface(UsbDevice device) {
		UsbConfiguration configuration = device.getActiveUsbConfiguration();
		return configuration.getUsbInterface((byte) 1);
	}

	private static UsbPipe getUsbPipe(UsbDevice device, byte endpointNr) {
		UsbInterface usbInt = getDeviceInterface(device);
		UsbEndpoint endpoint = usbInt.getUsbEndpoint(endpointNr);
		return endpoint.getUsbPipe();
	}

	@Override
	protected boolean disconnectFromOverride(UsbDevice o) {
		UsbInterface usbInt = getDeviceInterface(o);
		try {
			usbInt.release();
			usbDevices.remove(o);
			return true;
		} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void sendMessage(UsbDevice o, String text) {
		UsbPipe pipe = getUsbPipe(o, (byte) 0x01);
		try {
			pipe.open();
			pipe.syncSubmit(text.getBytes());
			pipe.close();
		} catch (UsbNotActiveException | UsbNotOpenException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnectedTo(UsbDevice o) {
		return usbDevices.contains(o);
	}

	@Override
	public List<UsbDevice> getConnected() {
		return usbDevices;
	}

	@Override
	public List<UsbDevice> getConnectables() {
		return findAllDevices();
	}

	@Override
	protected boolean connectToOverride(UsbDevice o, int id) {
		UsbInterface usbInt = getDeviceInterface(o);
		try {
			usbInt.claim();
			usbDevices.add(o);
			return true;
		} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getConnectableName(UsbDevice o) {
		return o.toString();
	}

	@Override
	public boolean isConnected() {
		return usbDevices.size() > 0;
	}

//	public void doTheThing() {
//		UsbServices services;
//		try {
//			services = UsbHostManager.getUsbServices();
//			List<UsbDevice> usbDevices = new ArrayList<UsbDevice>();
//			findDevices(services.getRootUsbHub(), usbDevices);
//			for (UsbDevice device : usbDevices) {
//				try {
//					System.out.println(device);
//					final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
//					final byte iManufacturer = desc.iManufacturer();
//					final byte iProduct = desc.iProduct();
//					System.out.println(desc);
//					// Dump the device name
//					device.addUsbDeviceListener(new UsbDeviceListener() {
//
//						@Override
//						public void usbDeviceDetached(UsbDeviceEvent arg0) {
//							System.out.println(arg0.getUsbDevice() + " detached");
//						}
//
//						@Override
//						public void errorEventOccurred(UsbDeviceErrorEvent arg0) {
//							System.out.println(arg0.getUsbDevice());
//
//						}
//
//						@Override
//						public void dataEventOccurred(UsbDeviceDataEvent arg0) {
//							System.out.println(arg0.getUsbDevice());
//							arg0.getData();
//
//						}
//					});
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UsbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UsbDisconnectedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
}