package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Map;

import javax.swing.JPanel;

import org.opencv.core.*;
import main.MatReceiver;
import main.MatSender;
import main.ParameterReceiver;
import parameters.BooleanParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;

public class ParameterReceivingPanel extends JPanel implements ParameterReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -153725482784880394L;
	private transient ParameterizedObject sender;
	private JLabel lblParamname;
	private JLabel lblParamvalue;
	private String paramFullName="output";
	
	/**
	 * @wbp.parser.constructor
	 */
	public ParameterReceivingPanel(ParameterizedObject sender) {
		setBackground(Color.WHITE);
		this.sender = sender;
		sender.addParameterReceiver(this);
		setLayout(new MigLayout("insets 0, gap 0", "[grow][][grow]", "[grow][][][grow]"));
		
		lblParamname = new JLabel("Name");
		lblParamname.setFont(new Font("Arial", Font.PLAIN, 15));
		add(lblParamname, "cell 1 1");
		
		lblParamvalue = new JLabel("Value");
		lblParamvalue.setFont(new Font("Arial", Font.BOLD, 50));
		add(lblParamvalue, "cell 1 2");
	}
	
	public ParameterReceivingPanel(String paramFullName) {
		this.paramFullName=paramFullName;
	}

	public void stop() {
		sender.removeParamterReceiver(this);
	}

	//Unused TODO: change structure (I am afraid, this won't happen so soon)
	@Override
	public void recalculateId() {
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		ParameterObject param=ParameterizedObject.getFirstFittingParameter(parameters, Parameter.class,paramFullName);
		if(param instanceof Parameter<?>) {
			lblParamname.setText(param.getName());
			lblParamvalue.setText(((Parameter<?>) param).getValue()+"");
			
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}
		// TODO Auto-generated method stub
		
	}
	
}
