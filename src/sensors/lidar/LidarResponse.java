package sensors.lidar;

public interface LidarResponse {
	public void parseData(byte[] data);
	public int getLength();
}
