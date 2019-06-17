package arduino.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import javax.jws.soap.SOAPBinding;

public class TwoWaySerialComm {
	private SerialPort port;
	private CommPortIdentifier connected;
	private SerialReadingThread reader;
	private SerialWriter writer;
	private int baudRate=250000;
	private List<Function<String,Void>> onReceives;
	
	/**
	 * @return the connected
	 */
	public CommPortIdentifier getConnected() {
		return connected;
	}

	/**
	 * @return the baudRate
	 */
	public int getBaudRate() {
		return baudRate;
	}

	/**
	 * @param baudRate the baudRate to set
	 */
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	/**
	 * @return the reader
	 */
	public void startReader() {
		if(reader==null) {
			initReader();
		}
		
		if(reader.isAlive())
			return ;
		reader.start();
	}
	
	public void initReader() {
		try {
			reader=new SerialReadingThread(port.getInputStream());
			if(onReceives==null)
				onReceives=new ArrayList<>();
			reader.setOnReceives(onReceives);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param reader the reader to set
	 */
	public void stopReader() {
		if(reader==null || !reader.isAlive()) {
			return ;
		}
		reader.interrupt();
	}
	
	public SerialPort getPort() {
		return port;
	}
	
	public TwoWaySerialComm() {
		super();
	}
	
	public SerialPort connect(CommPortIdentifier portIdentifier) throws Exception {
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
			return null;
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				connected=portIdentifier;
				setPort(serialPort);
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				return serialPort;

			} else {
				System.out.println("Error: Only serial ports are handled by this.");
			}
		}
		return null;
	}
	
	public SerialWriter getWriter() {
		if(writer==null) {
			if(port!=null) {
				try {
					System.out.println("INIT WRITER");
					writer=new SerialWriter(port.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return writer;
	}
	
	private void setPort(SerialPort port) {
		this.port=port;
		initReader();
	}

	public SerialPort connect(String portName) throws Exception {
		return connect(CommPortIdentifier.getPortIdentifier(portName));
	}
	
	public void disconnect() {
		stopReader();
		reader=null;
		writer=null;
		if(port!=null) {
			port.close();
			port=null;
		}
		connected=null;
	}

	public boolean isConnected() {
		return port!=null;
	}
	
	/** 
	 *
	 */
	public void addOnReceive(Function<String,Void> onReceive) {
		if(onReceives==null)
			onReceives=new ArrayList<>();
		onReceives.add(onReceive);
	}
	
	public void removeOnReceive(Function<String,Void> onReceive) {
		onReceives.remove(onReceive);
	}

	public List<CommPortIdentifier> getAvailablePorts(){
		List<CommPortIdentifier> openPorts=new ArrayList<>();
		Enumeration<?> enumComm;
		CommPortIdentifier serialPortId;
		enumComm = CommPortIdentifier.getPortIdentifiers();
		
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier)enumComm.nextElement();
			if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				openPorts.add(serialPortId);
			}
		}
		
		return openPorts;
	}
}