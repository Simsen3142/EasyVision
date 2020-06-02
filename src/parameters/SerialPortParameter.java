package parameters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.fazecast.jSerialComm.SerialPort;

import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;
import parameters.components.ParameterStringMultiOptionPanel;
import parameters.components.ParameterStringPanel;

public class SerialPortParameter extends StringMultiOptionParameter {
	private static final long serialVersionUID = -1248954497668843678L;
	
	public List<String> getOptions(){
		List<String> options=new ArrayList<String>();
		options.add(this.getValue());
		if(!options.contains("Any"))
			options.add("Any");
		TwoWaySerialComm.getAvailablePorts().forEach((port)->{
			String portname=port.getSystemPortName();
			if(!options.contains(portname))
				options.add(portname);
		});
		this.setOptions(options);
		return super.getOptions();
	}
	
	public SerialPortParameter(String name) {
		super(name, "Any");
	}
	
	public SerialPortParameter(String name, SerialPort port) {
		super(name, SerialHandler.getInstance().getConnectableSystemName(port));
	}
	
	public SerialPort getSelectedSerialPort() {
		SerialPort  port=null;
		if(getValue().equalsIgnoreCase("any")) {
			try{
				port=SerialHandler.getInstance().getSerialComms().get(0).getPort();
				if(port!=null) {
					return port;
				}
			}catch (NullPointerException | IndexOutOfBoundsException e) {
			}
			try{
				port=TwoWaySerialComm.getAvailablePorts().get(0);
				if(port!=null) {
					return port;
				}
			}catch (NullPointerException | IndexOutOfBoundsException e) {
			}
			return port;
		}
		
		port=SerialHandler.getInstance().getPortByName(getValue());
		return port;
	}
	
	public TwoWaySerialComm getSelectedCommunication() {
		return SerialHandler.getInstance().getCommunication(getSelectedSerialPort());
	}
	
	@Override
	public JComponent getEditComponent() {
		return new ParameterStringMultiOptionPanel(this);
	}
}
