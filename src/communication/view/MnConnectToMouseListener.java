package communication.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import communication.ConnectionHandler;
import communication.MultiConnHandler;
import communication.SuperConnHandler;
import gnu.io.CommPortIdentifier;

public class MnConnectToMouseListener<T> extends MouseAdapter {
	private JMenu menu;
	private ConnectionHandler<T> singleConHandler;
	private MultiConnHandler<T> multiConHandler;
	private SuperConnHandler<T> conHandler;
	private boolean multi=false;
	
	public MnConnectToMouseListener(JMenu menu, ConnectionHandler<T> connectionHandler) {
		this.menu=menu;
		this.conHandler=singleConHandler=connectionHandler;
	}
	
	public MnConnectToMouseListener(JMenu menu, MultiConnHandler<T> connectionHandler) {
		this.menu=menu;
		this.conHandler=multiConHandler=connectionHandler;
		multi=true;
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
			if(!multi) {
				JMenuItem mnItem = createDisconnectMenuItem(singleConHandler.getConnected());
				mnConnectTo.add(mnItem);
			}else {
				for(T x:multiConHandler.getConnected()) {
					JMenuItem mnItem = createDisconnectMenuItem(x);
					mnConnectTo.add(mnItem);
				}
			}
		}

		for (T portId : cons) {
			JMenuItem mnItem = new JMenuItem(conHandler.getConnectableName(portId));
			mnItem.addActionListener(new ConnectToActionListener<T>(portId,conHandler));
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
	
	private JMenuItem createDisconnectMenuItem(T t) {
		JMenuItem mnItem = new JMenuItem("Verbunden mit " + conHandler.getConnectableName(t) + " - disconnect");
		mnItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(multi) {
					multiConHandler.disconnectFrom(t);
				}else {
					singleConHandler.disconnect();
				}
			}
		});
		return mnItem;
	}
}