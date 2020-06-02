package sensors.lidar;

public class LidarSamplerateResponse implements LidarResponse {
    private static final int typelength = 4;
    private byte[] raw;
    private int tStandard;
    private int tExpress;
    
    @Override
	public int getLength() {
		return typelength;
	}
    
    public byte[] getRaw() {
		return raw;
	}

	public int getTStandard() {
		return tStandard;
	}

	public int getTExpress() {
		return tExpress;
	}

	public void parseData(byte[] data){
        this.raw = data;
        if (data.length < typelength) 
        	throw new RuntimeException("RESULT_INVALID_ANS_TYPE");
        //Model ID
        this.tStandard = (data[0] & 0xFF)+((data[1] & 0xFF)<<8);
        this.tExpress = (data[2] & 0xFF)+((data[3] & 0xFF)<<8);
    }
}
