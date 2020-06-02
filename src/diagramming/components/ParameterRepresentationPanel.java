package diagramming.components;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import functions.parameterreceiver.ParameterRepresenter;
import listener.ListenerHandler;
import parameters.ParameterizedObject;
import view.ParameterReceivingPanel;

import javax.swing.JPopupMenu;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParameterRepresentationPanel extends FunctionPanel<ParameterRepresenter<?>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4667924427485866328L;
	private boolean showPicture=false;
	private transient static JMenuItem mntmParameter;
	private transient static JCheckBoxMenuItem chckbxmntmPic;
	private transient ParameterReceivingPanel paramRecPanel;
	private transient ParameterRepresentationPanel instance=this;


	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public ParameterRepresentationPanel(ParameterRepresenter<?> paramRepresenter, String name) {
		super((ParameterRepresenter<?>)paramRepresenter,name);
	}


	
	@Override
	protected JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();

		mntmParameter = new JMenuItem("Parameter");
		mntmParameter.addActionListener(new MntmParameterActionListener());
		popupMenu.add(mntmParameter);
		
		chckbxmntmPic = new JCheckBoxMenuItem("Bild");
		popupMenu.add(chckbxmntmPic);
		
		chckbxmntmPic.setVisible(true);
		removeAllActionListeners(chckbxmntmPic);
		chckbxmntmPic.addActionListener(new ChckbxmntmPicActionListener());
		chckbxmntmPic.setSelected(showPicture);
		
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
			if(function instanceof ParameterizedObject) {
				((ParameterizedObject) function).showParameterChangeDialog();
			}
		}
	}
	
	public void showParameterValue(boolean show) {
		showPicture=show;
		EventQueue.invokeLater(()->{
			if(show) {
				hideLabel(true);
				paramRecPanel = new ParameterReceivingPanel(function);
				ListenerHandler.copyListeners(instance, paramRecPanel);
				add(paramRecPanel, BorderLayout.CENTER);
				paramRecPanel.repaint();
			}else {
				if(paramRecPanel != null) {
					function.removeParamterReceiver(paramRecPanel);
					remove(paramRecPanel);
					ListenerHandler.clearListeners(paramRecPanel);
					paramRecPanel = null;
				}
				hideLabel(false);
			}
			repaint();
		});
	}
	
	private class ChckbxmntmPicActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showParameterValue(chckbxmntmPic.isSelected());
		}
	}
}
