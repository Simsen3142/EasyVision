package communication.serial.view;

import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

public class SerialLogger{
	private List<SerialMessage> msgs;
	private List<SerialMonitorAdvanced> serialMonitors;
	
	public SerialLogger() {
		msgs = new ArrayList<SerialMessage>();
		serialMonitors=new ArrayList<SerialMonitorAdvanced>();
	}
	
	public SerialMonitorAdvanced getNewSerialMonitor() {
		for(SerialMonitorAdvanced sm:serialMonitors) {
			if(!sm.isActive()) {
				return sm;
			}
		}
		return new SerialMonitorAdvanced(this);
	}
	
	public boolean addSerialMonitor(SerialMonitorAdvanced sm) {
		return serialMonitors.add(sm);
	}

	public List<SerialMessage> getMessages() {
		return msgs;
	}

	public boolean addMessage(SerialMessage msg) {
		for(SerialMonitorAdvanced sm:serialMonitors) {
			sm.append(msg.getText(), msg.getPort());
		}
		return msgs.add(msg);
	}
	
	public boolean addMessage(String text, SerialPort port, long time) {
		return addMessage(new SerialMessage(port, text, time));
	}

	public ArrayList<SerialMessage> getMessagesFrom(SerialPort port) {
		ArrayList<SerialMessage> ret = new ArrayList<SerialMessage>();
		msgs.forEach((msg) -> {
			if (msg.getPort().equals(port))
				ret.add(msg);
		});
		return ret;
	}

	public static String msgsToString(List<SerialMessage> msgs) {
		String s = "";
		for(SerialMessage msg:msgs) {
			s+=msg.getText()+"\n";
		}
		return s;
	}
}
