package communication.serial.view;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.fazecast.jSerialComm.SerialPort;

import communication.ConnectionListener;
import communication.serial.SerialHandler;
import general.SystemParameters;

import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import view.PanelFrame;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JComboBox;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class SerialMonitorAdvanced extends JPanel implements ConnectionListener<SerialPort> {
	private static final long serialVersionUID = -866815762261233814L;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton btnClearOutput;
	private JCheckBox chckbxAutoscroll;
	private PanelFrame frame;
	private JButton btnStopplay;
	protected boolean pauseImplemented=false;
	private JPanel panel;
	private JCheckBox chckbxShowTimestamp;
	private SimpleDateFormat sdfDate;
	private JPanel panel_1;
	private JTextField tfSend;
	private JButton btnSend;
	private JComboBox<String> comboBox;
	private DefaultComboBoxModel<String> comOption;
	private JComboBox<String> comboBoxLineEnd;
	private DefaultComboBoxModel<String> lnOption;
	private JCheckBox chckbxAlwaysOnTop;
	private SerialMonitorAdvanced instance=this;
	private boolean initialized=false;

	/**
	 * Create the panel.
	 */
	public SerialMonitorAdvanced(SerialLogger logger) {
		sdfDate = new SimpleDateFormat("HH:mm:ss.SSS");
		comOption=new DefaultComboBoxModel<String>();
		comOption.addElement("All");
		SerialHandler.getInstance().getConnected().forEach((port)->comOption.addElement(port.getDescriptivePortName()));

		lnOption=new DefaultComboBoxModel<String>();
		lnOption.addElement("New line");
		lnOption.addElement("No line ending");

		logger.addSerialMonitor(this);
		SerialHandler.getInstance().addConListener(this);
		initialize();
	}
	
	
	protected void initialize() {
		setLayout(new MigLayout("", "[450px,grow][50%:n:50%,grow]", "[][300px,grow][]"));
		
		panel_1 = new JPanel();
		add(panel_1, "cell 0 0 2 1,grow");
		panel_1.setLayout(new MigLayout("insets 0 0", "[grow]", "[][]"));
		
		SendActionListener sal=new SendActionListener();
		tfSend = new JTextField();
		tfSend.addFocusListener(new TfSendFocusListener());
		tfSend.addKeyListener(sal);
		panel_1.add(tfSend, "flowx,cell 0 0,growx");
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(sal);
		panel_1.add(btnSend, "cell 0 0");
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 1,grow");
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		btnClearOutput = new JButton("Clear Output");
		btnClearOutput.addActionListener(new BtnClearOutputActionListener());
		
		comboBoxLineEnd = new JComboBox<String>(lnOption);
		add(comboBoxLineEnd, "flowx,cell 1 2,alignx right");
		
		comboBox = new JComboBox<String>(comOption);
		add(comboBox, "cell 1 2,alignx right");
		add(btnClearOutput, "cell 1 2,alignx right");
		
		panel = new JPanel();
		panel.setOpaque(false);
		add(panel, "flowx,cell 0 2");
		
		chckbxAutoscroll = new JCheckBox("Autoscroll");
		panel.add(chckbxAutoscroll);
		chckbxAutoscroll.setSelected(true);
		
		chckbxShowTimestamp = new JCheckBox("Show timestamp");
		panel.add(chckbxShowTimestamp);
		
		chckbxAlwaysOnTop = new JCheckBox("Always on top");
		chckbxAlwaysOnTop.addChangeListener(new ChckbxAlwaysOnTopChangeListener());
		panel.add(chckbxAlwaysOnTop);
		
		if(pauseImplemented) {
			btnStopplay = new JButton("StopPlay");
			btnStopplay.setToolTipText("Stop/Play");
			btnStopplay.addActionListener(new BtnPauseplayActionListener());
			add(btnStopplay, "cell 0 1");
		}
		initialized=true;
	}

	public void append(String t, SerialPort res) {
		if(initialized) {
			SerialPort selectedPort=getSelectedPort();
			
			if(selectedPort==null||selectedPort.getDescriptivePortName().equals(res.getDescriptivePortName())) {
				if(chckbxShowTimestamp.isSelected()) {
					String time=sdfDate.format(System.currentTimeMillis());
					t=time+" -> "+t;
				}
				if(comboBoxLineEnd.getSelectedItem().equals("New line")) {
					t+="\n";
				}
				textArea.append(t);
				if(chckbxAutoscroll.isSelected()) {
					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
			}
		}
	}
	
	private SerialPort getSelectedPort() {
		if(initialized) {
			String cbxTxt=comboBox.getSelectedItem().toString();
			return SerialHandler.getInstance().getPortByName(cbxTxt);
		}
		return null;
	}
	
	public void setText(String t) {
		textArea.setText(t);
	}
	
	public void clear() {
		textArea.setText("");
	}
	
	public void show() {
		Dimension d=SystemParameters.getScreenSize();
		int border=10;
		Rectangle pos=new Rectangle(d.width/2, border, d.width/2-border, d.height-border*2-30);
		if(frame==null || frame.isActive()) {
			frame=new PanelFrame(this);
			frame.setTitle("Serial Monitor");
			frame.setVisible(true);
			frame.setBounds(pos);
			frame.setOnWindowClosing((event)->{frame=null;return null;});
		}else {
			frame.setState(Frame.NORMAL);
			frame.setBounds(pos);
			frame.setAlwaysOnTop(true);
		}
		frame.setAlwaysOnTop(chckbxAlwaysOnTop.isSelected());
	}
	
	public boolean isActive() {
		return frame!=null;
	}
	
	private class BtnClearOutputActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			clear();
		}
	}
	
	private class BtnPauseplayActionListener implements ActionListener {
		private boolean paused=false;
		
		public void actionPerformed(ActionEvent e) {
			if(paused) {
				onPlay();
				paused=false;
			}else {
				Object[] options= new Object[]{"Ja", "Nein"};
				int answer=JOptionPane.showOptionDialog(null, "Wollen Sie wirklich die Serielle Verbindung stoppen?\n"
						+ "Verbindung startet danach neu!", "Warnung", 
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if(answer==JOptionPane.YES_OPTION) {
					onStop();
					paused=true;
				}
			}
			
		}
	}
	
	private class SendActionListener extends KeyAdapter implements ActionListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER) {
				btnSend.doClick(500);
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if(!tfSend.getText().isEmpty()) {
				SerialPort port=getSelectedPort();
				if(port==null)
					SerialHandler.getInstance().sendMessageToAll(tfSend.getText()+"\n");
				else
					SerialHandler.getInstance().sendMessage(port,tfSend.getText()+"\n");

			}
		}
	}
	private class TfSendFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			tfSend.selectAll();
		}
	}
	
	private class ChckbxAlwaysOnTopChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.getWindowAncestor(instance).setAlwaysOnTop(chckbxAlwaysOnTop.isSelected());
		}
	}
	
	public void onStop() {}
	
	public void onPlay() {}


	@Override
	public void onConnected(SerialPort connected) {
		comOption.addElement(connected.getDescriptivePortName());
	}

	@Override
	public void onDisconnected(SerialPort disconnected) {
		comOption.removeElement(disconnected.getDescriptivePortName());		
	}
}
