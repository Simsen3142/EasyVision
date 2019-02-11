package diagramming.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cvfunctions.MatEditFunction;
import view.MatReceiverPanel;

import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;

public class CVFunctionPanel extends MatReceiverNSenderPanel {
	private JLabel lblFunction;
	private Class<? extends MatEditFunction> functionClass;
	private MatEditFunction function;

	/**
	 * Create the panel.
	 */
	public CVFunctionPanel(Class<? extends MatEditFunction> functionClass) {
		super();
		this.functionClass=functionClass;
		lblFunction = new JLabel(this.functionClass.getSimpleName());
		super.initialize(initFunction(), functionClass.getSimpleName());
	}

	/**
	 * @return the functionClass
	 */
	public Class<? extends MatEditFunction> getFunctionClass() {
		return functionClass;
	}
	
	private MatEditFunction initFunction() {
		if(function==null) {
			try {
				function=functionClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return function;
	}
}
