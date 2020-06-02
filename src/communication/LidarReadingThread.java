package communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.util.Hex;

import com.fazecast.jSerialComm.SerialPortIOException;

import gnu.io.SerialPort;
import sensors.lidar.LidarListener;
import sensors.lidar.LidarMessage;

public class LidarReadingThread extends ReadingThread {
	private List<LidarListener> listeners=new ArrayList<LidarListener>();
	private double lastAngle=0;
	private transient HashMap<Integer,LidarMessage> msgs;
	private volatile boolean isSending=false;
	private transient long lastTimeCollected=0;

	public LidarReadingThread(InputStream in) {
		super(in);
	}

	public boolean addLidarListener(LidarListener lstnr) {
		synchronized (listeners) {
			if(!listeners.contains(lstnr)) {
				return listeners.add(lstnr);
			}
		}
		return false;
	}
	
	public boolean removeLidarListener(Object lstnr) {
		synchronized (listeners) {
			return listeners.remove(lstnr);
		}
	}
	
	public void setListeners(List<LidarListener> listeners) {
		this.listeners = listeners;
	}

	public LidarReadingThread(SerialPort port) throws IOException {
		this(port.getInputStream());
	}

	public void run() {
		byte[] last=null;
		while (!this.isInterrupted()) {
			try {
				try {
					if (getIn().available() >= 10) {
						while(getIn().available()>1010) {
							getIn().skip(1000);
						}
							
						byte[] dataIn = new byte[getIn().available()];
						getIn().read(dataIn, 0, dataIn.length);
						byte[] data;
						if(last!=null && last.length>0) {
							data=new byte[dataIn.length+(last!=null?last.length:0)];
							System.arraycopy(last, 0, data, 0, last.length);
					        System.arraycopy(dataIn, 0, data, last.length, dataIn.length);
						}else {
							data=dataIn;
						}
				        
						int offset = 0;
						while (offset <= data.length - 5) {
							if (parseScan(data, offset)) {
								offset += 5;
							} else {
								offset++;
							}
						}
						last=new byte[data.length-offset];
						System.arraycopy(data, offset, last, 0, data.length-offset);
					}
				} catch (IOException e) {
					Thread.sleep(50);
					e.printStackTrace();
					continue;
				}
				
				if(System.currentTimeMillis()-lastTimeCollected>1000) {
					lastTimeCollected=System.currentTimeMillis();
					System.gc();
				}
			} catch (InterruptedException e) {
				this.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean parseScan(byte[] data, int offset) {
		byte b0 = data[offset];
		byte b1 = data[offset + 1];

		boolean start0 = (b0 & 0x01) != 0;
		boolean start1 = (b0 & 0x02) != 0;

		if (start0 == start1)
			return false;

		if ((b1 & 0x01) != 1) {
			return false;
		}

		double quality = (b0 & 0xFF) >> 2;
		double angle = (((b1 & 0xFF) >> 1) + ((data[offset + 2] & 0xFF) << 7)) / 64.0;
		double distance = ((data[offset + 3] & 0xFF) + ((data[offset + 4] & 0xFF) << 8)) / 4.0;

		if(angle<=360) {
			LidarMessage msg=new LidarMessage(distance, angle, quality);
			new Thread(()-> {
				if(!isSending) {
					sendMsg(msg);
				}
			}).start();
			return true;
		}else {
			return false;
		}
	}
	
	private synchronized void sendMsg(LidarMessage msg) {
		isSending=true;
		long start=System.currentTimeMillis();
		if(msgs==null) {
			msgs=new HashMap<Integer, LidarMessage>();
		}
		msgs.put((int)Math.round(msg.getDegree()), msg);
		synchronized (listeners) {
			listeners.forEach((lstnr)->lstnr.onMessageReceived(msg));
		}
		
		if(lastAngle>300&&msg.getDegree()<100) {
			synchronized (listeners) {
				for(LidarMessage msgToRemove:new ArrayList<LidarMessage>(msgs.values())) {
					if(System.currentTimeMillis()-msgToRemove.getTime()>1000) {
						this.msgs.remove((int)Math.round(msgToRemove.getDegree()));
					}
				}
				Map<Integer,LidarMessage> clMsgs=(Map<Integer, LidarMessage>) msgs.clone();
				listeners.forEach((lstnr)->lstnr.onFullTurnDone(clMsgs));
			}
		}
		lastAngle=msg.getDegree();
		isSending=false;
	}
}