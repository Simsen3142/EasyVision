package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ParameterChangeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -310369601254850054L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JPanel pnl_paramChange;
	private ParameterizedObject parameterizedObject;
	private ParameterChangeDialog instance=this;
	
	/**
	 * Create the frame.
	 */
	public ParameterChangeDialog(ParameterizedObject parameterizedObjectNonSerializable) {
		addComponentListener(new ThisComponentListener());
		this.setTitle(parameterizedObjectNonSerializable.getClass().getSimpleName()+" - Parameter");
		
		if(parameterizedObjectNonSerializable instanceof RepresentationIcon) {
			((RepresentationIcon) parameterizedObjectNonSerializable).getRepresentationImage((img)->{
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
			ImageHandler.getImage("res/EVLogo.jpg", (img2)->{
				EventQueue.invokeLater(()->{
					if(img2!=null)
						setIconImage(img2);
				});
				return null;
			});
		}
		
		this.parameterizedObject=parameterizedObjectNonSerializable;
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
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
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
	
	private class ThisComponentListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			Dimension d=new Dimension(instance.getWidth()-20,0);
			pnl_paramChange.setMaximumSize(d);
			pnl_paramChange.setPreferredSize(d);
		}
	}
}
