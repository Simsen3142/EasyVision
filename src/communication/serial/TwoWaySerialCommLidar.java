package communication.serial;

import java.util.ArrayList;
import java.util.List;
import com.fazecast.jSerialComm.SerialPort;

import communication.LidarReadingThread;
import communication.ReadingThread;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessageSender;

public class TwoWaySerialCommLidar extends TwoWaySerialComm implements LidarMessageSender{
	private int baudRate = 115200;
	private List<LidarListener> listeners = new ArrayList<LidarListener>();

	@Override
	public boolean addLidarListener(LidarListener lstnr) {
		return listeners.add(lstnr);
	}

	@Override
	public boolean removeLidarListener(Object lstnr) {
		boolean ret = listeners.remove(lstnr);
		return ret;
	}

	@Override
	public ReadingThread createReader() {
		LidarReadingThread reader=null;
		try {
			reader = new LidarReadingThread(getInputStream());
			reader.setListeners(listeners);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return reader;
	}

	public TwoWaySerialCommLidar() {
		super();
	}

	@Override
	public boolean connect(SerialPort port) throws Exception {
		if (port.isOpen()) {
			System.out.println("Error: Port is currently in use");
			return false;
		} else {
			port.setBaudRate(baudRate);
			if (port.openPort(1000)) {
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				port.setParity(SerialPort.NO_PARITY);
				port.setNumDataBits(8);
				port.setNumStopBits(1);
				port.clearDTR();
				connectedPort = port;
				return true;
			}

			return false;
		}
	}
}