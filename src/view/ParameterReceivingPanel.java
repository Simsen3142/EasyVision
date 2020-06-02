package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import general.ObjectSendingHandler;
import main.ParameterReceiver;
import parameters.DoubleParameter;
import parameters.Parameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ParameterReceivingPanel extends JPanel implements ParameterReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -153725482784880394L;
	private transient ParameterizedObject sender;
	private transient JLabel lblParamname;
	private transient JScrollPane scrollPane;
	private transient JTextArea txtParamvalue;
	private transient String paramFullName="output";
	private transient ParameterReceivingPanel instance=this;
	
	public ParameterReceivingPanel() {}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ParameterReceivingPanel(ParameterizedObject sender) {
		setBackground(Color.WHITE);
		this.sender = sender;
		sender.addParameterReceiver(this);
		setLayout(new MigLayout("insets 0, gap 0", "[grow][grow][grow]", "[grow][][][grow]"));
		
		lblParamname = new JLabel("Name");
		lblParamname.setFont(new Font("Arial", Font.PLAIN, 15));
		add(lblParamname, "cell 1 1");
		
		txtParamvalue = new JTextArea("Value");
		txtParamvalue.setEditable(false);
		txtParamvalue.setBorder(null);
		txtParamvalue.setOpaque(false);
		txtParamvalue.setToolTipText("CLICK to copy to Clipboard");
		txtParamvalue.setFont(new Font("Arial", Font.BOLD, 50));
		txtParamvalue.setFocusable(false);
		txtParamvalue.addMouseListener(new MouseAdapter() {
			private Object lock=new Object();
			@Override
			public void mousePressed(MouseEvent me) {
				synchronized (lock) {
					lock.notifyAll();
				}
				EventQueue.invokeLater(()->{
					txtParamvalue.setForeground(Color.DARK_GRAY);
					revalidate();
					repaint();
				});
				ObjectSendingHandler.writeToClipboard(txtParamvalue.getText());
				
				new Thread(()-> {
					synchronized (lock) {
						try {
							lock.wait(500);
							EventQueue.invokeLater(()->{
								txtParamvalue.setForeground(Color.BLACK);
								revalidate();
								repaint();
							});
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		scrollPane=new JScrollPane(txtParamvalue);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setOpaque(false);
		add(scrollPane, "cell 1 2,grow");
		scrollPane.addComponentListener(new FontsizeChanger());
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
	public void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		if(txtParamvalue!=null) {
			txtParamvalue.addMouseListener(l);
		}
	}
	
	@Override
	public void addMouseMotionListener(MouseMotionListener l) {
		super.addMouseMotionListener(l);
		if(txtParamvalue!=null) {
			txtParamvalue.addMouseMotionListener(l);
		}
	}
	
	@Override
	public void removeMouseListener(MouseListener l) {
		super.addMouseListener(l);
//		if(scrollPane!=null) {
//			scrollPane.removeMouseListener(l);
//		}
		if(txtParamvalue!=null) {
			txtParamvalue.removeMouseListener(l);
		}
	}
	
	@Override
	public void removeMouseMotionListener(MouseMotionListener l) {
		super.removeMouseMotionListener(l);
//		if(scrollPane!=null) {
//			scrollPane.removeMouseMotionListener(l);
//		}
		if(txtParamvalue!=null) {
			txtParamvalue.removeMouseMotionListener(l);
		}
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
		ParameterObject param=ParameterizedObject.getFirstFittingParameter(parameters, Parameter.class,paramFullName);
		if(param instanceof Parameter<?>) {
			EventQueue.invokeLater(()->{
				lblParamname.setText(param.getName());
				String pvalue=((Parameter<?>) param).getValue()+"";
				if(param instanceof DoubleParameter) {
					pvalue=String.format("%,.2f", ((DoubleParameter) param).getValue());
				}
				txtParamvalue.setText(pvalue);
				revalidate();
				repaint();
			});
		}
	}
	
	private class FontsizeChanger extends ComponentAdapter{
		@Override
		public void componentResized(ComponentEvent e) {
			int minFont=10;
			int maxFont=50;
			FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
			Font f = null;
			
			int maxWidth=instance.getWidth()-20;
			
			String txt=txtParamvalue.getText();
			int fontSize=maxFont;
			
			for(;fontSize>=minFont;fontSize-=5) {
				boolean fitting=true;
				f=new Font("Arial", Font.BOLD, fontSize);
				for(String line:txt.split("\n")) {
					System.out.println("LINE: "+line);

					Rectangle2D rect=f.getStringBounds(line, frc);
					System.out.println(rect.getWidth()+" - "+maxWidth);

					if(rect.getWidth()>maxWidth) {
						fitting=false;
						break;
					}
				}
				if(fitting) {
					break;
				}
			}
			
			if(f==null) {
				f=new Font("Arial", Font.BOLD, maxFont);
			}
			txtParamvalue.setFont(f);
			EventQueue.invokeLater(()->{
				revalidate();
				repaint();
			});
		}
	}
}
