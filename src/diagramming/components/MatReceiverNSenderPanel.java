package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import database.ImageHandler;
import functions.RepresentationIcon;
import listener.ListenerHandler;
import main.MatSender;
import view.MatReceiverPanel;
import view.PanelFrame;

import javax.swing.JPopupMenu;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import java.awt.Font;

public abstract class MatReceiverNSenderPanel extends FunctionPanel<MatSender> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8869419565109116155L;
	private static JMenuItem mntmParameter;
	private static JCheckBoxMenuItem chckbxmntmPic;
	private boolean showPicture=false;
	private MatReceiverPanel matReceiverPanel;
	private static JMenuItem mntmInFensterAnzeigen;
	private MatReceiverNSenderPanel instance=this;

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public MatReceiverNSenderPanel(MatSender matSender, String name) {
		super(matSender,name);
	}


	@Override
	protected JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();

		mntmParameter = new JMenuItem("Parameter");
		popupMenu.add(mntmParameter);

		chckbxmntmPic = new JCheckBoxMenuItem("Bild");
		popupMenu.add(chckbxmntmPic);

		mntmInFensterAnzeigen = new JMenuItem("In Fenster anzeigen");
		popupMenu.add(mntmInFensterAnzeigen);
		
		removeAllActionListeners(mntmParameter);
		mntmParameter.addActionListener(new MntmParameterActionListener());

		chckbxmntmPic.setVisible(true);
		removeAllActionListeners(chckbxmntmPic);
		chckbxmntmPic.addActionListener(new ChckbxmntmPicActionListener());
		chckbxmntmPic.setSelected(showPicture);
		
		mntmInFensterAnzeigen.setVisible(true);
		removeAllActionListeners(mntmInFensterAnzeigen);
		mntmInFensterAnzeigen.addActionListener(new MntmInFensterAnzeigenActionListener());
			
		return popupMenu;
	}

	private void removeAllActionListeners(JMenuItem component) {
		List<ActionListener> removals=new ArrayList<>();
		for(ActionListener al:component.getActionListeners()) {
			removals.add(al);
		}
		removals.forEach((al)->component.removeActionListener(al));
	}
	
	private class MntmParameterActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			function.showParameterChangeDialog();
		}
	}

	private class ChckbxmntmPicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showPicture=chckbxmntmPic.isSelected();
			if (showPicture) {
				hideLabel(true);
				matReceiverPanel = new MatReceiverPanel(function);
				ListenerHandler.copyListeners(instance, matReceiverPanel);
				add(matReceiverPanel, BorderLayout.CENTER);
				matReceiverPanel.repaint();
			} else {
				function.removeMatReceiver(matReceiverPanel);
				remove(matReceiverPanel);
				ListenerHandler.clearListeners(matReceiverPanel);
				matReceiverPanel = null;
				hideLabel(false);
			}
			repaint();
		}
	}

	private class MntmInFensterAnzeigenActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			MatReceiverPanel panel = new MatReceiverPanel(function);
			PanelFrame frame = new PanelFrame(panel);
			frame.setOnWindowClosing((nix) -> {
				function.removeMatReceiver(panel);
				return null;
			});
			
			frame.setTitle(function.getClass().getSimpleName()+" - Picture");
			
			if(function instanceof RepresentationIcon) {
				((RepresentationIcon) function).getRepresentationImage((img)->{
					if(img!=null) {
						frame.setIconImage(img);
					}else {
						ImageHandler.getImage("res/EVLogo.jpg", (img2)->{
							EventQueue.invokeLater(()->{
								if(img2!=null)
									frame.setIconImage(img2);
							});
							return null;
						});
					}
					return null;					
				});
			}else {
				ImageHandler.getImage("res/EVLogo.jpg", (img2)->{
					EventQueue.invokeLater(()->{
						if(img2!=null)
							frame.setIconImage(img2);
					});
					return null;
				});
			}
			
			frame.setVisible(true);
		}
	}
}
