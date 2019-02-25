package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import main.ParameterReceiver;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParameterReceiverPanel extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4667924427485866328L;
	private JLabel lblName;
	private static JPopupMenu popupMenu;
	private static JMenuItem mntmParameter;
	private ParameterReceiver paramReceiver;
	
	/**
	 * @return the paramReceiver
	 */
	public ParameterReceiver getParamReceiver() {
		return paramReceiver;
	}

	/**
	 * @param paramReceiver the paramReceiver to set
	 */
	public void setParamReceiver(ParameterReceiver paramReceiver) {
		this.paramReceiver = paramReceiver;
	}

	public ParameterReceiverPanel() {
		super();
	}

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public ParameterReceiverPanel(ParameterReceiver paramRec, String name) {
		super();
		this.paramReceiver=paramRec;
		initialize(paramRec, name);
	}

	protected void initialize(ParameterReceiver paramRec, String name) {
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
//			paramReceiver.showParameterChangeDialog();
		}
	}
}
