package sensors.lidar;

import java.util.Map;

public interface LidarListener {
	public void onMessageReceived(LidarMessage msg);
	public void onFullTurnDone(Map<Integer,LidarMessage> msgs);
}
