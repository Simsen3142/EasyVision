package communication.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.CompletionHandler;

import javax.swing.JOptionPane;

import communication.ConnectionHandler;
import communication.SuperConnHandler;

public class ConnectToActionListener<T> implements ActionListener {
	private SuperConnHandler<T> conHandler;
	private T id;

	public ConnectToActionListener(T id,SuperConnHandler<T> conHandler) {
		this.id = id;
		this.conHandler=conHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (conHandler instanceof ConnectionHandler<?>) {
				if(((ConnectionHandler<?>)conHandler).isConnected()) {
					((ConnectionHandler<?>)conHandler).disconnect();
				}
			}
			if(!conHandler.connectTo(id)) {
				JOptionPane.showMessageDialog(null, "Connection was not successful", "Error", JOptionPane.ERROR_MESSAGE);
			};
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}