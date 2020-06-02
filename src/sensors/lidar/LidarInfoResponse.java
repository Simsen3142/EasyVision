package sensors.lidar;

import org.apache.pdfbox.util.Hex;

public class LidarInfoResponse implements LidarResponse {
    private static final int typelength = 20;
    private byte[] raw;
    private String serialNumber;
    private String firmwareVersion; 
    private String hardwareVersion;
    private String modelID;
    
    @Override
	public int getLength() {
		return typelength;
	}
    
    public byte[] getRaw() {
		return raw;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public String getModelID() {
		return modelID;
	}

	public void parseData(byte[] data){
        this.raw = data;
        if (data.length < typelength) 
        	throw new RuntimeException("RESULT_INVALID_ANS_TYPE");
        //Model ID
        byte model = data[0];
        this.modelID = (int)model+"";
        byte firmware_version_minor = data[1];
        byte firmware_version_major = data[2];
        this.firmwareVersion = firmware_version_major + "." + firmware_version_minor;
        byte hardware_version = data[3];
        this.hardwareVersion = (int)hardware_version+"";
        
        // 128bit unique serial number 
        byte[] serial_number = new byte[16];
        String serial="";
        for (int i = 4; i < typelength; i++){
            serial_number[i - 4] = data[i];
            serial+=Hex.getString(data[i]);
        }
        this.serialNumber = serial;
    }
}
