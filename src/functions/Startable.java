package functions;

public interface Startable extends Stoppable {
	public void start();
	public boolean isStarted();
}
