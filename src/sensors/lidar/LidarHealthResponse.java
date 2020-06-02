package sensors.lidar;

import org.apache.pdfbox.util.Hex;

public class LidarHealthResponse implements LidarResponse{
	private static final int typelength = 3;
	private byte[] raw;
	private int status;
	private int errorCode;
	
	@Override
	public int getLength() {
		return typelength;
	}
	
	public byte[] getRaw() {
		return raw;
	}

	public int getStatus() {
		return status;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void parseData(byte[] data) {
		this.raw = data;
		if (data.length < typelength)
			throw new RuntimeException("RESULT_INVALID_ANS_TYPE");
		
		this.status = data[0];
		this.errorCode = (data[1] & 0xFF)+((data[2] & 0xFF)<<8);
	}

}