package communication;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MultiConnHandler<T> extends SuperConnHandler<T>{
	private List<ConnectionHandler<T>> conHandlers=new ArrayList<ConnectionHandler<T>>();
	protected abstract boolean disconnectFromOverride(T o);
	public boolean disconnectFrom(T o) {
		if(disconnectFromOverride(o)) {
			super.triggerOnDisconnected(o);
			return true;
		}
		return false;
	}
	public abstract void sendMessage(T o,String text);
	public abstract boolean isConnectedTo(T o);
	public abstract List<T> getConnected();
}
