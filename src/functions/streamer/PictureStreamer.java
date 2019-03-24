package functions.streamer;

import java.awt.Image;
import java.io.File;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import database.ImageHandler;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.IntegerParameter;
import parameters.group.ParameterGroup;

public class PictureStreamer extends MatStreamer {
	
	private transient Mat mat;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4240705333246586616L;
	private static volatile Image img;
	
	public PictureStreamer(Boolean empty) {}

	public PictureStreamer() {
		this(new File("res/EVLogo.jpg"));
	}
	public PictureStreamer(File file) {
		super(file.getAbsolutePath());
		this.addParameters(new FileParameter("file", file),
			new ParameterGroup("endless", 				
				new BooleanParameter("endless", false),
				new IntegerParameter("delayval", 1000,0,100000)
			)
		);
	}
	

	
	@Override
	protected void initStreamThread() {
		this.setStreamThread(new Thread(new Runnable() {
			@Override
			public void run() {
				mat = new Mat();
				
				while (!getStreamThread().isInterrupted()) {
					try {
						File f=getFileVal("file");
						if(!new File((String)getResource()).getAbsolutePath().equals(f.getAbsolutePath())) {
							setResource(f.getAbsolutePath());
							stop();
							start();
							break;
						}
						if ((mat=Imgcodecs.imread(f.getAbsolutePath()))!=null) {
							if(getBoolVal("size_change")) {
								int width=getIntVal("size_width");
								int height=getIntVal("size_height");
								Imgproc.resize(mat, mat, new Size(width,height));
							}
							try {
								Thread.sleep(getIntVal("endless_delayval"));
								sendMat(mat);
								sendParameters();
							} catch(InterruptedException e) {
								break;
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							System.gc();
						}else {
							if(getBoolVal("endless_endless")) {
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
		}));
	}

	@Override
	public Image getRepresentationImage() {
		if (img == null)
			img = ImageHandler.getImage("res/icons/fotostreamer.png");
		return img;
	}
}