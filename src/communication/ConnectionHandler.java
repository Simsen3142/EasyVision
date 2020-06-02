package communication;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import communication.serial.SerialHandler;

public abstract class ConnectionHandler<T> extends SuperConnHandler<T> {
	protected PrintWriter writer;
	
	public abstract T getConnected();
	protected abstract boolean disconnectOverride();
	public boolean disconnect() {
		if(disconnectOverride()) {
			super.triggerOnDisconnected(null);
			return true;
		}
		return false;
	}	
	public void setWriter(PrintWriter writer) {
		this.writer=writer;
	}
	
	public abstract void sendMessage(String text);
}
