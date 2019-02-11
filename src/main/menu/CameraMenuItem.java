package main.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.MainFrame;
import main.VideoStreamer;
import view.MatReceiverPanel;
import view.MatPanel;
import view.PanelFrame;

public class CameraMenuItem extends MenuItemItemClickable{
	
	private static CameraMenuItemActionListener cmnitmal=new CameraMenuItemActionListener();


	public CameraMenuItem(Object object) {
		super(object, cmnitmal);
	}

	@Override
	public String getShownName() {
		return "camera<"+getObject()+">";
	}

	private static class CameraMenuItemActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			MenuItemItemClickable mnitm=(MenuItemItemClickable)arg0.getSource();
			VideoStreamer vstreamer=new VideoStreamer(mnitm.getObject());
			vstreamer=vstreamer.start();
			MatReceiverPanel pnl=new MatReceiverPanel(vstreamer);
			pnl.setShowFps(true);
			new PanelFrame(pnl).setVisible(true);
			
		}
	} 
}
