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
	private SerialReadingThread reader;
	private SerialWriter writer;
	private int baudRate=9600;
	private List<Function<String,Void>> onReceives;
	
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
			try {
				reader=new SerialReadingThread(port.getInputStream());
				reader.setOnReceives(onReceives);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(reader.isAlive())
			return ;
		reader.start();
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
		try {
			reader=new SerialReadingThread(port.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		reader.removeOnReceive(onReceive);
	}

	public List<CommPortIdentifier> getAvailablePorts(){
		List<CommPortIdentifier> openPorts=new ArrayList<>();
		Enumeration<?> enumComm;
		CommPortIdentifier serialPortId;
		enumComm = CommPortIdentifier.getPortIdentifiers();
		
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier)enumComm.nextElement();
			if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(serialPortId);
				openPorts.add(serialPortId);
			}
		}
		
		return openPorts;
	}
}