package diagramming.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cvfunctions.MatEditFunction;
import main.MatSender;
import view.MatReceiverPanel;
import view.PanelFrame;

import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

public abstract class MatReceiverNSenderPanel extends JButton {
	private JLabel lblName;
	private MatSender matSender;
	private static JPopupMenu popupMenu;
	private static JMenuItem mntmParameter;
	private static JCheckBoxMenuItem chckbxmntmBild;
	private boolean showPicture=false;
	private MatReceiverPanel matReceiverPanel;
	private static JMenuItem mntmInFensterAnzeigen;

	/**
	 * @return the matSender
	 */
	public MatSender getMatSender() {
		return matSender;
	}

	public MatReceiverNSenderPanel() {
		super();
	}

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public MatReceiverNSenderPanel(MatSender matSender, String name) {
		super();
		initialize(matSender, name);
	}

	protected void initialize(MatSender matSender, String name) {
		this.matSender = matSender;
		lblName = new JLabel(name);

		initPopup();
		addPopup(this);

		setLayout(new BorderLayout(0, 0));

		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName, BorderLayout.CENTER);

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}
	
	private static void initPopup() {
		if(popupMenu==null) {
			popupMenu = new JPopupMenu();
	
			mntmParameter = new JMenuItem("Parameter");
			popupMenu.add(mntmParameter);
	
			chckbxmntmBild = new JCheckBoxMenuItem("Bild");
			popupMenu.add(chckbxmntmBild);
	
			mntmInFensterAnzeigen = new JMenuItem("In Fenster anzeigen");
			popupMenu.add(mntmInFensterAnzeigen);
		}
	}

	private void addPopup(Component component) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				removeAllActionListeners(mntmParameter);
				mntmParameter.addActionListener(new MntmParameterActionListener());

				removeAllActionListeners(chckbxmntmBild);
				chckbxmntmBild.addActionListener(new ChckbxmntmBildActionListener());
				chckbxmntmBild.setSelected(showPicture);
				
				removeAllActionListeners(mntmInFensterAnzeigen);
				mntmInFensterAnzeigen.addActionListener(new MntmInFensterAnzeigenActionListener());

				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
			
			private void removeAllActionListeners(JMenuItem component) {
				List<ActionListener> removals=new ArrayList<>();
				for(ActionListener al:component.getActionListeners()) {
					removals.add(al);
				}
				removals.forEach((al)->component.removeActionListener(al));
			}
		});
	}

	private class MntmParameterActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			matSender.showParameterChangeDialog();
		}
	}

	private class ChckbxmntmBildActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showPicture=chckbxmntmBild.isSelected();
			if (showPicture) {
				matReceiverPanel = new MatReceiverPanel(matSender);
				remove(lblName);
				add(matReceiverPanel, BorderLayout.CENTER);
			} else {
				matSender.removeMatReceiver(matReceiverPanel);

				remove(matReceiverPanel);
				add(lblName, BorderLayout.CENTER);

				matReceiverPanel = null;
			}
		}
	}

	private class MntmInFensterAnzeigenActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			MatReceiverPanel panel = new MatReceiverPanel(matSender);
			PanelFrame frame = new PanelFrame(panel);
			frame.setOnWindowClosing((nix) -> {
				matSender.removeMatReceiver(panel);
				return null;
			});
			frame.setVisible(true);
		}
	}
}
