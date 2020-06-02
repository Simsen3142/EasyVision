package communication;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class SuperConnHandler<T> {
	private boolean searching=false;
	private Function<T,Void> onConnectableDetected;
	private Function<List<T>,Void> onConnectableSearchDone;
	private List<ConnectionListener<T>> conListeners=new ArrayList<ConnectionListener<T>>();
	
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
	protected abstract boolean connectToOverride(T o, int id);
	
	
	public boolean connectTo(T o, int id) {
		if(connectToOverride(o,id)) {
			conListeners.forEach((conn)->conn.onConnected(o));
			return true;
		}
		return false;
	}
	public boolean connectTo(T o) {
		return connectTo(o,0);
	}

	protected void triggerOnDisconnected(T o) {
		conListeners.forEach((conn)->conn.onDisconnected(o));
	}

	public abstract String getConnectableName(T o);
	public String getConnectableSystemName(T o) {
		return getConnectableName(o);
	}
	public abstract boolean isConnected();
	
	public void searchConnectables() {
		searching=true;
	}
	
	public void addConListener(ConnectionListener<T> conListener) {
		conListeners.add(conListener);
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
