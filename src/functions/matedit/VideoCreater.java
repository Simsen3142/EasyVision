package functions.matedit;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.InvocationTargetException;


import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import database.ImageHandler;
import parameters.BooleanParameter;
import parameters.FileParameter;
import parameters.IntegerParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;

public class VideoCreater extends MatEditFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1146389294639251120L;
	private static volatile Image img;
	private transient VideoWriter writer;
	private transient Size size;
	
	public VideoCreater() {
		super(
			new ParameterGroup("settings",
				new FileParameter("file", null),
				new ParameterGroup("size",
					new IntegerParameter("width", 200, 150, 1080),
					new IntegerParameter("height", 180, 150, 720)
				),
				new IntegerParameter("fps", 60,1,200),
				new BooleanParameter("color", true),
				new StringParameter("fourcc", "mjpg",4,4)
			)
		);
	}
	
	public VideoCreater(Boolean empty) {}

	
	public boolean initVideoWriter(){
		File file=getFileVal("settings_file");
		if(file==null)
			return false;
		int width=getIntVal("settings_size_width");
		int height=getIntVal("settings_size_height");
		size=new Size(width, height);
		
		int fps=getIntVal("settings_fps");
		boolean color=getBoolVal("settings_color");
		char[] cs=getStringVal("settings_fourcc").toCharArray();
		
		writer=new VideoWriter(file.getAbsolutePath(), VideoWriter.fourcc(cs[0], cs[1], cs[2], cs[3]), fps, size, color);
		return true;
	}
	
	@Override
	protected Mat apply(Mat matIn) {
		Mat matOut=new Mat();
		if(writer!=null || initVideoWriter()) {
			Imgproc.resize(matIn, matOut, size);
			writer.write(matOut);
		}
		return matOut;
	}
	
	@Override
	public void stop() {
		if(writer!=null)
			writer.release();
	}
	
	@Override
	public Image getRepresentationImage() {
		if(img==null)
			img=ImageHandler.getImage("res/icons/videocreater.png");;
		return img;
	}
}
