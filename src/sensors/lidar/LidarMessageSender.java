package sensors.lidar;

public interface LidarMessageSender {
	public boolean addLidarListener(LidarListener lstnr);
	public boolean removeLidarListener(Object lstnr);
}