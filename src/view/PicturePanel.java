package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class PicturePanel extends JPanel {
	private Image image;

	/**
	 * Create the panel.
	 */
	public PicturePanel(Image image) {
		this.image=image;
		setOpaque(false);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Dimension d=getDimension();
		Dimension size=getSize();
		g.drawImage(image, size.width/2-d.width/2, size.height/2-d.height/2, d.width,d.height , null);
	}
	
	private Dimension getDimension(){
		Dimension d=new Dimension(getSize());
		if(((double)image.getHeight(null)/(double)image.getWidth(null))>(double)d.getHeight()/(double)d.getWidth()){
			d.width=(int)(((double)d.height*(double)image.getWidth(null))/(double)image.getHeight(null));
		}else{
			d.height=(int)(((double)d.width*(double)image.getHeight(null))/(double)image.getWidth(null));
		}
		return d;
	}
}
