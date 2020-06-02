package communication.netsocket;

import java.util.ArrayList;
import java.util.List;
import com.fazecast.jSerialComm.SerialPort;

import communication.LidarReadingThread;
import communication.ReadingThread;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessageSender;

public class SocketCommLidar extends SocketComm implements LidarMessageSender{
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

	public SocketCommLidar() {
		super();
	}
}