package sensors.lidar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LidarReader {
	private List<LidarListener> listeners;

	public boolean addLidarListener(LidarListener listener) {
		return listeners.add(listener);
	}

	public boolean removeLidarListener(Object listener) {
		return listeners.remove(listener);
	}

	public LidarReader() {
		listeners = new ArrayList<LidarListener>();
	}
	
	private void triggerOnMessageReceived(LidarMessage msg) {
		listeners.forEach((lstnr)->lstnr.onMessageReceived(msg));
	}
	
	private void triggerOnFullTurnDone(Map<Integer,LidarMessage> msgs) {
		listeners.forEach((lstnr)->lstnr.onFullTurnDone(msgs));
	}
}
