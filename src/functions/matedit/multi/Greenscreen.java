package functions.matedit.multi;

import java.awt.Image;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;

import database.ImageHandler;
import functions.matedit.ChangeResolution;

public class Greenscreen extends MultiMatEditFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8003205441002816111L;

	public Greenscreen() {
		super();
	}
	
	public Greenscreen(Boolean empty) {
	}
	
	@Override
	public int getNrFunctionInputs() {
		return 0;
	}
	
	@Override
	public int getNrMatInputs() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	protected Mat apply(Map<Integer, Mat> matsIn) {
		Mat matBasicImage=matsIn.get(0);
		
		Mat matOverlayImage;
		Mat colorToOverlay;
		Mat matOut;
		
		try {
			matOverlayImage=matsIn.get(1);
			colorToOverlay=matsIn.get(2);
			
			int rows=matBasicImage.rows();		
			int cols=matBasicImage.cols();	
			
			if(matOverlayImage.rows()!=rows || matOverlayImage.cols()!=cols) {
				matOverlayImage=ChangeResolution.apply(matOverlayImage, cols, rows);
			}
			
			if(colorToOverlay.rows()!=rows || colorToOverlay.cols()!=cols) {
				colorToOverlay=ChangeResolution.apply(colorToOverlay, cols, rows);
			}
			
			matOut=matBasicImage.clone();
			
			for(int col=0;col<cols;col++) {
				for(int row=0;row<rows;row++) {
					if(colorToOverlay.get(row, col)[0]==255) {
						double[] dataOverlay=matOverlayImage.get(row, col);
						if(dataOverlay==null) {
							return matBasicImage;
						}
						matOut.put(row, col, dataOverlay);
					}
				}
			}
		}catch (NullPointerException e) {
			return matBasicImage;
		}catch (Exception e) {
			e.printStackTrace();
			return matBasicImage;
		}
		
		return matOut;
	}
	
	@Override
	public void clearMatsIn(Map<Integer, Mat> matsIn) {
		matsIn.remove(0);
		matsIn.remove(2);
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/greenscreen.png");
	}

}
