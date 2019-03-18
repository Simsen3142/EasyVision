package arduino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import arduino.serial.TwoWaySerialComm;
import gnu.io.CommPortIdentifier;

public class ConnectToPortActionListener implements ActionListener {
	private CommPortIdentifier id;

	public ConnectToPortActionListener(CommPortIdentifier id) {
		this.id = id;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TwoWaySerialComm serialComm=ArduinoHandler.getInstance().getSerialComm();
		try {
			if (serialComm.isConnected()) {
				serialComm.disconnect();
			}
			serialComm.connect(id);
			serialComm.startReader();
			serialComm.addOnReceive(ArduinoHandler.getInstance().getOnReceive());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}