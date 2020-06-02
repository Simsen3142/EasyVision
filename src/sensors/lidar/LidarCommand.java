package sensors.lidar;

public enum LidarCommand {
	STOP(0x25), RESET(0x40), SCAN(0x20), FORCE_SCAN(0x21), GET_INFO(0x50), GET_HEALTH(0x52), GET_SAMPLERATE(0x59);

	public final byte B;

	LidarCommand(byte b) {
		this.B = b;
	}

	LidarCommand(int b) {
		this.B = (byte) (b & 0xFF);
	}
}