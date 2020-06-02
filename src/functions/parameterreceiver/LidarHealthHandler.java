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
import parameters.group.ParameterGroup;
import sensors.lidar.LidarHealthResponse;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessage;

public class LidarHealthHandler extends SuperLidarHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 67955021294288334L;
	
	public LidarHealthHandler(Boolean empty) {
		
	}
	
	public LidarHealthHandler() {
		super(
			new IntegerParameter("status", -1,false),
			new IntegerParameter("errorCode", -1,false)
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
					LidarHealthResponse resp=getDeviceHealth();
					((IntegerParameter)getParameter("output_status")).setValue(resp.getStatus());
					((IntegerParameter)getParameter("output_errorCode")).setValue(resp.getErrorCode());
					sendParameters();
					Thread.sleep(100);
					break;
				}catch (InterruptedException e) {
					break;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
