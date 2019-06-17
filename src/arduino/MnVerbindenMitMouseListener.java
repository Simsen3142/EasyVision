package arduino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import arduino.serial.TwoWaySerialComm;
import connections.ConnectionHandler;
import gnu.io.CommPortIdentifier;

public class MnVerbindenMitMouseListener<T> extends MouseAdapter {
	private JMenu menu;
	private ConnectionHandler<T> conHandler;
	
	public MnVerbindenMitMouseListener(JMenu menu, ConnectionHandler<T> connectionHandler) {
		this.menu=menu;
		this.conHandler=connectionHandler;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		conHandler.setOnConnectableSearchDone((connectables)->{
			this.menu.setVisible(false);
			initVerbindenMitMenu(this.menu,connectables);
			this.menu.setVisible(true);
			this.menu.revalidate();
			this.menu.repaint();
			return null;
		});
		
		List<T> cons = conHandler.getConnectables();
		if(cons.size()<1) {
			conHandler.searchConnectables();
		}
		
		initVerbindenMitMenu(this.menu,cons);
	}

	public void initVerbindenMitMenu(JMenu mnConnectTo, List<T> cons) {
		mnConnectTo.removeAll();

		if (conHandler.isConnected()) {
			JMenuItem mnItem = new JMenuItem("Verbunden mit " + conHandler.getConnectableName(conHandler.getConnected()) + " - disconnect");
			mnItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					conHandler.disconnect();
				}
			});
			mnConnectTo.add(mnItem);
		}

		for (T portId : cons) {
			JMenuItem mnItem = new JMenuItem(conHandler.getConnectableName(portId));
			mnItem.addActionListener(new ConnectToPortActionListener<T>(portId,conHandler));
			mnConnectTo.add(mnItem);
		}
		
		mnConnectTo.addSeparator();
		JMenuItem mntmSearch=new JMenuItem("Searching...");
		JMenuItem mntmRefresh=new JMenuItem("Refresh");
		
		if(conHandler.isSearching()) {
			mnConnectTo.add(mntmSearch);
		}else {
			mnConnectTo.add(mntmRefresh);
//			mntmRefresh.removeMouseListener(mntmRefresh.getListeners(MouseListener.class)[0]);
			mntmRefresh.addActionListener((e)->{
				conHandler.searchConnectables();
				mnConnectTo.remove(mntmRefresh);
				mnConnectTo.add(mntmSearch);
			});
		}
	}
}