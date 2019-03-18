package arduino.view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import general.SystemParameters;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import view.PanelFrame;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class SerialMonitor extends JPanel {
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton btnClearOutput;
	private JCheckBox chckbxAutoscroll;
	private PanelFrame frame;
	private JButton btnStopplay;
	protected boolean pauseImplemented=false;

	/**
	 * Create the panel.
	 */
	public SerialMonitor() {
		initialize();
	}
	
	
	protected void initialize() {
		setLayout(new MigLayout("", "[450px,grow][50%:n:50%,grow]", "[300px,grow][]"));
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0 2 1,grow");
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		btnClearOutput = new JButton("Clear Output");
		btnClearOutput.addActionListener(new BtnClearOutputActionListener());
		
		chckbxAutoscroll = new JCheckBox("Autoscroll");
		chckbxAutoscroll.setSelected(true);
		add(chckbxAutoscroll, "flowx,cell 0 1");
		add(btnClearOutput, "cell 1 1,alignx right");
		
		if(pauseImplemented) {
			btnStopplay = new JButton("StopPlay");
			btnStopplay.setToolTipText("Stop/Play");
			btnStopplay.addActionListener(new BtnPauseplayActionListener());
			add(btnStopplay, "cell 0 1");
		}
	}

	public void append(String t) {
		textArea.append(t);
		if(chckbxAutoscroll.isSelected()) {
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
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
			frame.setAlwaysOnTop(false);
		}
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
				int answer=JOptionPane.showOptionDialog(null, "Wollen Sie wirklich den Arduino Stoppen?\n"
						+ "Das Programm startet danach neu!", "Warnung", 
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if(answer==JOptionPane.YES_OPTION) {
					onStop();
					paused=true;
				}
			}
			
		}
	}
	
	public void onStop() {}
	
	public void onPlay() {}
}
