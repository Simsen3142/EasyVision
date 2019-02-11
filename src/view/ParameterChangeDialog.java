package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;

import cvfunctions.MatEditFunction;
import parameters.NumberParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.group.ColorParameterGroup;
import parameters.group.ParameterGroup;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComponent;

public class ParameterChangeDialog extends JDialog {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JPanel pnl_paramChange;
	private ParameterizedObject parameterizedObject;
	
	/**
	 * Create the frame.
	 */
	public ParameterChangeDialog(ParameterizedObject parameterizedObject) {
		this.parameterizedObject=parameterizedObject;
		initialize();
	}
	
	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 500);
		setAlwaysOnTop(true);  
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		pnl_paramChange = new JPanel();
		scrollPane = new JScrollPane(pnl_paramChange);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		pnl_paramChange.setLayout(new BoxLayout(pnl_paramChange, BoxLayout.Y_AXIS));
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		initParamChangers();
	}

	private void initParamChangers() {
		for(ParameterObject param:parameterizedObject.getParameters().values()) {
			if(param.getParamGroup()==null) {
				pnl_paramChange.add(param.getComponent());
			}
		}
	}
}
