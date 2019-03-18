package arduino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import arduino.serial.TwoWaySerialComm;
import gnu.io.CommPortIdentifier;

public class MnVerbindenMitMouseListener extends MouseAdapter {
	private JMenu menu;
	
	public MnVerbindenMitMouseListener(JMenu menu) {
		this.menu=menu;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		initVerbindenMitMenu(this.menu);
	}

	public void initVerbindenMitMenu(JMenu mnConnectTo) {
		TwoWaySerialComm serialComm=ArduinoHandler.getInstance().getSerialComm();
		List<CommPortIdentifier> ports = serialComm.getAvailablePorts();
		
		mnConnectTo.removeAll();

		if (serialComm.isConnected()) {
			JMenuItem mnItem = new JMenuItem("Verbunden mit " + serialComm.getPort().getName() + " - disconnect");
			mnItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					serialComm.disconnect();
				}
			});
			mnConnectTo.add(mnItem);
		}

		for (CommPortIdentifier portId : ports) {
			JMenuItem mnItem = new JMenuItem(portId.getName());
			mnItem.addActionListener(new ConnectToPortActionListener(portId));
			mnConnectTo.add(mnItem);
		}
	}
}