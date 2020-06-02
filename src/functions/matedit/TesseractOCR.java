package functions.matedit;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import enums.OCRTesseractChoices;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageHelper;
import parameters.EnumParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;
import view.MatPanel;

public class TesseractOCR extends MatEditFunction {

	private static final long serialVersionUID = 6949328861977145755L;
	private transient Tesseract tesseract;
	private transient OCRTesseractChoices lastChoice=OCRTesseractChoices.STANDARD;

	public TesseractOCR(Boolean empty) {
	}
	
	public TesseractOCR() {
		super(
			new EnumParameter("detect", OCRTesseractChoices.STANDARD),
			new ParameterGroup("output",
				new StringParameter("text", "",false)
			)
		);
		
		tesseract=new Tesseract();
		tesseract.setDatapath("Tess4j/tessdata");
	}
	
	@Override
	public Image getRepresentationImage() {
		return ImageHandler.getImage("res/icons/farberkennung.png");
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		if(tesseract==null) {
			tesseract=new Tesseract();
			tesseract.setDatapath("Tess4j/tessdata");
		}
		
		if(getEnumVal("detect")!=lastChoice) {
			OCRTesseractChoices val=(OCRTesseractChoices) getEnumVal("detect");
			lastChoice=val;
			switch (val) {
			case STANDARD:
				tesseract.setConfigs(Arrays.asList());
				break;
			case DIGITS_ONLY:
				tesseract.setConfigs(Arrays.asList("digits"));
				break;
			}
		}
		
		Mat matOut=matIn.clone();
		if(matOut.channels()<2) {
			Imgproc.cvtColor(matOut, matOut, Imgproc.COLOR_GRAY2BGR);
		}
		
		BufferedImage bi=MatPanel.matToBufferedImage(matIn);
		try {
			String text=tesseract.doOCR(bi);
			if(text!=null) {
				((StringParameter)getParameter("output_text")).setValue(text);
			}
			int level = TessPageIteratorLevel.RIL_SYMBOL;
		    List<Word> words= tesseract.getWords(bi, level);
		    for (int i = 0; i < words.size(); i++) {
		    	Word w=words.get(i);
		        Rectangle rect = w.getBoundingBox();
		        Point ptext=new Point(rect.x+rect.width/2,rect.y+rect.height+9);
		        Imgproc.rectangle(matOut, new Point(rect.x,rect.y),  new Point(rect.x+rect.width,rect.y+rect.height),
		        		new Scalar(50, 50, 50));
		        Imgproc.putText(matOut,w.getText(), 
		        		ptext, Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255));		        
		    }
		} catch (TesseractException e) {
			e.printStackTrace();
		}


		return matOut;
	}
}
