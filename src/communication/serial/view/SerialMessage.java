package communication.serial.view;

import com.fazecast.jSerialComm.SerialPort;

public class SerialMessage implements Comparable<SerialMessage>{
	private SerialPort port;
	private String text;
	private long time;
	public SerialPort getPort() {
		return port;
	}
	public String getText() {
		return text;
	}
	public long getTime() {
		return time;
	}
	public SerialMessage(SerialPort port, String text, long time) {
		this.port = port;
		this.text = text;
		this.time = time;
	}
	
	@Override
	public int compareTo(SerialMessage o) {
		return new Long(this.time).compareTo(time);
	}
}
