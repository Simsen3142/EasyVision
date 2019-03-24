package components;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import general.PictureEdit;

public class PictureButton extends JButton {
	
	private PictureButton() {
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
	}
	
	public PictureButton(ImageIcon icMain, ImageIcon icRollover, ImageIcon icPressed, ImageIcon icDisabled) {
		this();
		initImages(icMain,icRollover,icPressed,icDisabled);
	}
	
	public PictureButton(ImageIcon ic, Color cMain, Color cRollover, Color cPressed, Color cDisabled) {
		this();
		
		ImageIcon icMain=PictureEdit.changeNotTransparentToColor(ic, cMain);
		ImageIcon icRollover=PictureEdit.changeNotTransparentToColor(ic, cRollover);
		ImageIcon icPressed=PictureEdit.changeNotTransparentToColor(ic, cPressed);
		ImageIcon icDisabled=PictureEdit.changeNotTransparentToColor(ic, cDisabled);
		
		initImages(icMain,icRollover,icPressed,icDisabled);
	}
	
	private void initImages(ImageIcon icMain, ImageIcon icRollover, ImageIcon icPressed, ImageIcon icDisabled) {
		this.setIcon(icMain);
		this.setRolloverIcon(icRollover);
		this.setPressedIcon(icPressed);
		this.setDisabledIcon(icDisabled);
	}
	
	
}
