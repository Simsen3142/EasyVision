package diagramming.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import main.MatSender;
import main.ParameterReceiver;
import parameters.ParameterizedObject;

import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParameterReceiverPanel extends FunctionPanel<ParameterizedObject> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4667924427485866328L;
	private JLabel lblName;
	private static JPopupMenu popupMenu;
	private static JMenuItem mntmParameter;
	
	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public ParameterReceiverPanel(ParameterReceiver parameterReceiver, String name) {
		super((ParameterizedObject)parameterReceiver,name);
		initialize((ParameterizedObject)parameterReceiver, name);
	}


	
	@Override
	protected JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();

		mntmParameter = new JMenuItem("Parameter");
		mntmParameter.addActionListener(new MntmParameterActionListener());
		popupMenu.add(mntmParameter);
		
		return popupMenu;
	}
	

	private class MntmParameterActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(function instanceof ParameterizedObject) {
				((ParameterizedObject) function).showParameterChangeDialog();
			}
		}
	}
}
