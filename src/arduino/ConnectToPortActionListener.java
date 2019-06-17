package arduino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import arduino.serial.TwoWaySerialComm;
import connections.ConnectionHandler;
import gnu.io.CommPortIdentifier;

public class ConnectToPortActionListener<T> implements ActionListener {
	private ConnectionHandler<T> conHandler;
	private T id;

	public ConnectToPortActionListener(T id,ConnectionHandler<T> conHandler) {
		this.id = id;
		this.conHandler=conHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (conHandler.isConnected()) {
				conHandler.disconnect();
			}
			if(!conHandler.connectTo(id)) {
				JOptionPane.showMessageDialog(null, "Connection was not successful", "Error", JOptionPane.ERROR_MESSAGE);
			};
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}