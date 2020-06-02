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
import sensors.lidar.LidarSamplerateResponse;

public class LidarSamplerateHandler extends SuperLidarHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 67955021294288334L;
	
	public LidarSamplerateHandler(Boolean empty) {
		
	}
	
	public LidarSamplerateHandler() {
		super(
			new IntegerParameter("tStandard", -1,false),
			new IntegerParameter("tExpress", -1,false)
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
					LidarSamplerateResponse resp=getSampleRate();
					System.out.println(resp);
					((IntegerParameter)getParameter("output_tStandard")).setValue(resp.getTStandard());
					((IntegerParameter)getParameter("output_tExpress")).setValue(resp.getTExpress());
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
