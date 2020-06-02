package main.menu;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import communication.bluetooth.BluetoothHandler;
import communication.serial.SerialHandler;
import communication.view.MnConnectToMouseListener;
import external.JScrollMenu;
import javax.bluetooth.RemoteDevice;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class MainMenuBar extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -375968247365462419L;
	private JMenuBar mnBar;
	private JMenu mnView;
	private JMenu mnSettings;
	private JMenuItem mntmCamera;
	private JMenuItem mntmMateditfunction;
	private JMenuItem mntmPanels;
	private JMenuItem mntmFiles;
	private JMenuItem mntmNewFrame;
	private JScrollMenu scrlmnMateditfunctions;
	private JScrollMenu scrlmnPanels;
	private JMenu mnSerial;
	private JMenu mnSerialConnect;
	private JMenu mnVerbindenMit;
	private JMenuItem mntmSerialMonitor;
	private JMenu mnBluetooth;
	private JMenu mnVerbindenMit2;
	private JMenu mnFile;
	private JMenuItem mntmNew;
	private JMenuItem mntmOpen;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmOpenLast;
	
	public MainMenuBar() {
		initMenuBar(mnBar);
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public MainMenuBar(boolean nothing) {
		initMenuBar(mnBar);
		setJMenuBar(mnBar);
	}
	
	public void initMenuBar(JMenuBar mnBar) {
		mnBar=new JMenuBar();
		
		mnFile = new JMenu("File");
		mnBar.add(mnFile);
		
		mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnFile.add(mntmNew);
		
		mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpen);
		
		mntmOpenLast = new JMenuItem("Open last");
		mntmOpenLast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFile.add(mntmOpenLast);
		
		mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFile.add(mntmSaveAs);
		
		mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSave);

		mnSettings = new JMenu("Settings");
		mnBar.add(mnSettings);
		
		mntmCamera = new JMenuItem("Camera");
		mnSettings.add(mntmCamera);
		
		mntmMateditfunction = new JMenuItem("MatEditFunction");
		mnSettings.add(mntmMateditfunction);
		
		mntmPanels = new JMenuItem("Panels");
		mnSettings.add(mntmPanels);
		
		mntmFiles = new JMenuItem("Files");
		mnSettings.add(mntmFiles);
		
		mnView = new JMenu("View");
		mnBar.add(mnView);
		
		scrlmnMateditfunctions = new JScrollMenu();
		scrlmnMateditfunctions.setText("MatEditFunctions");
		mnView.add(scrlmnMateditfunctions);
		
		scrlmnPanels = new JScrollMenu();
		scrlmnPanels.setText("Panels");
		mnView.add(scrlmnPanels);
		
		mntmNewFrame = new JMenuItem("New Frame");
		mnView.add(mntmNewFrame);
		
		
		mnSerial = new JMenu("Serial");
		mnBar.add(mnSerial);
		
		mnSerialConnect = new JMenu("Verbindung");
		mnSerial.add(mnSerialConnect);
		
		mnVerbindenMit = new JMenu("Verbinden mit...");
		mnVerbindenMit.addMouseListener(new MnConnectToMouseListener<SerialPort>(mnVerbindenMit,SerialHandler.getInstance()));
		mnSerialConnect.add(mnVerbindenMit);
		
		mntmSerialMonitor = new JMenuItem("Serial monitor");
		mntmSerialMonitor.addActionListener(new MntmSerialMonitorActionListener());
		mnSerial.add(mntmSerialMonitor);
		
		mnBluetooth = new JMenu("Bluetooth");
		mnBar.add(mnBluetooth);
		
		mnVerbindenMit2 = new JMenu("Verbinden mit...");
		mnVerbindenMit2.addMouseListener(new MnConnectToMouseListener<RemoteDevice>(mnVerbindenMit2,BluetoothHandler.getInstance()));
		mnBluetooth.add(mnVerbindenMit2);
		
		this.mnBar=mnBar;
	}
	

	public JMenuBar getJMenuBar() {
		return mnBar;
	}
	
	private class MntmSerialMonitorActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			SerialHandler.getInstance().getSerialLogger().getNewSerialMonitor().show();
		}
	}
	
	public void addSaveActionListener(ActionListener al) {
		mntmSave.addActionListener(al);
	}
	
	public void addSaveAsActionListener(ActionListener al) {
		mntmSaveAs.addActionListener(al);
	}
	
	public void addOpenActionListener(ActionListener al) {
		mntmOpen.addActionListener(al);
	}
	
	public void addOpenLastActionListener(ActionListener al) {
		mntmOpenLast.addActionListener(al);
	}
	
	public void addNewActionListener(ActionListener al) {
		mntmNew.addActionListener(al);
	}
}
