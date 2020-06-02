package communication;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.fazecast.jSerialComm.SerialPort;

public abstract class TwoWayConnection<T> {
	protected T connectedPort;
	private List<Function<String,Void>> onReceives;
	
	public T getPort() {
		return connectedPort;
	}
	private void setConnectedPort(T connectedPort) {
		this.connectedPort = connectedPort;
	}

	public abstract InputStream getInputStream();
	public abstract OutputStream getOutputStream();

	public TwoWayConnection() {
		super();
	}
	
	protected abstract boolean connect(T port) throws Exception;
	
	public abstract void startReader();
	public abstract void stopReader();
	
	public abstract boolean disconnect();

	public boolean isConnected() {
		return connectedPort!=null;
	}
	
	
	public abstract boolean sendText(String text);
	
	/** 
	 *
	 */
	public void addOnReceive(Function<String,Void> onReceive) {
		if(onReceives==null)
			onReceives=new ArrayList<>();
		onReceives.add(onReceive);
	}
	
	public void removeOnReceive(Function<String,Void> onReceive) {
		if(onReceives==null)
			onReceives=new ArrayList<>();
		onReceives.remove(onReceive);
	}
	
	public void triggerOnReceives(String text) {
		if(onReceives!=null)
			onReceives.forEach((rcv)->{
				if(rcv!=null)
					rcv.apply(text);
			});
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectedPort == null) ? 0 : connectedPort.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwoWayConnection<?> other = (TwoWayConnection<?>) obj;
		if (connectedPort == null) {
			if (other.connectedPort != null)
				return false;
		} else if (!connectedPort.equals(other.connectedPort))
			return false;
		return true;
	}
}