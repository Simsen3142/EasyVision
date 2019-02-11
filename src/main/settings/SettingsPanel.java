package main.settings;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.Dimension;

import javax.swing.JButton;

public class SettingsPanel extends JPanel {
	private JLabel lblSettings;
	private JButton btnCamera;
	private JButton btnMateditfunctions;
	private JButton btnPanels;
	private JButton btnFiles;
	private JButton btnNewFrame;

	/**
	 * Create the panel.
	 */
	public SettingsPanel() {
		setLayout(new MigLayout("", "[][grow][]", "[][][][][][]"));
		
		lblSettings = new JLabel("Settings");
		add(lblSettings, "cell 1 0");
		
		btnCamera = new JButton("Camera");
		add(btnCamera, "cell 1 1,growx");
		
		btnMateditfunctions = new JButton("MatEditFunctions");
		add(btnMateditfunctions, "cell 1 2,growx");
		
		btnPanels = new JButton("Panels");
		add(btnPanels, "cell 1 3,growx");
		
		btnFiles = new JButton("Files");
		add(btnFiles, "cell 1 4,growx");
		
		btnNewFrame = new JButton("New Frame");
		add(btnNewFrame, "cell 1 5,growx");

//		setPreferredSize(new Dimension(0, 0));
	}

}
