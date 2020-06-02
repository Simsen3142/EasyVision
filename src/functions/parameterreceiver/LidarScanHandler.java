package functions.parameterreceiver;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import communication.TwoWayConnection;
import communication.serial.SerialHandler;
import communication.serial.TwoWaySerialComm;
import communication.serial.TwoWaySerialCommLidar;
import functions.RepresentationIcon;
import functions.Startable;
import main.ParameterReceiver;
import parameters.CollectionParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.SerialPortParameter;
import parameters.group.ConnectionParameterGroup;
import parameters.group.ParameterGroup;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessage;
import sensors.lidar.LidarMessageSender;

public class LidarScanHandler extends SuperLidarHandler implements LidarListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 67955021294288334L;
	private int id=System.identityHashCode(this);
	private transient HashMap<Integer, LidarMessage> msgs;


	public LidarScanHandler(Boolean empty) {
	}
	
	public LidarScanHandler() {
		super(
			new CollectionParameter("scanvals", null)
		);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LidarScanHandler))
			return false;
		LidarScanHandler other = (LidarScanHandler) obj;
		if (id != other.id)
			return false;
		return true;
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
					sendParameters();
					Thread.sleep(100);
				}catch (InterruptedException e) {
					break;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void start() {
		super.start();
		if(getConnection()!=null && getConnection() instanceof LidarMessageSender) {
			startMotor();
			getConnection().startReader();
			((LidarMessageSender)getConnection()).addLidarListener(this);
			startScan();
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		if(getConnection()!=null && getConnection() instanceof LidarMessageSender) {
			((LidarMessageSender)getConnection()).removeLidarListener(this);
			stopLidar();
			stopMotor();
			getConnection().stopReader();
		}
	}
	
	@Override
	public void onMessageReceived(LidarMessage msg) {
		if(msgs==null) {
			this.msgs=new HashMap<Integer, LidarMessage>();
		}
		msgs.put((int)Math.round(msg.getDegree()), msg);
		((CollectionParameter)getParameter("output_scanvals")).setValue(new ArrayList<LidarMessage>(msgs.values()));
	}

	@Override
	public void onFullTurnDone(Map<Integer,LidarMessage> msgs) {
		this.msgs=(HashMap<Integer, LidarMessage>)msgs;
	}
}
