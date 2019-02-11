package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import cvfunctions.MatEditFunction;

public class MatPanel extends JPanel {

	protected transient BufferedImage image;
	protected transient Mat mat;
	protected boolean showFps=false;
	protected long fps;
	
	/**
	 * @return the mat
	 */
	public Mat getMat() {
		return mat;
	}
	/**
	 * @param mat the mat to set
	 */
	public void setMat(Mat mat) {
		this.mat = mat;
		image=matToBufferedImage(mat);
	}
	
	public void updateMat(Mat mat) {
		setMat(mat);
		setVisible(false);
		setVisible(true);
	}
	
	public void setShowFps(boolean show) {		
		this.showFps=show;
		setVisible(false);
		setVisible(true);
	}
	
	/**
	 * Create the panel.
	 */
	public MatPanel() {
		super();
		this.addMouseListener(ThisPopupMouseListener.getInstance());
	}
	
	@Override
	public void paint(Graphics g) {
		try {
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
			if(showFps) {
				g.setFont(new Font("Calibri", Font.BOLD, 18));
				g.setColor(Color.GREEN);
				g.drawString(fps+" FPS", 20, 20);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage grayscale(BufferedImage img) {
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(img.getRGB(j, i));

				int red = (int) (c.getRed() * 0.299);
				int green = (int) (c.getGreen() * 0.587);
				int blue = (int) (c.getBlue() * 0.114);

				Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);

				img.setRGB(j, i, newColor.getRGB());
			}
		}

		return img;
	}

	public static BufferedImage mirror(BufferedImage img) {
		BufferedImage imgsafe = deepCopy(img);

		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(imgsafe.getRGB(j, i));

				int y = img.getHeight() - i - 1;
				int x = img.getWidth() - j - 1;

				img.setRGB(x, i, c.getRGB());
			}
		}

		return img;
	}

	public static BufferedImage gainContrast(BufferedImage img) {
		RescaleOp rescaleOp = new RescaleOp(2f, 0, null);
		rescaleOp.filter(img, img);
		return img;
	}

	public static Mat invertColors(Mat frame) {
		Core.bitwise_not(frame, frame);
		return frame;
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage matToBufferedImage(Mat frame) {
		// Mat() to BufferedImage
		int type = 0;
		if (frame.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (frame.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		frame.get(0, 0, data);

		return image;
	}
	
	private static class ThisPopupMouseListener extends MouseAdapter {
		private JPopupMenu popupMenu;
		private JCheckBox chckbxShowFps;
		private static ThisPopupMouseListener popupMouseListener;

		private ThisPopupMouseListener() {
		}
		
		private void initPopupMenu() {
			popupMenu=new JPopupMenu();
			chckbxShowFps=new JCheckBox("Show FPS");
			popupMenu.add(chckbxShowFps);
		}
		
		public static ThisPopupMouseListener getInstance() {
			if(popupMouseListener==null) {
				popupMouseListener=new ThisPopupMouseListener();
			}
			
			return popupMouseListener;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showMenu(e);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			mousePressed(e);
		}

		private void showMenu(MouseEvent e) {
			initPopupMenu();
			MatPanel matPanel=(MatPanel) e.getSource();
			chckbxShowFps.setSelected(matPanel.showFps);
			chckbxShowFps.addItemListener(new CheckboxShowFpsItemListener(matPanel));
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
		private class CheckboxShowFpsItemListener implements ItemListener {
			MatPanel matPanel;
			public CheckboxShowFpsItemListener(MatPanel matPanel) {
				this.matPanel=matPanel;
			}
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				matPanel.setShowFps(chckbxShowFps.isSelected());
			}
		}
	}
}
