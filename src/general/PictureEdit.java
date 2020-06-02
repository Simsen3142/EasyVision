package general;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class PictureEdit {
	
	public static ImageIcon changeNotTransparentToColor(ImageIcon ic, Color newC){
		
		BufferedImage bi = new BufferedImage(
				ic.getIconWidth(),
				ic.getIconHeight(),
			    BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics g = bi.createGraphics();
			ic.paintIcon(null, g, 0,0);
			g.dispose();

	    for (int x = 0; x < bi.getWidth(); x++) {
	        for (int y = 0; y < bi.getHeight(); y++) {
	            if(bi.getRGB(x, y) != 0x00 ){
	            	bi.setRGB(x, y, newC.getRGB());
	            }
	        }
	    }
	    return new ImageIcon(bi);
	}
	
	public static ImageIcon changeDimension(ImageIcon ic, int width,int height){
		return new ImageIcon(ic.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH));
	}
	
	public static ImageIcon makeIconBrighter(ImageIcon ic,int value){
		BufferedImage bi = new BufferedImage(
				ic.getIconWidth(),
				ic.getIconHeight(),
			    BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics g = bi.createGraphics();
			ic.paintIcon(null, g, 0,0);
			g.dispose();

	    for (int x = 0; x < bi.getWidth(); x++) {
	        for (int y = 0; y < bi.getHeight(); y++) {
	            if(bi.getRGB(x, y) != 0x00 ){
	            	Color c=new Color(bi.getRGB(x, y));
	            	int[] rgba=new int[]{
	            		c.getRed()+value,c.getGreen()+value,c.getBlue()+value,c.getAlpha()
	            	};
	            	for(int i=0;i<4;i++){
	            		if(rgba[i]<0)
	            			rgba[i]=0;
	            		else if(rgba[i]>255)
	            			rgba[i]=255;
	            	}
	            	c=new Color(rgba[0],rgba[1],rgba[2],rgba[3]);
	            	bi.setRGB(x, y, c.getRGB());
	            }
	        }
	    }
	    return new ImageIcon(bi);
	}
	
	public static ImageIcon changeOpacity(ImageIcon ic, float opacity){
		BufferedImage bi = new BufferedImage(
				ic.getIconWidth(),
				ic.getIconHeight(),
			    BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = bi.createGraphics();
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			ic.paintIcon(null, g, 0,0);
			g.dispose();
			
			for (int x = 0; x < bi.getWidth(); x++) {
		        for (int y = 0; y < bi.getHeight(); y++) {
		            if(bi.getRGB(x, y) != 0x00 ){
		            	bi.setRGB(x, y, bi.getRGB(x,y));
		            }
		        }
		    }

	    return new ImageIcon(bi);
	}
	
	public static Color getAverageColor(ImageIcon ic) {
		BufferedImage bi = new BufferedImage(
				ic.getIconWidth(),
				ic.getIconHeight(),
			    BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			ic.paintIcon(null, g, 0,0);
			g.dispose();
				
	    long sumr = 0, sumg = 0, sumb = 0;
	    for (int x = 0; x < bi.getWidth(); x++) {
	        for (int y = 0; y < bi.getHeight(); y++) {
	            Color pixel = new Color(bi.getRGB(x, y));
	            sumr += pixel.getRed();
	            sumg += pixel.getGreen();
	            sumb += pixel.getBlue();
	        }
	    }
	    int num = bi.getWidth() * bi.getHeight();
	    int rgb[]=new int[]{(int) (sumr / num), (int) (sumg / num),(int) ( sumb / num)};

	    return new Color(rgb[0],rgb[1],rgb[2]);
	}
	
	public static ImageIcon makePixelTransparent(ImageIcon ic, Color c){
		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics g = bi.createGraphics();
		ic.paintIcon(null, g, 0, 0);
		g.dispose();

		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) {
				if (new Color(bi.getRGB(x, y)).equals(c)) {
					bi.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
				}
			}
		}
		return new ImageIcon(bi);
	}
	
	public static ImageIcon makeBrightPixelTransparent(ImageIcon ic, int brightness, boolean whichAreDarker){
		return makeBrightPixelTransparent(ic, brightness, whichAreDarker, 0);
	}
	
	public static ImageIcon makeBrightPixelTransparent(ImageIcon ic, int brightness, boolean whichAreDarker, int transparency){
		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics g = bi.createGraphics();
		ic.paintIcon(null, g, 0, 0);
		g.dispose();

		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) {
				Color c=new Color(bi.getRGB(x, y));
				if(whichAreDarker) {
					if (getColorBrightness(c)<=brightness) {
						int ci=transparency>0?(new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency).getRGB()):
							new Color(0,0,0, transparency).getRGB();
						bi.setRGB(x, y, ci);
					}
				}else {
					if (getColorBrightness(new Color(bi.getRGB(x, y)))>=brightness) {
						int ci=transparency>0?(new Color(c.getRed(),  c.getGreen(), c.getBlue(), transparency).getRGB()):
							new Color(0,0,0, transparency).getRGB();
						bi.setRGB(x, y, ci);					
					}
				}
			}
		}
		return new ImageIcon(bi);
	}
	
	public static ImageIcon decreaseTransparency(ImageIcon ic, float transparency){
		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics g = bi.createGraphics();
		ic.paintIcon(null, g, 0, 0);
		g.dispose();

		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) {
				Color c=new Color(bi.getRGB(x, y),true);
				
				int ci=(new Color(c.getRed(), c.getGreen(),c.getBlue(), (int)(c.getAlpha()*transparency)).getRGB());
				bi.setRGB(x, y, ci);					
			}
		}
		return new ImageIcon(bi);
	}
	
	public static int getColorBrightness(Color c){
		return (int) (((0.2126*c.getRed()+ 0.7152*c.getGreen()+0.0722*c.getBlue())/3)*255/c.getAlpha());
	}
	
	public static void safeImageToFile(ImageIcon ic, File f){
		Image img = ic.getImage();

		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics g = bi.createGraphics();
		ic.paintIcon(null, g, 0, 0);
		g.dispose();
		
		try {
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveImage(String imageUrl, File destinationFile) throws IOException {
	    URL url = new URL(imageUrl);
	    InputStream is = url.openStream();
	    OutputStream os = new FileOutputStream(destinationFile);

	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	        os.write(b, 0, length);
	    }

	    is.close();
	    os.close();
	}

}
