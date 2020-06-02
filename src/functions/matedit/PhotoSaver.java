package functions.matedit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import database.ImageHandler;
import functions.Startable;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.components.ParameterFileChoosePanel;
import view.MatPanel;

public class PhotoSaver extends MatEditFunction implements Startable {
	
	private transient boolean saving=false;
	private transient Mat m;
	private static volatile Image img;
	private static SimpleDateFormat sdfDate;

	
	public PhotoSaver(Boolean empty) {
	}
	
	public PhotoSaver() {
		super(new FileParameter("file", null),
				new BooleanParameter("feedback", true),
				new BooleanParameter("addtimestamp", false));
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		if(!saving) {
			m=matIn.clone();
		}
		return m;
	}

	@Override
	public void start() {
		saving=true;
		if(sdfDate==null) {
			sdfDate=new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss,SSS");
		}
		try {
			File f=getFileVal("file");
			if(f==null) {
				JFileChooser fc=ParameterFileChoosePanel.getFileChooser();
				int i=fc.showSaveDialog(null);
				if(i==JFileChooser.APPROVE_OPTION) {
					((FileParameter)this.getParameters().get("file")).setValue(fc.getSelectedFile());
				}
			}
			
			if(f!=null && m!=null) {
				String s=f.getAbsolutePath();
				
				boolean b=getBoolVal("addtimestamp");
				if(!s.contains(".")) {
					s+=".jpg";
				}
				
				if(b) {
					
					String before=s.substring(0, s.indexOf("."));
					String after=s.substring(s.indexOf("."), s.length());
					String tstmp=sdfDate.format(System.currentTimeMillis());
					
					s=before+" - "+tstmp+after;
				}
				
				if(new File(s).exists() && getBoolVal("feedback")) {
					Object options[]= {"yes","no"};
					int option=JOptionPane.showOptionDialog(null, "Do you want to override the file?", "File already exists", JOptionPane.YES_NO_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, null, options, JOptionPane.NO_OPTION);
					
					if(option==JOptionPane.YES_OPTION) {
						Imgcodecs.imwrite(s, m);
						JOptionPane.showMessageDialog(null, "Photo saved", "Completed", JOptionPane.INFORMATION_MESSAGE);
					}
				}else {
					Imgcodecs.imwrite(s, m);
					if(getBoolVal("feedback")) {
						JOptionPane.showMessageDialog(null, "Photo saved", "Completed", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		saving=false;
	}

	@Override
	public boolean isStarted() {
		return saving;
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/fotospeichern.png");
		return img;
	}

}
