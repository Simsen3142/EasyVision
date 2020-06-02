package functions.parameterreceiver;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;
import communication.serial.TwoWaySerialCommLidar;
import functions.RepresentationIcon;
import functions.Startable;
import main.ParameterReceiver;
import parameters.CollectionParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.SerialPortParameter;
import parameters.StringParameter;
import parameters.group.ParameterGroup;
import sensors.lidar.LidarHealthResponse;
import sensors.lidar.LidarInfoResponse;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessage;

public class LidarInfoHandler extends SuperLidarHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 67955021294288334L;
	
	public LidarInfoHandler(Boolean empty) {
	}
	
	public LidarInfoHandler() {
		super(
			new StringParameter("modelID", "",false),
			new StringParameter("firmwarev", "",false),
			new StringParameter("hardwarev", "",false),
			new StringParameter("serialnr", "",false)
		);
	}

	@Override
	public Image getRepresentationImage() {
		return null;
	}
	
	@Override
	public void getRepresentationImage(Function<Image, Void> onReceive) {
		new Thread(()-> {
			Image img=getRepresentationImage();
			onReceive.apply(img);
		}).start();
	}
	
	protected Thread initStreamThread() {
		return new Thread(()->{
			while (!getStreamThread().isInterrupted()) {
				try {
					LidarInfoResponse resp=getDeviceInfo();
					((StringParameter)getParameter("output_firmwarev")).setValue(resp.getFirmwareVersion());
					((StringParameter)getParameter("output_hardwarev")).setValue(resp.getHardwareVersion());
					((StringParameter)getParameter("output_modelID")).setValue(resp.getModelID());
					((StringParameter)getParameter("output_serialnr")).setValue(resp.getSerialNumber());
					sendParameters();
					Thread.sleep(100);
					break;
				}catch (InterruptedException e) {
					break;
				}catch (Exception e) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		});
	}
}
