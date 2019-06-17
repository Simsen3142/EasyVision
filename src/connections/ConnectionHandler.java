package connections;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import arduino.ArduinoHandler;

public abstract class ConnectionHandler<T> {
	protected PrintWriter writer;
	private boolean searching=false;
	private Function<T,Void> onConnectableDetected;
	private Function<List<T>,Void> onConnectableSearchDone;
	
	/**
	 * @return the searching
	 */
	public boolean isSearching() {
		return searching;
	}

	/**
	 * @param onConnectableDetected the onConnectableDetected to set
	 */
	public void setOnConnectableDetected(Function<T, Void> onConnectableDetected) {
		this.onConnectableDetected = onConnectableDetected;
	}
	
	/**
	 * @return the onConnectableDetected
	 */
	protected Function<T, Void> getOnConnectableDetected() {
		return onConnectableDetected;
	}
	
	/**
	 * @return the onConnectableSearchDone
	 */
	protected Function<List<T>, Void> getOnConnectableSearchDone() {
		return onConnectableSearchDone;
	}

	/**
	 * @param onConnectableSearchDone the onConnectableSearchDone to set
	 */
	public void setOnConnectableSearchDone(Function<List<T>, Void> onConnectableSearchDone) {
		this.onConnectableSearchDone = onConnectableSearchDone;
	}

	public abstract List<T> getConnectables();
	
	public abstract T getConnected();

	public abstract boolean isConnected();

	public abstract boolean connectTo(T o);
	
	public abstract void disconnect();
	
	public void setWriter(PrintWriter writer) {
		this.writer=writer;
	}
	
	public abstract void sendMessage(String text);
	
	public abstract String getConnectableName(T c);
	
	public void searchConnectables() {
		searching=true;
	}
	
	protected void connectableFound(T c) {
		if(getOnConnectableDetected()!=null) {
			getOnConnectableDetected().apply(c);
		}
	}
	
	protected void connectableSearchDone() {
		searching=false;
		if(getOnConnectableSearchDone()!=null) {
			getOnConnectableSearchDone().apply(getConnectables());
		}
	}
}
