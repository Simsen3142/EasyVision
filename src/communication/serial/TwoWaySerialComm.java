package communication.serial;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.fazecast.jSerialComm.SerialPort;

import communication.ReadingThread;
import communication.TwoWayConnection;
import communication.Writer;

public class TwoWaySerialComm extends TwoWayConnection<SerialPort> {
	private ReadingThread reader;
	private Writer writer;
	private int baudRate = 9600;

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
	@Override
	public void startReader() {
		if (reader!=null && reader.isAlive())
			return;
		
		reader=createReader();
		reader.start();
	}

	public ReadingThread createReader() {
		ReadingThread reader=null;
		try {
			reader = new ReadingThread(connectedPort.getInputStream());
			List<Function<String, Void>> onReceives = new ArrayList<Function<String, Void>>();
			onReceives.add((text) -> {
				this.triggerOnReceives(text);
				return null;
			});
			reader.setOnReceives(onReceives);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reader;
	}

	/**
	 * @param reader the reader to set
	 */
	public void stopReader() {
		if (reader == null || !reader.isAlive()) {
			return;
		}
		reader.interrupt();
	}

	public TwoWaySerialComm() {
		super();
	}

	public boolean connect(SerialPort port) throws Exception {
		System.out.println("CONNECT?");
		if (port.isOpen()) {
			System.out.println("Error: Port is currently in use");
			return false;
		} else {
			port.setBaudRate(baudRate);
			if(port.openPort(1000)) {
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				connectedPort=port;
				startReader();
				return true;
			}
			
			return false;
		}
	}
	
	private Writer getWriter() {
		if (writer == null) {
			if (connectedPort != null) {
				try {
					System.out.println("INIT WRITER");
					writer = new Writer(connectedPort.getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return writer;
	}

	public boolean disconnect() {
		boolean ret = true;
		stopReader();
		reader = null;
		writer = null;
		if (connectedPort != null) {
			ret = connectedPort.closePort();
		}
		connectedPort = null;

		return ret;
	}

	public static List<SerialPort> getAvailablePorts() {
		return Arrays.asList(SerialPort.getCommPorts());
	}

	@Override
	public boolean sendText(String text) {
		try {
			getWriter().doWrite(text);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public InputStream getInputStream() {
		if(isConnected())
			return connectedPort.getInputStream();
		return null;
	}
	@Override
	public OutputStream getOutputStream() {
		if(isConnected())
			return connectedPort.getOutputStream();
		return null;
	}
}