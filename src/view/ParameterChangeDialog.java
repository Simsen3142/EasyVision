package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

import database.ImageHandler;
import functions.RepresentationIcon;

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
		this.setTitle(parameterizedObject.getClass().getSimpleName()+" - Parameter");
		
		if(parameterizedObject instanceof RepresentationIcon) {
			((RepresentationIcon) parameterizedObject).getRepresentationImage((img)->{
				System.out.println("TEST");
				if(img!=null) {
					setIconImage(img);
				}else {
					ImageHandler.getImage("res/EVLogo.jpg", (img2)->{
						EventQueue.invokeLater(()->{
							if(img2!=null)
								setIconImage(img2);
						});
						return null;
					});
				}
				return null;					
			});
		}else {
			System.out.println("DZIUWKDJ");
			ImageHandler.getImage("res/EVLogo.jpg", (img2)->{
				EventQueue.invokeLater(()->{
					if(img2!=null)
						setIconImage(img2);
				});
				return null;
			});
		}
		
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
		for(ParameterObject param:parameterizedObject.getAllParameters().values()) {
			if(param.getParamGroup()==null) {
				pnl_paramChange.add(param.getComponent(parameterizedObject));
			}
		}
	}
}
