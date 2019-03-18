package main.menu;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import arduino.MnVerbindenMitMouseListener;
import external.JScrollMenu;
import functions.streamer.VideoStreamer;
import main.MainFrame;
import javax.swing.JButton;

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
	private JScrollMenu scrlmnCamera;
	private JScrollMenu scrlmnMateditfunctions;
	private JScrollMenu scrlmnPanels;
	private JMenu mnArduino;
	private JMenu mnSerial;
	private JMenu mnVerbindenMit;
	
	public MainMenuBar() {
		initMenuBar(mnBar);
		setJMenuBar(mnBar);
	}
	
	public void initMenuBar(JMenuBar mnBar) {
		mnBar=new JMenuBar();

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
		
		scrlmnCamera = new JScrollMenu();
		scrlmnCamera.setText("Camera");
		mnView.add(scrlmnCamera);
		
		initCameras();
		
		scrlmnMateditfunctions = new JScrollMenu();
		scrlmnMateditfunctions.setText("MatEditFunctions");
		mnView.add(scrlmnMateditfunctions);
		
		scrlmnPanels = new JScrollMenu();
		scrlmnPanels.setText("Panels");
		mnView.add(scrlmnPanels);
		
		mntmNewFrame = new JMenuItem("New Frame");
		mnView.add(mntmNewFrame);
		
		
		mnArduino = new JMenu("Arduino");
		mnBar.add(mnArduino);
		
		mnSerial = new JMenu("Serial");
		mnArduino.add(mnSerial);
		
		mnVerbindenMit = new JMenu("Verbinden mit...");
		mnVerbindenMit.addMouseListener(new MnVerbindenMitMouseListener(mnVerbindenMit));
		mnSerial.add(mnVerbindenMit);
		
		this.mnBar=mnBar;
	}
	

	public JMenuBar getJMenuBar() {
		return mnBar;
	}
	
	private void initCameras() {
		try {
			for(int i:VideoStreamer.getAvailableCameras()) {
				CameraMenuItem mnItem=new CameraMenuItem(i);
				scrlmnCamera.add(mnItem);
			}
			
			for(Object o:MainFrame.getKnownCameraResources()) {
				CameraMenuItem mnItem=new CameraMenuItem(o);
				scrlmnCamera.add(mnItem);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
