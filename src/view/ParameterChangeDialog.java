package view;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

public class ParameterChangeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -310369601254850054L;
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
