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

public class MatEditFunctionDiagramPanel extends MatReceiverNSenderPanel {
	private JLabel lblFunction;
	private MatEditFunction function;

	/**
	 * Create the panel.
	 */
	public MatEditFunctionDiagramPanel(MatEditFunction function) {
		super();
		this.function=function;
		lblFunction = new JLabel(function.getClass().getSimpleName());
		super.initialize(function, function.getClass().getSimpleName());
	}
}
