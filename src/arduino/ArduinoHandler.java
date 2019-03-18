package arduino;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.logging.FileHandler;

import arduino.csv.CsvConverter;
import arduino.serial.TwoWaySerialComm;
import arduino.view.SerialMonitor;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class ArduinoHandler {
	private static ArduinoHandler instance;
	private TwoWaySerialComm serialComm;
	private SerialMonitor serialMonitor;
	private CsvConverter csvConverter;
	
	public static synchronized ArduinoHandler getInstance() {
		if(instance==null)
			instance=new ArduinoHandler();
		return instance;
	}
	
	/**
	 * @return the serialComm
	 */
	public TwoWaySerialComm getSerialComm() {
		return serialComm;
	}

	/**
	 * @return the serialMonitor
	 */
	public SerialMonitor getSerialMonitor() {
		return serialMonitor;
	}
	
	/**
	 * @return the ports
	 */
	public List<CommPortIdentifier> getPorts() {
		return serialComm.getAvailablePorts();
	}

	private ArduinoHandler() {
		serialComm=new TwoWaySerialComm();
		serialMonitor=new SerialMonitor();
		csvConverter=new CsvConverter();
		
		serialComm.addOnReceive(
			this.getOnReceive()
		);
	}
	
	public Function<String, Void> getOnReceive(){
		return (line) -> 
		{
			serialMonitor.append(line+"\n");
			double[] csvs = csvConverter.convertCsvs(line,";",".");
			
			return null;
		};
	}
	
}
