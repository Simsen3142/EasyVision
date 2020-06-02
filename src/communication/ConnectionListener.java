package communication;

public interface ConnectionListener<T> {
	public void onConnected(T connected);
	public void onDisconnected(T disconnected);
}
