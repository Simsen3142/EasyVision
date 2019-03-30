package functions.streamer;

import java.awt.Image;
import java.io.File;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.IntegerParameter;

public class FileVideoStreamer extends VideoStreamer {
	
	private transient Mat mat;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4240705333246586616L;
	private static volatile Image img;

	public FileVideoStreamer(File file) {
		super(file.getAbsolutePath());
		this.addParameters(new FileParameter("file", file),
				new BooleanParameter("endless", true));
	}
	
	public FileVideoStreamer() {
		this(new File("res/SampleVideo_1280x720_1mb.mp4"));
	}
	
	public FileVideoStreamer(Boolean empty) {}
	
	@Override
	protected void initStreamThread() {
		initCamera();

		this.setStreamThread(new Thread(new Runnable() {
			@Override
			public void run() {
				mat = new Mat();
				
				if (!getCamera().isOpened()) {
					System.out.println("Error");
				} else {
					while (!getStreamThread().isInterrupted()) {
						try {
							File f=getFileVal("file");
							if(!new File((String)getResource()).getAbsolutePath().equals(f.getAbsolutePath())) {
								setResource(f.getAbsolutePath());
								System.out.println(f.getAbsolutePath());
								System.out.println(getResource());
								stop();
								start();
								break;
							}
							
							if (getCamera().read(mat)) {
								IntegerParameter widthParam=(IntegerParameter) getParameter("size_width");
								IntegerParameter heightParam=(IntegerParameter) getParameter("size_height");
								
								if(getBoolVal("size_change")) {
									int width=widthParam.getValue();
									int height=heightParam.getValue();
									Imgproc.resize(mat, mat, new Size(width,height));
								}else {
									widthParam.setValue(mat.cols());
									heightParam.setValue(mat.rows());
								}
								
								try {
									sendMat(mat);
									sendParameters();
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								System.gc();
							}else {
								if(getBoolVal("endless")) {
									stop();
									start();
									break;
								}else {
									System.out.println("Camera not available");
								}
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}));
	}
	
	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/filestreamer.png");
		return img;
	}
}