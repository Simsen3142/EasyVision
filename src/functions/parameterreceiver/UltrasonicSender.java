package functions.parameterreceiver;

import java.awt.Image;
import java.util.Map;
import java.util.function.Function;

import communication.TwoWayConnection;
import communication.serial.SerialHandler;
import functions.RepresentationIcon;
import functions.Startable;
import main.ParameterReceiver;
import parameters.DoubleParameter;
import parameters.IntegerParameter;
import parameters.ParameterObject;
import parameters.ParameterizedObject;
import parameters.SerialPortParameter;
import parameters.group.ParameterGroup;

public class UltrasonicSender extends ParameterizedObject implements ParameterReceiver, RepresentationIcon, Startable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 67955021294288334L;
	private int id=System.identityHashCode(this);
	private transient TwoWayConnection<?> con;
	private transient Function<String, Void> onReceive;
	
	double lastd=0;
	private transient Thread streamThread;


	@Override
	public void recalculateId() {
		this.id*=Math.random();
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public UltrasonicSender(Boolean empty) {
		
	}
	
	public UltrasonicSender() {
		super(
			new SerialPortParameter("port"),
			new IntegerParameter("delay", 100,0,10000),
			new DoubleParameter("vc", 343.2,0.1,10000),
			new ParameterGroup("output",
				new IntegerParameter("timeDiff", 0,false),
				new DoubleParameter("distance", 0,false)
			)
		);
	}

	@Override
	public void onParameterReceived(Map<String, ParameterObject> parameters,ParameterizedObject sender) {
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
		if (!(obj instanceof UltrasonicSender))
			return false;
		UltrasonicSender other = (UltrasonicSender) obj;
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
	
	private void initStreamThread() {
		streamThread=new Thread(()->{
			while(!streamThread.isInterrupted()) {
				try {
					Thread.sleep(getIntVal("delay"));
					if(con!=null) {
						con.sendText("req");
					}else {
						break;
					}
					sendParameters();
				} catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		if(onReceive==null) {
			onReceive=(text)->{
				try {
					int timeDiff=Integer.parseInt(text);
					if(timeDiff>=0) {
						((IntegerParameter)getParameter("output_timeDiff")).setValue(timeDiff);
						((DoubleParameter)getParameter("output_distance")).setValue(getDoubleVal("vc")*(double)timeDiff/1000.0);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			};
		}
	}
	
	@Override
	public void start() {
		con=((SerialPortParameter)getParameter("port")).getSelectedCommunication();
		if(streamThread==null || !streamThread.isAlive() || streamThread.isInterrupted()) {
			initStreamThread();
			streamThread.start();
		}
		if(con!=null) {
			con.addOnReceive(onReceive);
		}
	}
	@Override
	public void stop() {
		if(streamThread!=null)
			streamThread.interrupt();
		super.stop();
		if(con!=null) {
			con.removeOnReceive(onReceive);
		}
	}
	
	@Override
	public boolean isStarted() {
		return streamThread!=null && streamThread.isAlive();
	}
}
